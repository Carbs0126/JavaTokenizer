package cn.carbs.tokenizer.util;

public class Log {

    private static final boolean SILENCE_VERBOSE = true;

    private static boolean SILENCE_ERROR = true;

    private static final int MAX_ERROR_LOG_COUNT = Integer.MAX_VALUE;
//    private static final int MAX_ERROR_LOG_COUNT = 20;

    private static int sLogCount = 0;

    public static void e(String tag, String message, String fileName) {
        if (SILENCE_ERROR) {
            System.err.println("FILE NAME --> [" + fileName + "] ");
            System.err.println("PARSE ERROR --> [" + tag + "] " + message);
            sLogCount++;
            if (sLogCount > MAX_ERROR_LOG_COUNT - 1) {
                SILENCE_ERROR = false;
            }
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
