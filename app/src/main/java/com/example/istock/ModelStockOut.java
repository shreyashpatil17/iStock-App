package com.example.istock;

public class ModelStockOut {
    String Date_Time,Product_Name,Product_Price,Product_Quantity,Price,Product_Barcode,Customer_Name,Customer_Number,PushId;

    ModelStockOut()
    {

    }

    public ModelStockOut(String date_Time, String product_Name, String product_Price, String product_Quantity, String price, String product_Barcode, String customer_Name, String customer_Number, String pushId) {
        Date_Time = date_Time;
        Product_Name = product_Name;
        Product_Price = product_Price;
        Product_Quantity = product_Quantity;
        Price = price;
        Product_Barcode = product_Barcode;
        Customer_Name = customer_Name;
        Customer_Number = customer_Number;
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

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getProduct_Barcode() {
        return Product_Barcode;
    }

    public void setProduct_Barcode(String product_Barcode) {
        Product_Barcode = product_Barcode;
    }

    public String getCustomer_Name() {
        return Customer_Name;
    }

    public void setCustomer_Name(String customer_Name) {
        Customer_Name = customer_Name;
    }

    public String getCustomer_Number() {
        return Customer_Number;
    }

    public void setCustomer_Number(String customer_Number) {
        Customer_Number = customer_Number;
    }

    public String getPushId() {
        return PushId;
    }

    public void setPushId(String pushId) {
        PushId = pushId;
    }
}
