package com.example.finalprojectapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Data implements Parcelable
{

    private String name;
    private String url;
    private String type;
    private long size;



    public Data()
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

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }



    //Parcelling part
    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.name);
        dest.writeString(this.url);
        dest.writeString(this.type);
        dest.writeLong(this.size);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public Data createFromParcel(Parcel in){
            return new Data(in);
        }

        public Data[] newArray(int size){
            return new Data[size];
        }
    };

    public Data (Parcel in){
        this.name = in.readString();
        this.url = in.readString();
        this.size = in.readLong();
        this.type = in.readString();
    }


}


