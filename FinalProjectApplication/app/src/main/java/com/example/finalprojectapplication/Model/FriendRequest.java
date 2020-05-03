package com.example.finalprojectapplication.Model;

import java.lang.reflect.Array;
import java.util.List;

public class FriendRequest
{
    String name;
    String status;
    String fromUid;
    String profilePictureUrl;


    public FriendRequest(){

    }


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    /*public List<String> getStatus()
    {
        return status;
    }

    public void setStatus(List<String> status)
    {
        this.status = status;
    }*/

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getFromUid()
    {
        return fromUid;
    }

    public void setFromUid(String fromUid)
    {
        this.fromUid = fromUid;
    }

    public String getProfilePictureUrl()
    {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl)
    {
        this.profilePictureUrl = profilePictureUrl;
    }
}
