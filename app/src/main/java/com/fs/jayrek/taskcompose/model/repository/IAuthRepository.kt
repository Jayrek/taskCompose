package com.fs.jayrek.taskcompose.model.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot

interface IAuthRepository {

    suspend fun signInWithEmail(
        email: String,
        password: String
    ): Resource<AuthResult>

    suspend fun signUpWithEmail(
        email: String,
        password: String, fName: String, lName: String
    ): Resource<AuthResult>

    suspend fun getUserInfo(
        uid: String
    ): Resource<DocumentSnapshot>

    fun checkUser(): FirebaseUser?
}
