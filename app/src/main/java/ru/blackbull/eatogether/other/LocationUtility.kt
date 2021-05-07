package ru.blackbull.eatogether.other

import android.Manifest
import android.content.Context
import pub.devrel.easypermissions.EasyPermissions

object LocationUtility {

    /**
     * Функция для проверки разрешения на использования местоположения
     *
     * @param context
     */
    fun hasLocationPermission(context: Context) = EasyPermissions.hasPermissions(
        context ,
        Manifest.permission.ACCESS_FINE_LOCATION ,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
}