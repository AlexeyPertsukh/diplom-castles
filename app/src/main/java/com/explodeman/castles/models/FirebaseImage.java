package com.explodeman.castles.models;

public class FirebaseImage implements INull {
    public String castleId;
    public String filename;
    public String copyright;
    public String description;

    public FirebaseImage() {
    }

    public FirebaseImage(String castleId, String filename, String copyright, String description) {
        this.castleId = castleId;
        this.filename = filename;
        this.copyright = copyright;
        this.description = description;
    }


    @Override
    public boolean isNull() {
        return false;
    }
}
