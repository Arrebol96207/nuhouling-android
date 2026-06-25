package com.muhouling.app.data.repository

import com.muhouling.app.data.api.*
import com.muhouling.app.data.local.PrefsStore
import kotlinx.coroutines.flow.first

class ContactRepository(
    private val api: ApiService,
    private val prefsStore: PrefsStore
) {
    private suspend fun getAuthHeader(): String {
        val token = prefsStore.token.first()
            ?: throw Exception("Not authenticated")
        return "Bearer $token"
    }

    suspend fun getContacts(): Result<List<EmergencyContact>> {
        return try {
            val response = api.getContacts(getAuthHeader())
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to get contacts"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun inviteContact(phone: String, name: String?): Result<EmergencyContact> {
        return try {
            val response = api.inviteContact(getAuthHeader(), ContactInvite(phone, name))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Invite failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun confirmContact(token: String): Result<Unit> {
        return try {
            val response = api.confirmContact(ContactConfirm(token))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Confirm failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteContact(id: String): Result<Unit> {
        return try {
            val response = api.deleteContact(getAuthHeader(), id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Delete failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
