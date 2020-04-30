package com.example.finalprojectapplication.Model;

import java.util.Date;

public class ChatMessage
{
    String fromUid;
    String messageText;
    Date sentAt;
    Boolean isFile;
    String url;
    String type;

    public ChatMessage()
    {
    }

    public String getFromUid()
    {
        return fromUid;
    }

    public void setFromUid(String fromUid)
    {
        this.fromUid = fromUid;
    }

    public String getMessageText()
    {
        return messageText;
    }

    public void setMessageText(String messageText)
    {
        this.messageText = messageText;
    }

    public Date getSentAt()
    {
        return sentAt;
    }

    public void setSentAt(Date sentAt)
    {
        this.sentAt = sentAt;
    }

    public Boolean getIsFile()
    {
        return isFile;
    }

    public void setIsFile(Boolean isFile)
    {
        this.isFile = isFile;
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
}
