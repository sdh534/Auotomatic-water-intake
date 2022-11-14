package com.example.babycare;

public class DataItem2 {
    String data_water; //마신 물의 양
    String data_time; //시간
    int category; //카테고리

    public DataItem2(String data_time, String data_water, int category) {
        this.data_water = data_water;
        this.data_time= data_time;
        this.category = category;
    }



    public String getData_water() {
        return data_water;
    }

    public String getData_time() {
        return data_time;
    }

    public int getcategory() {
        return category;
    }

    public void setData_water(String data_water) {
        this.data_water = data_water;
    }

    public void setData_time(String data_time) {
        this.data_time = data_time;
    }

    public void setcategory(int category) {
        this.category = category;
    }


}