package com.cgitsoft.convertgeneration.Notification;

public class Data {
    String Message,Title,id;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public Data() {
    }

    public Data(String message, String title,String id) {
        Message = message;
        Title = title;
        this.id=id;
    }
}
