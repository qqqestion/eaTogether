package ru.blackbull.eatogether.validators;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.util.Date;

import ru.blackbull.eatogether.db.DatabaseModel;
import ru.blackbull.eatogether.db.User;

public class UserValidator implements BaseValidator {

    @Override
    public boolean isValid(DatabaseModel model) {
        User userModel = (User) model;
        return true;
    }
}





