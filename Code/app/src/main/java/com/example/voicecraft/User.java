package com.example.voicecraft;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "User")
public class User {
    @ColumnInfo(name = "fullName")
    private String fullName;
    @ColumnInfo(name = "Email")
    private String Email;
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "userName")
    private String userName;
    @ColumnInfo(name = "passWord")
    private String passWord;

    @ColumnInfo(name = "Gender")
    private String Gender;

    @ColumnInfo(name = "Address")
    private String Address;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getUserName() { return userName; }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        this.Gender = gender;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

}
