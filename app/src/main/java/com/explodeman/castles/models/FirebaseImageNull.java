package com.explodeman.castles.models;

public class FirebaseImageNull extends FirebaseImage{

    public static FirebaseImageNull instance;

    private FirebaseImageNull() {
    }

    public static FirebaseImageNull getInstance() {
        if(instance == null) {
            instance = new FirebaseImageNull();
        }
        return instance;
    }

    @Override
    public boolean isNull() {
        return true;
    }
}
