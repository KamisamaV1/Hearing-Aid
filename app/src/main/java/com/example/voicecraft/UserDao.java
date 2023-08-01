package com.example.voicecraft;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Insert
    void registerUser(User user);

    @Query("SELECT * from User where userName=(:userName) and passWord=(:passWord)")
    User loginUser(String userName, String passWord);

    @Query("SELECT * FROM User where username = :userName")
    User getUser(String userName);

}

