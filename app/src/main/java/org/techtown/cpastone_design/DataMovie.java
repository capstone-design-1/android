package org.techtown.cpastone_design;

import android.media.Image;

import org.json.JSONException;
import org.json.JSONObject;

public class DataMovie {
    String malicious;
    String title;

    public DataMovie(String title, JSONObject res) throws JSONException {
        //this.image = image;
        this.title = title;
        int temp = res.getInt("is_malicious");
        if(temp == 0){
            this.malicious = "무해함";
        }
        else{
            this.malicious = "유해함";
        }
    }
/*
    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
*/
    public String getMalicious(){
        return malicious;
    }

    public void  setMalicious(String malicious){
        this.malicious = malicious;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}