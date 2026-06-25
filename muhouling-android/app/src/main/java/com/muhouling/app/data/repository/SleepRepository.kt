package com.muhouling.app.data.repository

import com.muhouling.app.data.api.*
import com.muhouling.app.data.local.PrefsStore
import kotlinx.coroutines.flow.first

class SleepRepository(
    private val api: ApiService,
    private val prefsStore: PrefsStore
) {
    private suspend fun getAuthHeader(): String {
        val token = prefsStore.token.first()
            ?: throw Exception("Not authenticated")
        return "Bearer $token"
    }

    suspend fun checkIn(): Result<SleepSession> {
        return try {
            val response = api.checkIn(getAuthHeader())
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Check-in failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun earlyExit(): Result<EarlyExitData> {
        return try {
            val response = api.earlyExit(getAuthHeader())
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Early exit failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun wake(): Result<WakeData> {
        return try {
            val response = api.wake(getAuthHeader())
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Wake failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getToday(): Result<TodayData> {
        return try {
            val response = api.getToday(getAuthHeader())
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to get today"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHistory(limit: Int = 30): Result<List<SleepSession>> {
        return try {
            val response = api.getHistory(getAuthHeader(), limit)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to get history"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePolicy(
        checkInDeadline: String? = null,
        wakeTime: String? = null,
        streakRewardThreshold: Int? = null
    ): Result<Unit> {
        return try {
            val response = api.updateSleepPolicy(
                getAuthHeader(),
                SleepPolicyUpdate(checkInDeadline, wakeTime, streakRewardThreshold)
            )
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Update failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
