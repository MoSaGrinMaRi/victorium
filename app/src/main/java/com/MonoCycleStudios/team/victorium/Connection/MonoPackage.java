package com.MonoCycleStudios.team.victorium.Connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MonoPackage implements Serializable {

    private static final long serialVersionUID = 567890765678L;

    String typeOfObject;
    String descOfObject;
    Object obj;

    private void writeObject(ObjectOutputStream stream) throws IOException {
        System.out.println("HEY!! I'm writing ur an Object!");
        stream.writeUTF(typeOfObject);
        stream.writeUTF(descOfObject);
        stream.writeObject(obj);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        System.out.println("HEY!! I'm reading ur an Object!");
        typeOfObject = stream.readUTF();
        descOfObject = stream.readUTF();
        obj = stream.readObject();
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
