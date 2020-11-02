package com.emoji.eatogether.validators;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.emoji.eatogether.db.DatabaseModel;
import com.emoji.eatogether.db.Party;
import com.emoji.eatogether.db.User;

import java.time.LocalDateTime;
import java.util.Date;

public class UserValidator implements BaseValidator {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean isValid(DatabaseModel model) {
        User userModel = (User) model;
        LocalDateTime now = LocalDateTime.now();
        if (userModel.getBirthday().compareTo(new Date(
                now.getYear() - 16,
                now.getMonthValue(),
                now.getDayOfMonth()).toString()) > 0) {
            return false;
        }
        return true;
    }
}





