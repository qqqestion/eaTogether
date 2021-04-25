package ru.blackbull.eatogether.repositories

import junit.framework.TestCase

import org.junit.Before
import ru.blackbull.eatogether.api.FakeGooglePlaceApiService

class PlaceRepositoryTest : TestCase() {

    lateinit var placeRepository: PlaceRepository

    @Before
    override fun setUp() {
        placeRepository = PlaceRepository(FakeGooglePlaceApiService())
    }

}