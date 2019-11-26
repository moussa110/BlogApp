package com.example.photosblogapp;

import java.sql.Timestamp;
import java.util.Date;

public class BlogPost {

    String desc,image_uri,thumb_uri,user_id;
    Date times_tamb;


    public BlogPost(){


    }
    public BlogPost(String desc, String image_uri, String thumb_uri, String user_id,Date times_tamb) {
        this.desc = desc;
        this.image_uri = image_uri;
        this.thumb_uri = thumb_uri;
        this.user_id = user_id;
        this.times_tamb=times_tamb;
    }



    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public String getThumb_uri() {
        return thumb_uri;
    }

    public void setThumb_uri(String thumb_uri) {
        this.thumb_uri = thumb_uri;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public Date getTimes_tamb() {
        return times_tamb;
    }

    public void setTimes_tamb(Date times_tamb) {
        this.times_tamb = times_tamb;
    }

}
