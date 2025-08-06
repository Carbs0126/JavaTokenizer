package cn.carbs.tokenizer;

import cn.carbs.tokenizer.core.JavaTokenParser;
import cn.carbs.tokenizer.entity.SealedToken;
import cn.carbs.tokenizer.type.TokenType;
import cn.carbs.tokenizer.util.Utils;

import java.util.ArrayList;

public class Main {

    public static void main(String[] argv) {

        // data0.txt success
        // data1.txt success
        // data2.txt success
        // data3.txt success
        // data4.txt success
        // data5.txt success
        // data6.txt
        // data7.txt
        // data8.txt
        // data9.txt
        ArrayList<String> arrayList = Utils.readLines("data3.txt");

        ArrayList<SealedToken> tokens = JavaTokenParser.getTokens(arrayList);

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
    }
}
