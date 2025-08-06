package cn.carbs.tokenizer;

import cn.carbs.tokenizer.core.JavaTokenParser;
import cn.carbs.tokenizer.entity.SealedToken;
import cn.carbs.tokenizer.type.TokenType;
import cn.carbs.tokenizer.util.Utils;

import java.util.ArrayList;

public class Main {

    private static void testAllCases() {
        String[] allFiles = new String[]{
                "data0.txt", "data1.txt", "data2.txt", "data3.txt", "data4.txt",
                "data5.txt", "data6.txt", "data7.txt", "data8.txt", "data9.txt",};
        int[] tokenCount = new int[]{150, 9976, 3262, 11138, 8251, 3519, 24, 0, 0, 0};
        for (int i = 0; i < allFiles.length; i++) {
            assertTokens(allFiles[i], tokenCount[i]);
        }
    }

    public static void main(String[] argv) {

        // analyseJavaFileAndShow("data3.txt");

        testAllCases();
    }

    private static void assertTokens(String fileName, int tokenCount) {
        ArrayList<String> arrayList = Utils.readLines(fileName);
        ArrayList<SealedToken> tokens = new JavaTokenParser().getTokens(arrayList);
        if (tokens.size() == tokenCount) {
            System.out.println("[Pass] " + fileName + "'s token count is " + tokenCount);
        } else {
            System.err.println("[Fail] " + fileName + "'s token count is " + tokens.size()
                    + ", target token count is " + tokenCount);
        }
    }

    private static void analyseJavaFileAndShow(String fileName) {
        ArrayList<String> arrayList = Utils.readLines(fileName);

        ArrayList<SealedToken> tokens = new JavaTokenParser().getTokens(arrayList);

        StringBuilder tokensStr = new StringBuilder();
        // 展示 tokens
        if (tokens != null) {
            SealedToken preToken = null;
            SealedToken curToken = null;
            boolean needSpace = false;
            for (SealedToken sealedToken : tokens) {
                curToken = sealedToken;
                if (preToken != null) {
                    if (preToken.type == TokenType.Identifier && curToken.type == TokenType.Identifier) {
                        needSpace = true;
                    } else {
                        needSpace = false;
                    }
                }
                if (needSpace) {
                    tokensStr.append(" ");
                }
                tokensStr.append(sealedToken.getLiteralStr());
                preToken = curToken;
            }
        }
        System.out.println("=================== ↓ display tokens ↓ ===================");
        System.out.println(tokensStr);
        System.out.println("token size : " + tokens.size());
    }
}
