package com.example.voicecraft;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CalibrationDao {

    @Insert
    void insertCalibration(Calibration calibration);

    @Query("SELECT * FROM Calibration WHERE calibration_date=(:calibrationDate) and userName = (:userName)")
    List<Calibration> getAllCalibrations(String calibrationDate, String userName);

    @Query("Select DISTINCT calibration_date from Calibration")
    List<String> getUniqueDates();

}

