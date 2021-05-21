package com.example.nasaapplication;

public class MyApod {

    String myDate;
    String myTitle;
    String myURL;

    public MyApod(String theDate, String theTitle, String theURL){
        myDate = theDate;
        myTitle = theTitle;
        myURL = theURL;
    }

    public String getMyDate() {
        return myDate;
    }

    public String getMyTitle(){
        return myTitle;
    }

    public String getMyURL(){
        return myURL;
    }
}
