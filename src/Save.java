import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Save {
    public static void main(String[] args) throws IOException {

    // Read from file
    List<Map<String, Object>> readData = RMapFile.readRMap("hmm.rmap");

    // Print read data
    System.out.println(readData);
}
}
