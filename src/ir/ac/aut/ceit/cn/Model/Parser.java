package ir.ac.aut.ceit.cn.Model;

import java.util.ArrayList;

public class Parser {

    public static boolean isReceiveValid(String rawString) {
        String[] lines = splitString(rawString);
        return lines != null && lines[0] != null && lines[0].equals("p2p") && lines.length >= 3;
    }
    public static String parseReceive(String rawString) {
        String[] lines = splitString(rawString);
        return lines[2];
    }

    public static boolean isServeValid(String rawString) {
        String[] lines = splitString(rawString);
        return lines != null && lines[0] != null && lines[0].equals("p2p") && (lines.length >= 6) && lines[3] != null && lines[5] != null;
    }

    public static ArrayList<String> parseServe(String rawString) {
        String[] lines = splitString(rawString);
        ArrayList<String> result = new ArrayList<>();
        result.add(lines[3]);
        result.add(lines[5]);
        return result;
    }

    private static String[] splitString(String rawString) {
        return rawString.split(" ");
    }
}
