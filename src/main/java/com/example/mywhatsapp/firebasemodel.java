package com.example.mywhatsapp;

public class firebasemodel {
    String name;
    String uid;
    String Image;
    String status;

    public firebasemodel(String name,String uid, String Image,String status) {
        this.name = name;
        this.uid=uid;
        this.Image=Image;
        this.status=status;

    }


    public firebasemodel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


