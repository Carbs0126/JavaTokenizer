package cn.carbs.tokenizer.backup;

import cn.carbs.tokenizer.util.Utils;

public class R {

    public static class id {

        public static final int my_button = 1;

    }

    public static void main(String[] args) {
        String importPath = "cn.carbs.tools.R.layout.my_button";
        String certainRPath = "cn.carbs.tools.R";
        getIdentifierMatcher(importPath, certainRPath);
    }

    public static Utils.IdentifierMatcher getIdentifierMatcher(String importPath, String certainRPath) {
        if (importPath == null || certainRPath == null || !importPath.startsWith(certainRPath)) {
            return null;
        }
        int lastIndexOfDot = importPath.lastIndexOf('.');
        String lastIdentifier = null;
        System.out.println(lastIndexOfDot);
        if (lastIndexOfDot > 0 && lastIndexOfDot < importPath.length() - 1) {
            lastIdentifier = importPath.substring(lastIndexOfDot + 1);
        }

        System.out.println(lastIdentifier);
        if (lastIdentifier == null) {
            return null;
        }
        String rPostfix = null;
        if (importPath.length() == certainRPath.length()) {
            rPostfix = lastIdentifier + "."; // R.
        } else {
            rPostfix = importPath.substring(certainRPath.length() - 1);
        }
        System.out.println("lastIdentifier : " + lastIdentifier + "  rPostfix : " + rPostfix);
        return new Utils.IdentifierMatcher(lastIdentifier, rPostfix, importPath);
    }

}
