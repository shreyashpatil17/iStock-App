package com.example.istock;

import java.text.DecimalFormat;

public class ModelProduct {

    String Name,Manufacturer_Name,Distributor_Name,Distributor_Number,Price,Barcode_Number,Available_Quantity,ImageURL;
    DecimalFormat format = new DecimalFormat();

    ModelProduct()
    {

    }

    public ModelProduct(String name, String manufacturer_Name, String distributor_Name, String distributor_Number, String price, String barcode_Number, String available_Quantity, String imageURL) {
        Name = name;
        Manufacturer_Name = manufacturer_Name;
        Distributor_Name = distributor_Name;
        Distributor_Number = distributor_Number;
        Price = price;
        Barcode_Number = barcode_Number;
        Available_Quantity = available_Quantity;
        ImageURL = imageURL;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getManufacturer_Name() {
        return Manufacturer_Name;
    }

    public void setManufacturer_Name(String manufacturer_Name) {
        Manufacturer_Name = manufacturer_Name;
    }

    public String getDistributor_Name() {
        return Distributor_Name;
    }

    public void setDistributor_Name(String distributor_Name) {
        Distributor_Name = distributor_Name;
    }

    public String getDistributor_Number() {
        return Distributor_Number;
    }

    public void setDistributor_Number(String distributor_Number) {
        Distributor_Number = distributor_Number;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getBarcode_Number() {
        return Barcode_Number;
    }

    public void setBarcode_Number(String barcode_Number) {
        Barcode_Number = barcode_Number;
    }

    public String getAvailable_Quantity() {
        return Available_Quantity;
    }

    public void setAvailable_Quantity(String available_Quantity) {
        Available_Quantity = available_Quantity;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }
}
