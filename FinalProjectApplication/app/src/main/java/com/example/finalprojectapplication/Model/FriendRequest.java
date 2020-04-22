package com.example.finalprojectapplication.Model;

public class FriendRequest
{
    String name;
    String status;
    String fromUid;


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
}
