package com.fs.jayrek.taskcompose.model.repository

import com.fs.jayrek.taskcompose.helper.StringConstants
import com.fs.jayrek.taskcompose.model.model.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val _auth: FirebaseAuth,
    private val _fireStore: FirebaseFirestore
) : IAuthRepository {

    override suspend fun signInWithEmail(
        email: String,
        password: String
    ): Resource<AuthResult> {
        return withContext(Dispatchers.IO) {
            safeApiCall {
                try {
                    val response = _auth.signInWithEmailAndPassword(email, password).await()
                    Resource.Success(response)
                } catch (e: Exception) {
                    Resource.Error(e.message.toString())
                }
            }
        }
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String
    ): Resource<AuthResult> {
        return withContext(Dispatchers.IO) {
            safeApiCall {
                try {
                    Resource.Success(
                        _auth.createUserWithEmailAndPassword(email, password)
                            .await()
                    )
                } catch (e: Exception) {
                    Resource.Error(e.message.toString())
                }
            }
        }
    }

    override suspend fun saveUserInfo(
        uid: String,
        fName: String,
        lName: String,
        email: String
    ): Boolean {
        return try {
            val user = User(fName, lName, email)
            _fireStore.collection(StringConstants.DOCUMENT_USER).document(uid).set(user)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getCurrentUserId(): String? = _auth.currentUser?.uid

    override suspend fun getUserInfo(
        uid: String
    ): Resource<DocumentSnapshot> {
        return withContext(Dispatchers.IO) {
            safeApiCall {
                try {
                    Resource.Success(
                        _fireStore.collection(StringConstants.DOCUMENT_USER)
                            .document(uid)
                            .get().await()
                    )
                } catch (e: Exception) {
                    Resource.Error(e.message.toString())
                }
            }
        }
    }

    override fun checkUser(): FirebaseUser? {
        return try {
            _auth.currentUser
        } catch (e: Exception) {
            null
        }
    }
}
