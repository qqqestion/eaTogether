package ru.blackbull.eatogether.repositories

import junit.framework.TestCase

import org.junit.Before

import org.junit.Assert.*
import ru.blackbull.eatogether.api.FakeFirebaseApi

class FirebaseRepositoryTest : TestCase() {

    lateinit var firebaseRepository: FirebaseRepository

    @Before
    override fun setUp() {
        firebaseRepository = FirebaseRepository(FakeFirebaseApi())
    }
}