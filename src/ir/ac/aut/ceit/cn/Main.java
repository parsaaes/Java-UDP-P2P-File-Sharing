package ir.ac.aut.ceit.cn;

import ir.ac.aut.ceit.cn.Logic.Client;
import ir.ac.aut.ceit.cn.Logic.Server;

public class Main {

    public static void main(String[] args) {
        Thread server = new Thread(new Server());
        Thread client = new Thread(new Client());

        server.start();
        client.start();
    }
}
