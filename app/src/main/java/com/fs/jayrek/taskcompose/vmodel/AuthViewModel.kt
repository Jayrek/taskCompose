package com.fs.jayrek.taskcompose.vmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fs.jayrek.taskcompose.model.repository.IAuthRepository
import com.fs.jayrek.taskcompose.model.repository.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val _repo: IAuthRepository) : ViewModel() {

    private val _loader = MutableLiveData(false)
    private val _authStatus = MutableLiveData<Resource<AuthResult>>()
    private val _snapShot = MutableLiveData<Resource<DocumentSnapshot>>()
    private val _user = MutableLiveData<FirebaseUser?>()
    private val _isLogOut: MutableLiveData<Resource<Boolean>> = MutableLiveData()

    val loader: LiveData<Boolean> = _loader
    val authStatus: LiveData<Resource<AuthResult>> = _authStatus
    val snapShot: LiveData<Resource<DocumentSnapshot>> = _snapShot
    val user: LiveData<FirebaseUser?> = _user
    val isLogout: LiveData<Resource<Boolean>> = _isLogOut

    fun signIn(email: String, password: String) {
        _loader.postValue(true)
        viewModelScope.launch {
            val response = _repo.signInWithEmail(email, password)
            _authStatus.postValue(response)
        }
        _loader.postValue(false)
    }

    fun signUp(email: String, fName: String, lName: String, password: String) {
        _loader.postValue(true)
        viewModelScope.launch {
            val response = _repo.signUpWithEmail(email, password, fName, lName)
            _authStatus.postValue(response)
        }
        _loader.postValue(false)
    }

    fun getUser() {
        viewModelScope.launch {
            try {
                _snapShot.postValue(Resource.Loading())
                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                val snap = _repo.getUserInfo(uid)
                _snapShot.postValue(snap)
            } catch (e: Exception) {
                _snapShot.postValue(Resource.Error(e.message.toString()))
            }
        }
    }

    fun checkUserLogIn() {
        viewModelScope.launch {
            try {
                val repository = _repo.checkUser()
                _user.postValue(repository!!)
            } catch (e: Exception) {
                _user.postValue(null)
            }
        }
    }

    fun logOut() {
        viewModelScope.launch {
            try {
                _isLogOut.postValue(Resource.Loading())
                FirebaseAuth.getInstance().signOut()
                _isLogOut.postValue(Resource.Success(true))
            } catch (e: Exception) {
                _isLogOut.postValue(Resource.Error(e.message.toString()))
            }
        }
    }
}