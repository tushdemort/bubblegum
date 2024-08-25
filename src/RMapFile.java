import java.io.*;
import java.util.*;

public class RMapFile {
    
    public static void writeRMap(String filename, List<Map<String, Object>> data) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filename))) {
            // Write number of objects
            dos.writeInt(data.size());
            
            for (Map<String, Object> obj : data) {
                // Write number of key-value pairs in this object
                dos.writeInt(obj.size());
                
                for (Map.Entry<String, Object> entry : obj.entrySet()) {
                    // Write key
                    dos.writeUTF(entry.getKey());
                    
                    // Write value type
                    Object value = entry.getValue();
                    if (value instanceof Integer) {
                        dos.writeByte(0);
                        dos.writeInt((Integer) value);
                    } else if (value instanceof String) {
                        dos.writeByte(1);
                        dos.writeUTF((String) value);
                    } else if (value instanceof Double) {
                        dos.writeByte(2);
                        dos.writeDouble((Double) value);
                    } else if (value instanceof Boolean) {
                        dos.writeByte(3);
                        dos.writeBoolean((Boolean) value);
                    } else {
                        throw new IllegalArgumentException("Unsupported type: " + value.getClass());
                    }
                }
            }
        }
    }
    
    public static List<Map<String, Object>> readRMap(String filename) throws IOException {
        List<Map<String, Object>> result = new ArrayList<>();
        
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filename))) {
            int objectCount = dis.readInt();
            
            for (int i = 0; i < objectCount; i++) {
                Map<String, Object> obj = new HashMap<>();
                int pairCount = dis.readInt();
                
                for (int j = 0; j < pairCount; j++) {
                    String key = dis.readUTF();
                    byte type = dis.readByte();
                    
                    switch (type) {
                        case 0:
                            obj.put(key, dis.readInt());
                            break;
                        case 1:
                            obj.put(key, dis.readUTF());
                            break;
                        case 2:
                            obj.put(key, dis.readDouble());
                            break;
                        case 3:
                            obj.put(key, dis.readBoolean());
                            break;
                        default:
                            throw new IOException("Unknown type: " + type);
                    }
                }
                
                result.add(obj);
            }
        }
        
        return result;
    }

    public static void main(String[] args) throws IOException {
        // Create sample data
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> obj1 = new HashMap<>();
        obj1.put("name", "John");
        obj1.put("age", 30);
        obj1.put("height", 1.75);
        obj1.put("isStudent", false);
        data.add(obj1);

        Map<String, Object> obj2 = new HashMap<>();
        obj2.put("name", "Alice");
        obj2.put("age", 25);
        obj2.put("height", 1.68);
        obj2.put("isStudent", true);
        data.add(obj2);

        // Write to file
        writeRMap("example.rmap", data);

        // Read from file
        List<Map<String, Object>> readData = readRMap("example.rmap");

        // Print read data
        System.out.println(readData);
    }
}