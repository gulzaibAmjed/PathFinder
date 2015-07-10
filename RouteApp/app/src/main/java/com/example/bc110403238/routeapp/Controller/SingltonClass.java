package com.example.bc110403238.routeapp.Controller;

import android.app.Application;

import com.example.bc110403238.routeapp.Modal.MapDictionery;

/**
 * Created by zaib on 7/10/2015.
 */
public class SingltonClass extends Application {
    private static SingltonClass myObject;
    public String myValue = null;
    public String duration = null;
    public String start = null;
    public String distance = null ;
    public String end = null;
    MapDictionery mp = new MapDictionery();


    private SingltonClass() {


    }

    public static  SingltonClass getMyObject(){
        if(myObject == null){
            myObject = new SingltonClass();

        }
        return myObject;
    }
}
