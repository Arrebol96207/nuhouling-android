package com.muhouling.app.data.api

import retrofit2.Response
import retrofit2.http.*

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null,
    val message: String? = null
)

data class OtpRequest(val phone: String)
data class OtpVerify(val phone: String, val code: String)
data class TokenResponse(val token: String)

data class SleepPolicyUpdate(
    val checkInDeadline: String? = null,
    val wakeTime: String? = null,
    val streakRewardThreshold: Int? = null
)

data class ContactInvite(val phone: String, val name: String? = null)
data class ContactConfirm(val token: String)

data class SleepSession(
    val id: String,
    val userId: String,
    val date: String,
    val checkInAt: String? = null,
    val wakeAt: String? = null,
    val earlyExitAt: String? = null,
    val status: String,
    val idempotencyKey: String,
    val createdAt: String
)

data class TodayData(
    val session: SleepSession? = null,
    val streak: Int = 0,
    val medalBalance: Int = 0
)

data class PenaltyResult(
    val type: String,
    val success: Boolean
)

data class EarlyExitData(
    val session: SleepSession,
    val penalty: PenaltyResult
)

data class WakeData(
    val session: SleepSession,
    val streak: Int,
    val medalBalance: Int,
    val medalAwarded: Boolean
)

data class EmergencyContact(
    val id: String,
    val userId: String,
    val phone: String,
    val name: String? = null,
    val status: String,
    val confirmToken: String? = null,
    val createdAt: String
)

interface ApiService {
    @POST("/auth/request-otp")
    suspend fun requestOtp(@Body request: OtpRequest): Response<ApiResponse<Unit>>

    @POST("/auth/verify-otp")
    suspend fun verifyOtp(@Body request: OtpVerify): Response<ApiResponse<TokenResponse>>

    @PUT("/me/sleep-policy")
    suspend fun updateSleepPolicy(
        @Header("Authorization") token: String,
        @Body policy: SleepPolicyUpdate
    ): Response<ApiResponse<Unit>>

    @GET("/contacts")
    suspend fun getContacts(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<EmergencyContact>>>

    @POST("/contacts/invite")
    suspend fun inviteContact(
        @Header("Authorization") token: String,
        @Body request: ContactInvite
    ): Response<ApiResponse<EmergencyContact>>

    @POST("/contacts/confirm")
    suspend fun confirmContact(@Body request: ContactConfirm): Response<ApiResponse<Unit>>

    @DELETE("/contacts/{id}")
    suspend fun deleteContact(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>

    @POST("/sleep/check-in")
    suspend fun checkIn(
        @Header("Authorization") token: String
    ): Response<ApiResponse<SleepSession>>

    @POST("/sleep/early-exit")
    suspend fun earlyExit(
        @Header("Authorization") token: String
    ): Response<ApiResponse<EarlyExitData>>

    @POST("/sleep/wake")
    suspend fun wake(
        @Header("Authorization") token: String
    ): Response<ApiResponse<WakeData>>

    @GET("/sleep/today")
    suspend fun getToday(
        @Header("Authorization") token: String
    ): Response<ApiResponse<TodayData>>

    @GET("/sleep/history")
    suspend fun getHistory(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int? = null
    ): Response<ApiResponse<List<SleepSession>>>
}
