package cn.carbs.tokenizer.xml;

import java.util.ArrayList;

// 在整体中，只计算 comment 和 string，不区分其它信息
public class XMLParser {

    private XmlCommentOrString sCommentOrString = XmlCommentOrString.None;

    // 前一个是否为 escape
    private boolean stringEscapePrepared = false;

    // 获取一个 file 的 tokens
    public ArrayList<String> getResourceRefs(ArrayList<String> arrayList) {
        ArrayList<String> strArr = new ArrayList<>();
        if (arrayList == null || arrayList.size() == 0) {
            return strArr;
        }
        // 开始了
        StringBuilder strCache = new StringBuilder();

        int lineIndex = -1;
        for (String s : arrayList) {
            lineIndex++;
//             print("line ->|" + (lineIndex + 1) + "|" + s);
            // 人工添加一个 换行 token，便于打印
            int strLength = s.length();

            if (sCommentOrString == XmlCommentOrString.MayCommentStart0
                    || sCommentOrString == XmlCommentOrString.MayCommentStart1
                    || sCommentOrString == XmlCommentOrString.MayCommentStart2) {
                sCommentOrString = XmlCommentOrString.None;
            }
            if (sCommentOrString == XmlCommentOrString.MayCommentEnd0
                    || sCommentOrString == XmlCommentOrString.MayCommentEnd1) {
                sCommentOrString = XmlCommentOrString.InComment;
            }
            // 当 section 位于 none 或者 package 或者 import 时
            for (int i = 0; i < strLength; i++) {
                char c = s.charAt(i);
                if (sCommentOrString == XmlCommentOrString.None) {
                    if (c == '<') {
                        sCommentOrString = XmlCommentOrString.MayCommentStart0;
                        continue;
                    } else if (isStringSymbol(c)) {
                        sCommentOrString = XmlCommentOrString.InString;
                        continue;
                    }
                } else if (sCommentOrString == XmlCommentOrString.InString) {
                    if (stringEscapePrepared) {
                        stringEscapePrepared = false;
                        strCache.append(c);
                        continue;
                    } else if (isEscape(c)) {
                        stringEscapePrepared = true;
                        strCache.append(c);
                        continue;
                    } else if (isStringSymbol(c)) {
                        sCommentOrString = XmlCommentOrString.None;
                        strArr.add(strCache.toString());
                        strCache.setLength(0);
                        continue;
                    } else {
                        strCache.append(c);
                        continue;
                    }
                } else if (sCommentOrString == XmlCommentOrString.InComment) {
                    if (c == '-') {
                        sCommentOrString = XmlCommentOrString.MayCommentEnd0;
                    }
                    continue;
                } else if (sCommentOrString == XmlCommentOrString.MayCommentStart0) {
                    if (c == '!') {
                        sCommentOrString = XmlCommentOrString.MayCommentStart1;
                    } else {
                        sCommentOrString = XmlCommentOrString.None;
                    }
                    continue;
                } else if (sCommentOrString == XmlCommentOrString.MayCommentStart1) {
                    if (c == '-') {
                        sCommentOrString = XmlCommentOrString.MayCommentStart2;
                        continue;
                    } else {
                        sCommentOrString = XmlCommentOrString.None;
                        continue;
                    }
                } else if (sCommentOrString == XmlCommentOrString.MayCommentStart2) {
                    if (c == '-') {
                        sCommentOrString = XmlCommentOrString.InComment;
                        continue;
                    } else {
                        sCommentOrString = XmlCommentOrString.None;
                        continue;
                    }
                } else if (sCommentOrString == XmlCommentOrString.MayCommentEnd0) {
                    if (c == '-') {
                        sCommentOrString = XmlCommentOrString.MayCommentEnd1;
                        continue;
                    } else {
                        sCommentOrString = XmlCommentOrString.InComment;
                        continue;
                    }
                } else if (sCommentOrString == XmlCommentOrString.MayCommentEnd1) {
                    if (c == '>') {
                        sCommentOrString = XmlCommentOrString.None;
                        continue;
                    } else {
                        sCommentOrString = XmlCommentOrString.InComment;
                        continue;
                    }
                }
            }
        }
        return strArr;
    }

    // 是否为转义字符
    private static boolean isEscape(char c) {
        return c == '\\';
    }

    private static boolean isStringSymbol(char c) {
        return c == '"';
    }

}
