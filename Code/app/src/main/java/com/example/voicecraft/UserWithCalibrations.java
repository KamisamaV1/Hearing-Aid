package com.example.voicecraft;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class UserWithCalibrations {
    @Embedded
    public User user;

    @Relation(
            parentColumn = "userName",
            entityColumn = "userName"
    )
    public List<Calibration> calibrations ;
}
