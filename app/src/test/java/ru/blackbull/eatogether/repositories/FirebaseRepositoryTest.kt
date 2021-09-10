package ru.blackbull.eatogether.repositories

import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import ru.blackbull.data.FirebaseRepository
import ru.blackbull.data.FirebaseApi
import ru.blackbull.data.models.firebase.User
import ru.blackbull.domain.Resource
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FirebaseRepositoryTest {

    private val api: FirebaseApi = mockk()

    private val repository: FirebaseRepository = FirebaseRepository(api)

    private val currentUserId = "current_user_id"

    @BeforeEach
    fun setUp() {
        clearMocks(api)
    }

    @Test
    fun `should return friend list of current user`() {
        var currentUser = User(
            currentUserId ,
            friendList = mutableListOf("1" , "2" , "3")
        )
        every { api.getCurrentUserId() } returns currentUserId
        coEvery { api.getUser(currentUserId) } returns currentUser
                coEvery { api.getFriendList(currentUser) } returns listOf(
            User(id = "1") , User(id = "2") , User(id = "3")
        )

        val response = runBlocking { repository.getFriendList() }
        assertThat(response).isInstanceOf(Resource.Success::class.java)
//        assertThat(response.data?.size).isEqualTo(3)
    }

    //    @Test
//    fun `should find parties by given place id`() {
//        coEvery { api.searchPartyByPlace("1") } returns listOf(
//            Party(
//                placeId = "1" ,
//                users = mutableListOf("1" , "2")
//            )
//        )
//        coEvery { api.getUser(any()) } returns User()
//
//        val response = runBlocking { repository.searchPartyByPlace("1") }
//        assertThat(response).isInstanceOf(Resource.Success::class.java)
//        assertThat(response.data?.size).isEqualTo(1)
//        val party = response.data!!.first()
//        assertThat(party.users.size).isEqualTo(2)
//        assertThat(party.placeId).isEqualTo("1")
//        coVerify(exactly = 1) { api.searchPartyByPlace("1") }
//        coVerify(exactly = 2) { api.getUser(any()) }
//    }
//
//    @Test
//    fun `should return empty list of parties when no placeId found`() {
//        coEvery { api.searchPartyByPlace("1") } returns listOf()
//
//        val response = runBlocking { repository.searchPartyByPlace("1") }
//        assertThat(response).isInstanceOf(Resource.Success::class.java)
//        assertThat(response.data).isEmpty()
//    }
//
//    @Test
//    fun `should call addParty only once`() {
//        runBlocking { repository.addParty(Party()) }
//        coVerify(exactly = 1) { api.addParty(Party()) }
//    }

    @Test
    fun `should call signOut only once`() {
        every { api.signOut() } returns Unit
        runBlocking { repository.signOut() }
        coVerify(exactly = 1) { api.signOut() }
    }

    @Test
    fun `should update user images`() {
        val user = User(

        )
    }
}