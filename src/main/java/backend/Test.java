package backend;


import java.io.*;

public class Test {
    public void writeExcel(Animal animal) {
        try {
            File file = new File("src/main/resources/test.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            String text = animal.getEnergyAsString();
            writer.write(text);
            writer.newLine();
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}