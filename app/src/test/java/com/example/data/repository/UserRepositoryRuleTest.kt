package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class UserRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveProfile_authenticatedUser_savesSuccessfully() = runBlocking {
        val uniqueEmail = "alice_${UUID.randomUUID().toString().take(8)}@ghanagrantfinder.org"
        val uid = signInTestUser(uniqueEmail)
        val repository = UserRepository(firestore)

        val profile = UserProfile(
            userId = uid,
            fullName = "Alice Mensah",
            region = "Greater Accra",
            userType = "Entrepreneur",
            occupation = "AgriTech Founder",
            interests = listOf("Agriculture", "Business grants")
        )

        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveProfile(profile)
        }

        val fetched = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getProfile(uid)
        }

        assertNotNull(fetched)
        assertEquals("Alice Mensah", fetched?.fullName)
        assertEquals("Greater Accra", fetched?.region)
    }

    @Test
    fun observeProfile_authenticatedUser_emitsProfile() = runBlocking {
        val uniqueEmail = "alice_${UUID.randomUUID().toString().take(8)}@ghanagrantfinder.org"
        val uid = signInTestUser(uniqueEmail)
        val repository = UserRepository(firestore)

        val profile = UserProfile(
            userId = uid,
            fullName = "Alice Mensah Realtime",
            region = "Ashanti",
            userType = "Farmer"
        )

        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveProfile(profile)
        }

        val emitted = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.observeProfile(uid).first { it?.fullName == "Alice Mensah Realtime" }
        }

        assertNotNull(emitted)
        assertEquals("Ashanti", emitted?.region)
    }

    @Test
    fun getProfile_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceEmail = "alice_${UUID.randomUUID().toString().take(8)}@ghanagrantfinder.org"
        val aliceUid = signInTestUser(aliceEmail)
        val aliceRepo = UserRepository(firestore)
        aliceRepo.saveProfile(
            UserProfile(
                userId = aliceUid,
                fullName = "Alice Confidential",
                region = "Central",
                userType = "Researcher"
            )
        )

        val bobEmail = "bob_${UUID.randomUUID().toString().take(8)}@ghanagrantfinder.org"
        signInTestUser(bobEmail)
        val bobRepo = UserRepository(firestore)

        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.getProfile(aliceUid)
        }
        assertTrue(result == null)
    }

    @Test
    fun observeProfile_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = UserRepository(firestore)

        var caughtException: Exception? = null
        try {
            withTimeout(DEFAULT_TIMEOUT_MS) {
                repository.observeProfile("some_user_id").first()
            }
        } catch (e: Exception) {
            caughtException = e
        }
        assertNotNull(caughtException)
        val firestoreException = checkNotNull(caughtException?.unwrapFirestoreException()) {
            "Expected nested FirebaseFirestoreException, got $caughtException"
        }
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException.code)
    }

    private companion object {
        const val DEFAULT_TIMEOUT_MS = 5000L
    }
}
