package org.techtown.cpastone_design.DB;

public class UrlTableList {
    public int url_id;
    public String previous_url;
    public String destination_url;
    public int count;
    public String date;
    public int malicious;
    public String site_image;

    public UrlTableList(int url_id, String previous_url, String destination_url, int count, String date, int malicious, String site_image){
        this.url_id = url_id;
        this.previous_url = previous_url;
        this.destination_url = destination_url;
        this.count = count;
        this.date = date;
        this.malicious = malicious;
        this.site_image = site_image;
    }
}
