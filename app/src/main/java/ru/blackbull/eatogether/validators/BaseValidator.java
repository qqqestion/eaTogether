package ru.blackbull.eatogether.validators;


import ru.blackbull.eatogether.db.DatabaseModel;

public interface BaseValidator {
    boolean isValid(DatabaseModel model);
}





