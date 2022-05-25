package com.fs.jayrek.taskcompose.vmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fs.jayrek.taskcompose.model.repository.AuthRepositoryImpl
import com.fs.jayrek.taskcompose.model.repository.IAuthRepository
import com.fs.jayrek.taskcompose.model.repository.Resource
import com.google.android.gms.tasks.Task
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
import io.mockk.mockk
import jp.co.tristone.akaso.MainCoroutineRule
import jp.co.tristone.akaso.getOrAwaitValueTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: AuthViewModel
    private lateinit var repository: IAuthRepository

    @Mock
    private lateinit var auth: FirebaseAuth

    @Mock
    private lateinit var firestore: FirebaseFirestore

//    @Mock
//    private lateinit var authResult: AuthResult

    @Mock
    private lateinit var resource: Resource<AuthResult>

    private var email = "test-email"
    private var password = "test-password"

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = AuthRepositoryImpl(auth, firestore)
        viewModel = AuthViewModel(repository)

    }

    @Test
    fun sampleTest() = runBlocking {
//        every { auth.signInWithEmailAndPassword(email, password) } returns mockTask()

        `when`(auth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask())
        viewModel.signIn(email, password)
        val result = viewModel.authStatus.getOrAwaitValueTest()
        assertThat(result).isNotNull()
    }

    private inline fun <reified T> mockTask(exception: Exception? = null): Task<T> {
        val task: Task<T> = mockk(relaxed = true)
        every { task.isComplete } returns true
        every { task.isSuccessful } returns false
        every { task.isCanceled } returns false
        every { task.exception } returns exception
        val relaxedValue: T = mockk(relaxed = true)
        every { task.result } returns relaxedValue
        return task
    }
}