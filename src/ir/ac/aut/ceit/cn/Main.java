package ir.ac.aut.ceit.cn;

import ir.ac.aut.ceit.cn.Logic.Client;
import ir.ac.aut.ceit.cn.Logic.Server;
import ir.ac.aut.ceit.cn.Model.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        //FileUtils.writeFile(FileUtils.readFile("sender/test.png"),"receiver/ok.png");
        Thread server = new Thread(new Server("poker-face","sender/test.png"));
        Thread client = new Thread(new Client("poker-face"));

        server.start();
        client.start();
    }
}
