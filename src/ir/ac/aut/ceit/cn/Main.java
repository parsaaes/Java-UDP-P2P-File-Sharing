package ir.ac.aut.ceit.cn;

import ir.ac.aut.ceit.cn.Logic.Client;
import ir.ac.aut.ceit.cn.Logic.Server;
import ir.ac.aut.ceit.cn.Model.FileUtils;
import ir.ac.aut.ceit.cn.Model.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String rawText = sc.nextLine();
            processRequest(rawText);
        }

    }

    private static void processRequest(String rawText) {
        if (Parser.isServeValid(rawText)) {
            ArrayList<String> arguments = Parser.parseServe(rawText);
//            Thread server = new Thread(new Server("poker-face21.mp4", "sender/vid.mp4"));
            if(FileUtils.fileExist(arguments.get(1))) {
                Thread server = new Thread(new Server(arguments.get(0), arguments.get(1)));
                server.start();
                System.out.println("I am serving " + arguments.get(0));
            }
            else {
                System.out.println("File doesn't exist! please restart the program with a valid file.");
                System.exit(-1);
            }
        }
        else if(Parser.isReceiveValid(rawText)) {
            String argument = Parser.parseReceive(rawText);
            Thread client = new Thread(new Client(argument));
            client.start();
        }
    }
}
