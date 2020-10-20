package com.emoji.eatogether.validators;

import com.emoji.eatogether.db.DatabaseModel;

public class UserValidator implements BaseValidator {

    @Override
    public boolean isValid(DatabaseModel model) {
        return true;
    }
}
