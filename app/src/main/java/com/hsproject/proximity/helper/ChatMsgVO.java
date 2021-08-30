package com.hsproject.proximity.helper;

import androidx.annotation.NonNull;

public class ChatMsgVO {
    private int uid;
    private String name;
    private String crt_dt;
    private String content;

    public ChatMsgVO() {

    }

    public ChatMsgVO(int uid, String name, String crt_dt, String content) {
        this.uid = uid;
        this.name = name;
        this.crt_dt = crt_dt;
        this.content = content;
    }

    public int getUid() { return uid; }
    public void setUid(int uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCrt_dt() { return  crt_dt; }
    public void setCrt_dt(String crt_dt) { this.crt_dt = crt_dt; }

    public String getContent() { return  content; }
    public void setContent(String content) { this.content = content; }

    @NonNull
    @Override
    public String toString() {
        return "ChatMsgVO{" +
                "uid='" + uid + '\'' +
                "name='" + name + '\'' +
                ", crt_dt='" + crt_dt + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
