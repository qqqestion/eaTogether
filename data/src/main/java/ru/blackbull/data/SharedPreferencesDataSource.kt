package ru.blackbull.data

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

internal sealed class Preference<T : Any>(
    val key: String,
    val default: T
) {

    object RegistrationComplete : Preference<Boolean>("registration_complete", false)
    object SelectedCuisines : Preference<Set<String>>("selected_cuisines", emptySet())
}

internal class SharedPreferencesDataSource @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun write(preference: Preference<String>, value: String) {
        sharedPreferences.edit {
            putString(preference.key, value)
        }
    }

    fun write(preference: Preference<Long>, value: Long) {
        sharedPreferences.edit {
            putLong(preference.key, value)
        }
    }

    fun write(preference: Preference<Int>, value: Int) {
        sharedPreferences.edit {
            putInt(preference.key, value)
        }
    }

    fun write(preference: Preference<Boolean>, value: Boolean) {
        sharedPreferences.edit {
            putBoolean(preference.key, value)
        }
    }

    fun write(preference: Preference<Float>, value: Float) {
        sharedPreferences.edit {
            putFloat(preference.key, value)
        }
    }

    fun write(preference: Preference<Set<String>>, value: Set<String>) {
        sharedPreferences.edit {
            putStringSet(preference.key, value)
        }
    }

    fun read(preference: Preference<String>) =
        sharedPreferences.getString(preference.key, preference.default)

    fun read(preference: Preference<Long>) =
        sharedPreferences.getLong(preference.key, preference.default)

    fun read(preference: Preference<Int>) =
        sharedPreferences.getInt(preference.key, preference.default)

    fun read(preference: Preference<Float>) =
        sharedPreferences.getFloat(preference.key, preference.default)

    fun read(preference: Preference<Boolean>) =
        sharedPreferences.getBoolean(preference.key, preference.default)

    fun read(preference: Preference<Set<String>>): Set<String> =
        sharedPreferences.getStringSet(preference.key, preference.default) ?: preference.default

    fun remove(preference: Preference<*>) = sharedPreferences.edit { remove(preference.key) }
}