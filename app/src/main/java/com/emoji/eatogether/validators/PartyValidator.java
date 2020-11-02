package com.emoji.eatogether.validators;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.emoji.eatogether.db.DatabaseModel;
import com.emoji.eatogether.db.Party;

import java.time.LocalDateTime;

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
