package com.example.voicecraft;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


@Entity(tableName = "calibration_table",
        foreignKeys = @ForeignKey(entity = User.class, parentColumns = "userName", childColumns = "user_name"))
public class Calibration {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "frequency")
    private int[] frequency;

    @ColumnInfo(name = "loss_left_ear")
    private int[] lossLeftEar;

    @ColumnInfo(name = "loss_right_ear")
    private int[] lossRightEar;

    @ColumnInfo(name = "calibration_date")
    private int calibrationDate;

    @ColumnInfo(name = "user_name")
    private String userName;

    public int[] getFrequency() {
        return frequency;
    }

    public void setFrequency(int[] frequency) {
        this.frequency = frequency;
    }

    public int[] getLossLeftEar() {
        return lossLeftEar;
    }

    public void setLossLeftEar(int[] lossLeftEar) {
        this.lossLeftEar = lossLeftEar;
    }

    public int[] getLossRightEar() {
        return lossRightEar;
    }

    public void setLossRightEar(int[] lossRightEar) {
        this.lossRightEar = lossRightEar;
    }

    public int getCalibrationDate() {
        return calibrationDate;
    }

    public void setCalibrationDate(int calibrationDate) {
        this.calibrationDate = calibrationDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}