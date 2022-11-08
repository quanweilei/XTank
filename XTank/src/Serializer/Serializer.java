package Serializer;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/*
 * Quanwei Lei
 * Serializer serializes ObjectSerializer and can either return an object from a byte array of vice versa.
 * Extremely useful for transferring information regarding tanks and game world from server to client and client
 * to server.
 */
public class Serializer implements Serializable{
    static Serializer ser = null;

    /*
     * Singleton instance of a serializer
     */
    public static Serializer getInstance() {
        if (ser == null) {
            ser = new Serializer();
        }
        return ser;
    }

    public byte[] obToByte(ObjectSerialize obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        byte[] data = bos.toByteArray();
        return data;
    }

    public ObjectSerialize byteToOb(byte[] bArray) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bos = new ByteArrayInputStream(bArray);
        ObjectInputStream is = new ObjectInputStream(bos);
        return (ObjectSerialize) is.readObject();
    }
}
