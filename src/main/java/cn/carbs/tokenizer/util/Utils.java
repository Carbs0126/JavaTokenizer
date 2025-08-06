package cn.carbs.tokenizer.util;

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
}
