package com.MonoCycleStudios.team.victorium.Connection;

import java.io.Serializable;

public class MonoPackage implements Serializable {

    private static final long serialVersionUID = 567890765678L;

    String typeOfObject;
    String descOfObject;
    Object obj;

    public String getTypeOfObject() {
        return typeOfObject;
    }
    public String getDescOfObject() {
        return descOfObject;
    }
    public Object getObj() {
        return obj;
    }

    public MonoPackage(String typeOfObject, String descOfObject, Object obj) {
        this.typeOfObject = typeOfObject;
        this.descOfObject = descOfObject;
        this.obj = obj;
    }

    public String fullToString(){
        return this.typeOfObject + " | " + this.descOfObject + " | " + this.obj;
    }
}
