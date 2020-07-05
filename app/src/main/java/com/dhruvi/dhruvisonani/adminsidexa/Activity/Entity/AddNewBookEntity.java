package com.dhruvi.dhruvisonani.adminsidexa.Activity.Entity;
public class AddNewBookEntity {

    private  String str_imageId;
    private String currentFirebaseUser;
    private String str_bookName;
    private long str_bookPrice;

    public AddNewBookEntity() {
    }

    public AddNewBookEntity(String str_imageId, String str_bookName, int str_bookPrice, String currentFirebaseUser) {
        this.str_imageId = str_imageId;
        this.str_bookName = str_bookName;
        this.str_bookPrice = str_bookPrice;
        this.currentFirebaseUser = currentFirebaseUser;
    }

    public String getStr_imageId() {
        return str_imageId;
    }

    public void setStr_imageId(String str_imageId) {
        this.str_imageId = str_imageId;
    }

    public String getStr_bookName() {
        return str_bookName;
    }

    public void setStr_bookName(String str_bookName) {
        this.str_bookName = str_bookName;
    }

    public String getCurrentFirebaseUser() {
        return currentFirebaseUser;
    }

    public void setCurrentFirebaseUser(String currentFirebaseUser){
        this.currentFirebaseUser = currentFirebaseUser;
    }
    public long getStr_bookPrice() {
        return str_bookPrice;
    }

    public void setStr_bookPrice(long str_bookPrice) {
        this.str_bookPrice = str_bookPrice;
    }
}
