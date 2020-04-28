package com.example.finalprojectapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class User
{

    String name;
    String email;
    String password;
    String location;
    String age;
    String profilePictureUrl;
    String uid;
    String status;
    Boolean isGoogleSign;

    public User(){

    }
    public User(String name){
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getLocation()
    {
        return location;
    }


    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getProfilePictureUrl()
    {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl)
    {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public Boolean getIsGoogleSign()
    {
        return isGoogleSign;
    }

    public void setIsGoogleSign(Boolean isGoogleSign)
    {
        this.isGoogleSign = isGoogleSign;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
