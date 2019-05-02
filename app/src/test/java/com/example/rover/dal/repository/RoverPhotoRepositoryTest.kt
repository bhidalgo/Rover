package com.example.rover.dal.repository

import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.example.rover.BaseTest
import com.example.rover.dal.api.CURIOSITY
import com.example.rover.dal.api.MarsRoverApi
import com.example.rover.dal.api.Rover
import com.example.rover.dal.api.model.RoverManifestResponse
import com.example.rover.dal.exception.ApiRequestLimitReachedException
import com.example.rover.dal.room.RoverPhotoDao
import com.example.rover.dal.room.RoverPhotoDatabase
import com.example.rover.model.RoverManifest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import retrofit2.Call
import retrofit2.Response

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
        stubDatabaseToReturn(expectedManifest)
        assertEquals(expectedManifest, request())
    }

    @Test
    fun `when fetching rover manifest and database has it then we should not query mars rover api`() {
        val expectedManifest = mock(RoverManifest::class.java)
        stubDatabaseToReturn(expectedManifest)
        request()
        verify(mMockMarsRoverApi, never()).getRoverManifest(anyString(), anyString())
    }

    @Test
    fun `when fetching rover manifest and database does not have it but user is not connected to network then we should return null`() {
        stubDatabaseToReturn(null)
        stubUserConnectedToNetwork(false)
        assertNull(request())
    }

    @Test
    fun `when fetching rover manifest and database does not have it then we should return mars api response`() {
        val expectedManifest = mock(RoverManifest::class.java)
        stubDatabaseToReturn(null)
        stubUserConnectedToNetwork(true)
        stubApiToReturn(expectedManifest)
        assertEquals(expectedManifest, request())
    }

    @Test
    fun `when fetching rover manifest from mars api and response is successful then we should insert the manifest into the database`() {
        val expectedManifest = mock(RoverManifest::class.java)
        stubDatabaseToReturn(null)
        stubUserConnectedToNetwork(true)
        stubApiToReturn(expectedManifest)
        request()
        verify(mMockRoverPhotoDatabase.roverPhotoDao()).insertManifest(expectedManifest)
    }

    @Test
    fun `when fetching rover manifest from mars api and response is null we should return null`() {
        stubDatabaseToReturn(null)
        stubUserConnectedToNetwork(true)
        stubApiToReturn(null)
        assertNull(request())
    }

    @Test
    fun `when fetching rover manifest from mars api and api limit reached we should throw exception`() {
        stubDatabaseToReturn(null)
        stubUserConnectedToNetwork(true)
        stubApiToReturn(null, false, API_REQUEST_LIMIT_CODE)
        try {
            request()
            fail()
        } catch(e: ApiRequestLimitReachedException) {}
    }

    @Test
    fun `when fetching rover manifest from mars api and request failed then we should return null`() {
        stubDatabaseToReturn(null)
        stubUserConnectedToNetwork(true)
        stubApiToReturn(null, false)
        assertNull(request())
    }

    @Test
    fun `when fetching rover manifest from mars api and response body is null then we should return null`() {
        stubDatabaseToReturn(null)
        stubUserConnectedToNetwork(true)
        stubApiToReturn(null, true)
        assertNull(request())
    }

    private fun stubDatabaseToReturn(value: RoverManifest?, @Rover forRover: String? = null) {
        val mockDao = mock(RoverPhotoDao::class.java)
        given(mockDao.getRoverManifest(forRover ?: anyString())).willReturn(value)
        given(mMockRoverPhotoDatabase.roverPhotoDao()).willReturn(mockDao)
    }

    private fun stubApiToReturn(value: RoverManifest?, isSuccess: Boolean = true, errorCode: Int = 400, @Rover forRover: String? = null) {
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

    private fun request(@Rover forRover: String = CURIOSITY): RoverManifest? = runBlocking(Dispatchers.Unconfined) {
        mSubject.getRoverManifest(forRover)
    }
}