package cn.carbs.tokenizer.util;

public class Log {

    private static final boolean SILENCE_VERBOSE = true;

    private static final boolean SILENCE_ERROR = true;

    public static void e(String tag, String message, String fileName) {
        if (SILENCE_ERROR) {
            System.err.println("FILE NAME --> [" + fileName + "] ");
            System.err.println("PARSE ERROR --> [" + tag + "] " + message);
        } else {
            System.err.println("FILE NAME --> [" + fileName + "] ");
            throw new RuntimeException("PARSE ERROR --> [" + tag + "] " + message);
        }
    }

    public static void wtf(String message) {
        throw new RuntimeException("ERROR --> [" + message + "] ");
    }

    public static void v(String message) {
        if (SILENCE_VERBOSE) {
            return;
        }
        System.out.println(message);
    }

}
