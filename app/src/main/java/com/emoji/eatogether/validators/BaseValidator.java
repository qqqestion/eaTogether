package com.emoji.eatogether.validators;

import com.emoji.eatogether.db.DatabaseModel;

public interface BaseValidator {
    boolean isValid(DatabaseModel model);
}





