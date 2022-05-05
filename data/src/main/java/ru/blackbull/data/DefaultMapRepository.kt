package ru.blackbull.data

import ru.blackbull.domain.MapRepository
import javax.inject.Inject

class DefaultMapRepository @Inject internal constructor(
    private val sharedPreferencesDataSource: SharedPreferencesDataSource
) : MapRepository {

    override fun saveCuisines(cuisines: Set<String>) {
        sharedPreferencesDataSource.write(Preference.SelectedCuisines, cuisines)
    }

    override fun getSavedCuisines(): Set<String> =
        sharedPreferencesDataSource.read(Preference.SelectedCuisines)

    override fun removeSavedCuisines() =
        sharedPreferencesDataSource.remove(Preference.SelectedCuisines)
}