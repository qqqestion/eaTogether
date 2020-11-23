package ru.blackbull.eatogether.validators;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;

import ru.blackbull.eatogether.db.DatabaseModel;
import ru.blackbull.eatogether.db.Party;

public class PartyValidator implements BaseValidator {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean isValid(DatabaseModel model) {
        Party partyModel = (Party) model;
        if (partyModel.getTime().compareTo(LocalDateTime.now().toString()) < 0) {
            return false;
        }
        return true;
    }
}
