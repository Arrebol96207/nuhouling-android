package com.muhouling.app.data.repository

import com.muhouling.app.data.api.ApiService
import com.muhouling.app.data.api.OtpRequest
import com.muhouling.app.data.api.OtpVerify
import com.muhouling.app.data.local.PrefsStore
import kotlinx.coroutines.flow.first

class AuthRepository(
    private val api: ApiService,
    private val prefsStore: PrefsStore
) {
    suspend fun requestOtp(phone: String): Result<Unit> {
        return try {
            val response = api.requestOtp(OtpRequest(phone))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to send OTP"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyOtp(phone: String, code: String): Result<String> {
        return try {
            val response = api.verifyOtp(OtpVerify(phone, code))
            if (response.isSuccessful && response.body()?.success == true) {
                val token = response.body()?.data?.token
                    ?: return Result.failure(Exception("No token received"))
                prefsStore.saveToken(token)
                prefsStore.savePhone(phone)
                Result.success(token)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Invalid OTP"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getToken(): String? {
        return prefsStore.token.first()
    }

    suspend fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    suspend fun logout() {
        prefsStore.clearAll()
    }
}
