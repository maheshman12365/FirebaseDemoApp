package com.app.firebasedemoapp;

public class User {
    int uid;
    String title;
    boolean ischecked;

    public User(int i, String s, boolean b) {
        uid = i;
        title = s;
        ischecked = b;
    }

    public User() {
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isIschecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }

}
