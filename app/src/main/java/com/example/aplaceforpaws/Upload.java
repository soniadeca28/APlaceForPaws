package com.example.aplaceforpaws;

public class Upload {
    private String petName;
    private String petType;
    private String petAge;
    private String petDescription;
    private String downloadUrl;
    private String imgName;
    private String currentUser;
    private String address;

    public Upload() {

    }

    public Upload(String petName, String petType, String petAge, String description, String downloadUrl,String imgName,String currentUser,String address) {
        if (petName.trim().equals("")) {
            petName = "No Name";
        }
        this.petName = petName;
        this.petType = petType;
        this.petAge = petAge;
        this.petDescription = description;
        this.downloadUrl = downloadUrl;
        this.imgName = imgName;
        this.currentUser = currentUser;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getPetDescription() {
        return petDescription;
    }

    public void setPetDescription(String petDescription) {
        this.petDescription = petDescription;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public String getPetAge() {
        return petAge;
    }

    public void setPetAge(String petAge) {
        this.petAge = petAge;
    }

    public String getDescription() {
        return petDescription;
    }

    public void setDescription(String description) {
        this.petDescription = description;
    }

    public String getPetImage() {
        return downloadUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public void setPetImage(String petImage) {
        this.downloadUrl = petImage;
    }
}
