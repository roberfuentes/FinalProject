package com.example.finalprojectapplication.Model;

public class Friend
{

    String name;
    String profilePictureUrl;
    String status;
    String uidFriend;


    public Friend()
    {
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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


    public String getUidFriend()
    {
        return uidFriend;
    }

    public void setUidFriend(String uidFriend)
    {
        this.uidFriend = uidFriend;
    }
}
