package com.example.aplaceforpaws;

public class Upload {
    private String petName;
    private String petType;
    private String petAge;
    private String description;
    private String petImage;

    public Upload() {

    }

    public Upload(String petName, String petType, String petAge, String description, String petImage) {
        if (petName.trim().equals("")) {
            petName = "No Name";
        }
        this.petName = petName;
        this.petType = petType;
        this.petAge = petAge;
        this.description = description;
        this.petImage = petImage;
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
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPetImage() {
        return petImage;
    }

    public void setPetImage(String petImage) {
        this.petImage = petImage;
    }
}
