package cn.carbs.tokenizer.util;

public class Log {

    private static final boolean SILENCE_ERROR = false;

    public static void d(String tag, String message, String fileName) {
        if (SILENCE_ERROR) {
            System.err.println("PARSE ERROR --> [" + tag + "] " + message);
        } else {
            System.err.println("FILE NAME --> [" + fileName + "] ");
            throw new RuntimeException("PARSE ERROR --> [" + tag + "] " + message);
        }
    }

    public static void e(String message) {
        throw new RuntimeException("ERROR --> [" + message + "] ");
    }

}
