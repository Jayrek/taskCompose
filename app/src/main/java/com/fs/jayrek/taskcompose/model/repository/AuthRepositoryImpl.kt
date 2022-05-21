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
                Resource.Success(
                    _auth.signInWithEmailAndPassword(email, password).await()
                )
            }
        }
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String, fName: String, lName: String
    ): Resource<AuthResult> {
        return withContext(Dispatchers.IO) {
            safeApiCall {
                val auth =
                    _auth.createUserWithEmailAndPassword(email, password)
                        .await()

                val user = User(fName, lName)
                _fireStore.collection(StringConstants.DOCUMENT_USER)
                    .document(auth.user!!.uid)
                    .set(user)
                    .await()
                Resource.Success(auth)
            }
        }
    }

    override suspend fun getUserInfo(
        uid: String
    ): Resource<DocumentSnapshot> {
        return withContext(Dispatchers.IO) {
            safeApiCall {
                Resource.Success(
                    _fireStore.collection(StringConstants.DOCUMENT_USER)
                        .document(uid)
                        .get().await()
                )
            }
        }
    }

    override fun checkUser(): FirebaseUser? = _auth.currentUser
}
