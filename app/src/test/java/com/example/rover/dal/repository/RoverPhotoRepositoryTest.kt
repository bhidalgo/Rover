package com.example.rover.dal.repository

import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.example.rover.BaseTest
import com.example.rover.dal.api.CURIOSITY
import com.example.rover.dal.api.MarsRoverApi
import com.example.rover.dal.api.Rover
import com.example.rover.dal.api.model.RoverManifestResponse
import com.example.rover.dal.api.model.RoverPhotosResponse
import com.example.rover.dal.exception.ApiRequestLimitReachedException
import com.example.rover.dal.room.RoverPhotoDao
import com.example.rover.dal.room.RoverPhotoDatabase
import com.example.rover.model.RoverManifest
import com.example.rover.model.RoverPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import retrofit2.Call
import retrofit2.Response
import java.lang.IllegalStateException

class RoverPhotoRepositoryTest : BaseTest() {
    private lateinit var mSubject: RoverPhotoRepository
    private lateinit var mMockConnectivityManager: ConnectivityManager
    private lateinit var mMockMarsRoverApi: MarsRoverApi
    private lateinit var mMockRoverPhotoDatabase: RoverPhotoDatabase

    @Before
    fun setUp() {
        mMockConnectivityManager = mock(ConnectivityManager::class.java)
        mMockMarsRoverApi = mock(MarsRoverApi::class.java)
        mMockRoverPhotoDatabase = mock(RoverPhotoDatabase::class.java)
        mSubject = RoverPhotoRepository(mMockConnectivityManager, mMockMarsRoverApi, mMockRoverPhotoDatabase)
    }

    @Test
    fun `when fetching rover manifest and database has it then we should return database value`() {
        val expectedManifest = mock(RoverManifest::class.java)
        stubDatabaseToReturnManifest(expectedManifest)
        assertEquals(expectedManifest, requestManifest())
    }

    @Test
    fun `when fetching rover manifest and database has it then we should not query mars rover api`() {
        val expectedManifest = mock(RoverManifest::class.java)
        stubDatabaseToReturnManifest(expectedManifest)
        requestManifest()
        verify(mMockMarsRoverApi, never()).getRoverManifest(anyString(), anyString())
    }

    @Test
    fun `when fetching rover manifest from api and user is not connected to network then we should return null`() {
        stubDatabaseToReturnManifest(null)
        stubUserConnectedToNetwork(false)
        assertNull(requestManifest())
    }

    @Test
    fun `when fetching rover manifest from api then we should return mars api response value`() {
        val expectedManifest = mock(RoverManifest::class.java)
        stubDatabaseToReturnManifest(null)
        stubUserConnectedToNetwork(true)
        stubApiToReturnManifest(expectedManifest)
        assertEquals(expectedManifest, requestManifest())
    }

    @Test
    fun `when fetching rover manifest from mars api and response is successful then we should insert the manifest into the database`() {
        val expectedManifest = mock(RoverManifest::class.java)
        stubDatabaseToReturnManifest(null)
        stubUserConnectedToNetwork(true)
        stubApiToReturnManifest(expectedManifest)
        requestManifest()
        verify(mMockRoverPhotoDatabase.roverPhotoDao()).insertManifest(expectedManifest)
    }

    @Test
    fun `when fetching rover manifest from mars api and response is null we should return null`() {
        stubDatabaseToReturnManifest(null)
        stubUserConnectedToNetwork(true)
        stubApiToReturnManifest(null)
        assertNull(requestManifest())
    }

    @Test
    fun `when fetching rover manifest from mars api and api limit reached we should throw exception`() {
        stubDatabaseToReturnManifest(null)
        stubUserConnectedToNetwork(true)
        stubApiToReturnManifest(null, false, API_REQUEST_LIMIT_CODE)
        try {
            requestManifest()
            fail()
        } catch(e: ApiRequestLimitReachedException) {}
    }

    @Test
    fun `when fetching rover manifest from mars api and request failed then we should return null`() {
        stubDatabaseToReturnManifest(null)
        stubUserConnectedToNetwork(true)
        stubApiToReturnManifest(null, false)
        assertNull(requestManifest())
    }

    @Test
    fun `when fetching rover photo and database contains value then we should return database value`() {
        val expectedRoverPhotos: ArrayList<RoverPhoto> = arrayListOf(mock(RoverPhoto::class.java), mock(RoverPhoto::class.java), mock(RoverPhoto::class.java))
        stubDatabaseToReturnPhotos(expectedRoverPhotos)
        assertEquals(expectedRoverPhotos, requestRover())
    }

    @Test
    fun `when fetching rover photo and database contains value then we should not request photo from api`() {
        val expectedRoverPhotos: ArrayList<RoverPhoto> = arrayListOf(mock(RoverPhoto::class.java), mock(RoverPhoto::class.java), mock(RoverPhoto::class.java))
        stubDatabaseToReturnPhotos(expectedRoverPhotos)
        requestRover()
        verify(mMockMarsRoverApi, never()).getRoverPhotos(anyString(), anyInt(), anyString(), anyInt())
    }

    @Test
    fun `when fetching rover photo from api and user has no connection then we should return null`() {
        stubDatabaseToReturnPhotos(null)
        stubUserConnectedToNetwork(false)
        assertNull(requestRover())
    }

    @Test
    fun `when fetching rover photo from api then we should return api response value`() {
        val expectedRoverPhotos: ArrayList<RoverPhoto> = arrayListOf(mock(RoverPhoto::class.java), mock(RoverPhoto::class.java), mock(RoverPhoto::class.java))
        stubDatabaseToReturnPhotos(null)
        stubUserConnectedToNetwork(true)
        stubApiToReturnRoverPhotos(expectedRoverPhotos)
        assertEquals(expectedRoverPhotos, requestRover())
    }

    @Test
    fun `when fetching rover photo from api and response is successful then we should store the photos into the database`() {
        val expectedRoverPhotos: ArrayList<RoverPhoto> = arrayListOf(mock(RoverPhoto::class.java), mock(RoverPhoto::class.java), mock(RoverPhoto::class.java))
        stubDatabaseToReturnPhotos(null)
        stubUserConnectedToNetwork(true)
        stubApiToReturnRoverPhotos(expectedRoverPhotos)
        requestRover()
        verify(mMockRoverPhotoDatabase.roverPhotoDao()).insertAllPhotos(expectedRoverPhotos)
    }

    @Test
    fun `when fetching rover photo from api and response is null then we should return null`() {
        stubDatabaseToReturnPhotos(null)
        stubUserConnectedToNetwork(true)
        stubApiToReturnRoverPhotos(null, true)
        assertNull(requestRover())
    }

    @Test
    fun `when fetching rover photo from api and api limit reached then we should throw exception`() {
        stubDatabaseToReturnPhotos(null)
        stubUserConnectedToNetwork(true)
        stubApiToReturnRoverPhotos(null, false, API_REQUEST_LIMIT_CODE)
        try {
            requestRover()
            fail()
        } catch(e: ApiRequestLimitReachedException) { }
    }

    @Test
    fun `when fetching rover photo from api and request failed then we should return null`() {
        stubDatabaseToReturnPhotos(null)
        stubUserConnectedToNetwork(true)
        stubApiToReturnRoverPhotos(null, false)
        assertNull(requestRover())
    }

    private fun stubDatabaseToReturnPhotos(value: ArrayList<RoverPhoto>?, @Rover forRover: String? = null) {
        val mockDao = mock(RoverPhotoDao::class.java)
        given(mockDao.getRoverPhotosBySol(forRover ?: anyString(), anyInt())).willReturn(value)
        given(mMockRoverPhotoDatabase.roverPhotoDao()).willReturn(mockDao)
    }

    private fun stubApiToReturnRoverPhotos(value: ArrayList<RoverPhoto>?, isSuccess: Boolean = true, errorCode: Int = 400, @Rover forRover: String? = null) {
        val expectedPhotoResponse = value?.let {
            RoverPhotosResponse(it)
        }

        val testResponse = if(isSuccess) {
            Response.success(expectedPhotoResponse)
        } else {
            Response.error(errorCode, ResponseBody.create(MediaType.get("application/json"), "{}"))
        }

        val mockApiCall: Call<RoverPhotosResponse> = mock(Call::class.java).cast()

        given(mockApiCall.execute()).willReturn(testResponse)
        given(mMockMarsRoverApi.getRoverPhotos(forRover ?: anyString(), anyInt(), anyString(), anyInt())).willReturn(mockApiCall)
    }

    private fun stubDatabaseToReturnManifest(value: RoverManifest?, @Rover forRover: String? = null) {
        val mockDao = mock(RoverPhotoDao::class.java)
        given(mockDao.getRoverManifest(forRover ?: anyString())).willReturn(value)
        given(mMockRoverPhotoDatabase.roverPhotoDao()).willReturn(mockDao)
    }

    private fun stubApiToReturnManifest(value: RoverManifest?, isSuccess: Boolean = true, errorCode: Int = 400, @Rover forRover: String? = null) {
        val expectedManifestResponse = value?.let {
            RoverManifestResponse(value)
        }

        val testResponse = if(isSuccess) {
            Response.success(expectedManifestResponse)
        } else {
            Response.error(errorCode, ResponseBody.create(MediaType.get("application/json"), "{}"))
        }

        val mockApiCall: Call<RoverManifestResponse> = mock(Call::class.java).cast()

        given(mockApiCall.execute()).willReturn(testResponse)
        given(mMockMarsRoverApi.getRoverManifest(forRover ?: anyString(), anyString())).willReturn(mockApiCall)
    }

    private fun stubUserConnectedToNetwork(connected: Boolean) {
        val mockActiveNetworkInfo = mock(NetworkInfo::class.java)
        given(mockActiveNetworkInfo.isConnected).willReturn(connected)
        given(mMockConnectivityManager.activeNetworkInfo).willReturn(mockActiveNetworkInfo)
    }

    private fun requestManifest(@Rover forRover: String = CURIOSITY): RoverManifest? = runBlocking(Dispatchers.Unconfined) {
        mSubject.getRoverManifest(forRover)
    }

    private fun requestRover(@Rover forRover: String = CURIOSITY, sol: Int = 0): ArrayList<RoverPhoto>? = runBlocking(Dispatchers.Unconfined) {
        mSubject.getRoverPhotos(forRover, sol)
    }
}