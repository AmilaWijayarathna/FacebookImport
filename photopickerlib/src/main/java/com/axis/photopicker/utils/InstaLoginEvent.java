package com.axis.photopicker.utils;

/**
 * Created by Sithmal on 1/19/16.
 */
public class InstaLoginEvent {

    public boolean isSuccess = false;
    String token = "";


    public InstaLoginEvent(boolean isSuccess, String token) {
        this.isSuccess = isSuccess;
        this.token = token;
    }

    @Override
    public String toString() {
        return "InstaLoginEvent{" +
                "isSuccess=" + isSuccess +
                ", token='" + token + '\'' +
                '}';
    }
}
