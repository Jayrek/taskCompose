package com.fs.jayrek.taskcompose.vmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fs.jayrek.taskcompose.getOrAwaitValue
import com.fs.jayrek.taskcompose.helper.StringConstants.DOCUMENT_USER
import com.fs.jayrek.taskcompose.model.model.User
import com.fs.jayrek.taskcompose.model.repository.AuthRepositoryImpl
import com.fs.jayrek.taskcompose.model.repository.IAuthRepository
import com.fs.jayrek.taskcompose.model.repository.Resource
import com.google.android.gms.tasks.Task
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import jp.co.tristone.akaso.MainCoroutineRule
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

    @Mock
    private lateinit var auth: FirebaseAuth

    @Mock
    private lateinit var fireStore: FirebaseFirestore

    @Mock
    private lateinit var collectionReference: CollectionReference

    @Mock
    private lateinit var documentReference: DocumentReference

    @Mock
    private lateinit var firebaseUser: FirebaseUser

    private lateinit var viewModel: AuthViewModel
    private lateinit var repository: IAuthRepository

    private var uid = "test-uid"
    private var email = "test-email"
    private var password = "test-password"
    private var fName = "test-fName"
    private var lName = "test-lName"
    private val user = User(fName, lName)

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = AuthRepositoryImpl(auth, fireStore)
        viewModel = AuthViewModel(repository)
    }

    @Test
    fun `sign in check returns resource success`() = runBlocking {
        `when`(auth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask())

        viewModel.signIn(email, password)

        val result = viewModel.authStatus.getOrAwaitValue()
        val loader = viewModel.loader.getOrAwaitValue()
        assertThat(result is Resource.Success).isTrue()
        assertThat(loader).isFalse()
    }

    @Test
    fun `sign in check returns resource error exception`() = runBlocking {
        `when`(auth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask(Exception()))

        viewModel.signIn(email, password)

        val result = viewModel.authStatus.getOrAwaitValue()
        val loader = viewModel.loader.getOrAwaitValue()
        assertThat(result is Resource.Error).isTrue()
        assertThat(loader).isFalse()
    }

    @Test
    fun `sign up check returns resource success`() = runBlocking {
        `when`(auth.createUserWithEmailAndPassword(email, password)).thenReturn(mockTask())

        viewModel.signUp(email, password)

        val result = viewModel.authStatus.getOrAwaitValue()
        val loader = viewModel.loader.getOrAwaitValue()
        assertThat(result is Resource.Success).isTrue()
        assertThat(loader).isFalse()
    }

    @Test
    fun `save user information to fireStore`() = runBlocking {
        `when`(auth.currentUser).thenReturn(firebaseUser)
        `when`(auth.currentUser?.uid.toString()).thenReturn(uid)

        `when`(fireStore.collection(DOCUMENT_USER)).thenReturn(collectionReference)
        `when`(collectionReference.document(uid)).thenReturn(documentReference)
        `when`(documentReference.set(user)).thenReturn(mockTask())

        val result = viewModel.saveUserDocument(fName, lName, email)
        val loader = viewModel.loader.getOrAwaitValue()
        assertThat(result).isEqualTo(true)
        assertThat(loader).isFalse()
    }

    @Test
    fun `sign up check returns resource error exception`() = runBlocking {
        `when`(
            auth.createUserWithEmailAndPassword(
                email,
                password
            )
        ).thenReturn(mockTask(Exception()))

        viewModel.signUp(email, fName)

        val result = viewModel.authStatus.getOrAwaitValue()
        val loader = viewModel.loader.getOrAwaitValue()
        assertThat(result is Resource.Error).isTrue()
        assertThat(loader).isFalse()
    }

    @Test
    fun `check if has user`() = runBlocking {
        `when`(auth.currentUser).thenReturn(firebaseUser)

        viewModel.checkUserLogIn()
        val user = viewModel.user.getOrAwaitValue()
        val loader = viewModel.loader.getOrAwaitValue()
        assertThat(user).isNotNull()
        assertThat(loader).isFalse()
    }

    @Test
    fun `get user information`() = runBlocking {
        `when`(auth.currentUser).thenReturn(firebaseUser)
        `when`(auth.currentUser?.uid.toString()).thenReturn(uid)

        `when`(fireStore.collection(DOCUMENT_USER)).thenReturn(collectionReference)
        `when`(collectionReference.document(uid)).thenReturn(documentReference)
        `when`(documentReference.get()).thenReturn(mockTask())

        viewModel.getUser()
        val user = viewModel.snapShot.getOrAwaitValue()
        val loader = viewModel.loader.getOrAwaitValue()
        assertThat(user is Resource.Success).isTrue()
        assertThat(loader).isFalse()

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