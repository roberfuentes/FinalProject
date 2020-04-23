package com.example.finalprojectapplication.Model;

import java.util.Date;

public class ChatMessage
{
    String fromUid;
    String messageText;
    Date sentAt;

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
}
