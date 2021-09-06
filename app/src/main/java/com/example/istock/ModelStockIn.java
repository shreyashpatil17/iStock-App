package com.example.istock;

public class ModelStockIn {

    String Date_Time,Product_Name,Product_Price,Product_Quantity,Total_Price,Product_Barcode,PushId;

    ModelStockIn()
    {

    }

    public ModelStockIn(String date_Time, String product_Name, String product_Price, String product_Quantity, String total_Price, String product_Barcode,String pushId) {
        Date_Time = date_Time;
        Product_Name = product_Name;
        Product_Price = product_Price;
        Product_Quantity = product_Quantity;
        Total_Price = total_Price;
        Product_Barcode = product_Barcode;
        PushId = pushId;
    }

    public String getDate_Time() {
        return Date_Time;
    }

    public void setDate_Time(String date_Time) {
        Date_Time = date_Time;
    }

    public String getProduct_Name() {
        return Product_Name;
    }

    public void setProduct_Name(String product_Name) {
        Product_Name = product_Name;
    }

    public String getProduct_Price() {
        return Product_Price;
    }

    public void setProduct_Price(String product_Price) {
        Product_Price = product_Price;
    }

    public String getProduct_Quantity() {
        return Product_Quantity;
    }

    public void setProduct_Quantity(String product_Quantity) {
        Product_Quantity = product_Quantity;
    }

    public String getTotal_Price() {
        return Total_Price;
    }

    public void setTotal_Price(String total_Price) {
        Total_Price = total_Price;
    }

    public String getProduct_Barcode() {
        return Product_Barcode;
    }

    public void setProduct_Barcode(String product_Barcode) {
        Product_Barcode = product_Barcode;
    }

    public String getPushId() {
        return PushId;
    }

    public void setPushId(String pushId) {
        PushId = pushId;
    }
}
