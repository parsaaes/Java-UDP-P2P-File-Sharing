package ir.ac.aut.ceit.cn.Model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {
    public static byte[] readFile(String path) {
        File file = new File(path);
        byte[] data = null;
        try {
            data = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void writeFile(byte[] data, String path) {
        try {
            Files.write(new File(path).toPath(), data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
