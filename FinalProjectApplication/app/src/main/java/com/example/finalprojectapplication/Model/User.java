package com.example.finalprojectapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class User
{

    String name;

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

    //Parcelling part

    /*public User(Parcel in){
        this.name = in.readString();
    }
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>(){
        public User createFromParcel(Parcel in){
            return new User(in);
        }
        public User[] newArray(int size){
            return new User[size];
        }
    };*/

    @Override
    public String toString()
    {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
