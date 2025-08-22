package cn.carbs.tokenizer.reference;

import java.io.File;
import java.util.ArrayList;

public class CodeFileAndReferencedToken {

    public ArrayList<ReferencedToken> referencedTokenArr;

    public String codeFileAbsPath;

    public CodeFileAndReferencedToken(ArrayList<ReferencedToken> referencedTokenArr, String codeFileAbsPath) {
        this.referencedTokenArr = referencedTokenArr;
        this.codeFileAbsPath = codeFileAbsPath;
    }

    @Override
    public String toString() {
        if (referencedTokenArr == null || referencedTokenArr.size() == 0) {
            return "CodeFileAndReferencedToken{"
//                    + "shortCodeFileAbsPath=" + getShortFileName(codeFileAbsPath)
                    + "codeFileAbsPath=" + codeFileAbsPath
                    + ", referencedTokenArr size = 0}";
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (ReferencedToken token : referencedTokenArr) {
                stringBuilder.append("\n");
                stringBuilder.append(token);
            }
            return "CodeFileAndReferencedToken{"
//                    + "shortCodeFileAbsPath=" + getShortFileName(codeFileAbsPath)
                    + "codeFileAbsPath=" + codeFileAbsPath
                    + ", referencedTokenArr=" + stringBuilder.toString() + " }";
        }
    }

    private static String getShortFileName(String absPath) {
        if (absPath == null) {
            return null;
        }
        int index = absPath.lastIndexOf(File.separator);
        if (index > 0 && index < absPath.length() - 1) {
            return absPath.substring(index + 1);
        }
        return absPath;
    }
}