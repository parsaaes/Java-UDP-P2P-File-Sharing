package ir.ac.aut.ceit.cn;

import ir.ac.aut.ceit.cn.Logic.Client;
import ir.ac.aut.ceit.cn.Logic.Server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
         String testFile = "test123";

        File file = new File("sender/test.png");
        try {
            byte[] data = Files.readAllBytes(file.toPath());
            System.out.println(data.length);
            Files.write(new File("receiver/res2.png").toPath(),data);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread server = new Thread(new Server("test",testFile.getBytes().length));
        Thread client = new Thread(new Client("test"));

        server.start();
        client.start();
    }
}
