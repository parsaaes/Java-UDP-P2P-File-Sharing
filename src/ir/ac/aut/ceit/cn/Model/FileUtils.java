package ir.ac.aut.ceit.cn.Model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {

    public static boolean fileExist(String path) {
        File f = new File(path);
        if(f.exists() && !f.isDirectory()) {
            return true;
        }
        else {
            return false;
        }
    }

    public static byte[] readFile(String path) {
        File file = new File(path);
        byte[] data = null;
        try {
            data = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            System.out.println("Error in reading file. maybe it's not exist.");
        }
        return data;
    }

    public static void writeFile(byte[] data, String path) {
        File file = new File(path);
        try {
            if(!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            else {
                // following lines seems unnecessary
                file.delete();
                file.createNewFile();
            }
            Files.write(file.toPath(), data);
        } catch (IOException e) {
            System.out.println("Error in writing to file.");
        }
    }
}
