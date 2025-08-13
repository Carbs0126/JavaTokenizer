package cn.carbs.tokenizer.util;

import cn.carbs.tokenizer.backup.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Utils {

    public static ArrayList<String> readLines(String fileName) {

        ArrayList arrayList = new ArrayList();

        try (InputStream is = Utils.class.getClassLoader().getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                arrayList.add(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    public static IdentifierMatcher getIdentifierMatcher(String importPath, String certainRPath) {
        if (importPath == null || certainRPath == null || !importPath.startsWith(certainRPath)) {
            return null;
        }
        int lastIndexOfDot = importPath.lastIndexOf('.');
        String lastIdentifier = null;
//        System.out.println(lastIndexOfDot);
        if (lastIndexOfDot > 0 && lastIndexOfDot < importPath.length() - 1) {
            lastIdentifier = importPath.substring(lastIndexOfDot + 1);
        }
//        System.out.println(lastIdentifier);
        if (lastIdentifier == null) {
            return null;
        }
        String rPostfix = null;
        if (importPath.length() == certainRPath.length()) {
            rPostfix = lastIdentifier + "."; // R.
        } else {
            rPostfix = importPath.substring(certainRPath.length() - 1);
        }
//        System.out.println("lastIdentifier : " + lastIdentifier + "  rPostfix : " + rPostfix);
        return new IdentifierMatcher(lastIdentifier, rPostfix, importPath);
    }

    public static class IdentifierMatcher {
        public String lastIdentifier;
        public String rPostfix;
        public String importPath;

        public IdentifierMatcher(String lastIdentifier, String rPostfix, String importPath) {
            this.lastIdentifier = lastIdentifier;
            this.rPostfix = rPostfix;
            this.importPath = importPath;
        }
    }

}
