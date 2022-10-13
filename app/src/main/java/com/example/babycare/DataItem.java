package com.example.babycare;

public class DataItem {
    String data_water; //마신 물의 양
    String data_time; //시간

    public DataItem(String data_time, String data_water) {
        this.data_water = data_water;
        this.data_time= data_time;
    }



    public String getData_water() {
        return data_water;
    }

    public String getData_time() {
        return data_time;
    }

    public void setData_water(String data_water) {
        this.data_water = data_water;
    }

    public void setData_time(String data_time) {
        this.data_time = data_time;
    }


}