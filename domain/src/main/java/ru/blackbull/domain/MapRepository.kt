package ru.blackbull.domain

interface MapRepository {

    fun saveCuisines(cuisines: Set<String>)

    fun getSavedCuisines(): Set<String>

    fun removeSavedCuisines()
}