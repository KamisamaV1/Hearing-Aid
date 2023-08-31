package com.example.voicecraft;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CalibrationDao {

    @Insert
    void insertCalibration(Calibration calibration);

    @Query("SELECT * FROM calibration_table")
    List<Calibration> getAllCalibrations();

}

