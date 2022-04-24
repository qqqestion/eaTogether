package ru.blackbull.data

import com.google.firebase.FirebaseNetworkException
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.blackbull.data.firebase.AuthManager
import ru.blackbull.data.firebase.UserCollection
import ru.blackbull.domain.AppCoroutineDispatchers
import ru.blackbull.domain.functional.error
import ru.blackbull.domain.functional.isFailure
import ru.blackbull.domain.functional.isSuccess
import ru.blackbull.domain.functional.value
import ru.blackbull.domain.usecases.NoInternetError

class AuthRepositoryTest {

    private val email = Faker.user.email
    private val password = Faker.user.password

    private val dispatchers = AppCoroutineDispatchers(
        Dispatchers.Main,
        Dispatchers.Main,
        Dispatchers.Main
    )

    @RelaxedMockK
    lateinit var sharedPreferencesDataSource: SharedPreferencesDataSource

    @RelaxedMockK
    lateinit var userCollection: UserCollection

    @RelaxedMockK
    lateinit var authManager: AuthManager

    private lateinit var authRepository: DefaultAuthRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        authRepository = DefaultAuthRepository(
            dispatchers,
            sharedPreferencesDataSource,
            userCollection,
            authManager
        )
    }

    @Test
    fun `should successfully sign in user`() = runTest {
        coEvery { authManager.signInWithEmailAndPassword(email, password) } just runs

        val result = authRepository.signInWithEmailAndPassword(email, password)

        assertTrue(result.isSuccess)
        assertEquals(Unit, result.value)
        coVerify {
            authManager.signInWithEmailAndPassword(email, password)
        }
    }

    @Test
    fun `should handle no internet exception`() = runTest {
        coEvery { authManager.signInWithEmailAndPassword(email, password) } throws mockk<FirebaseNetworkException>()

        val result = authRepository.signInWithEmailAndPassword(email, password)

        assertTrue(result.isFailure)
        assertEquals(NoInternetError, result.error)
        coVerify {
            authManager.signInWithEmailAndPassword(email, password)
        }
    }
}