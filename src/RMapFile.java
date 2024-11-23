import java.io.*;
import java.util.*;

public class RMapFile {

    // Method to write List<List<Map<String, Object>>> to a file
    public static void writeRMap(String filename, List<List<Map<String, Object>>> data) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
        }
    }

    // Method to read List<List<Map<String, Object>>> from a file
    public static List<List<Map<String, Object>>> readRMap(String filename) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<List<Map<String, Object>>>) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Class not found while reading RMap file", e);
        }
    }
}