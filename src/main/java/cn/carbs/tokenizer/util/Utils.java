package cn.carbs.tokenizer.util;

import cn.carbs.tokenizer.backup.R;
import cn.carbs.tokenizer.entity.SealedToken;
import cn.carbs.tokenizer.search.IdentifierMatcher;
import cn.carbs.tokenizer.search.ReferencedToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public static IdentifierMatcher getIdentifierMatcherForImport(String importPath, String certainRPath) {
        if (importPath == null || certainRPath == null || !importPath.startsWith(certainRPath)) {
            return null;
        }
        int lastIndexOfDotForImport = importPath.lastIndexOf('.');
        String lastIdentifier = null;
//        System.out.println(lastIndexOfDot);
        if (lastIndexOfDotForImport > 0 && lastIndexOfDotForImport < importPath.length() - 1) {
            lastIdentifier = importPath.substring(lastIndexOfDotForImport + 1);
        }
//        System.out.println(lastIdentifier);
        if (lastIdentifier == null) {
            return null;
        }
//        String rPostfix = null;
//        if (importPath.length() == certainRPath.length()) {
//            rPostfix = lastIdentifier + "."; // R.
//        } else {
//            rPostfix = importPath.substring(certainRPath.length() - 1);
//        }
        String rPostfix = importPath.substring(certainRPath.length());
        int lastIndexOfDotForCertain = certainRPath.lastIndexOf('.');
        String iPrefix = importPath.substring(lastIndexOfDotForCertain + 1, lastIndexOfDotForImport + 1);

//        System.out.println("lastIdentifier : " + lastIdentifier + "  rPostfix : " + rPostfix);
        return new IdentifierMatcher(lastIdentifier, importPath, certainRPath, iPrefix, rPostfix);
    }

    public static ReferencedToken checkTokenMatched(HashMap<String, IdentifierMatcher> identifierMatcherMap,
                                                    ArrayList<SealedToken> completeIdentifierToken,
                                                    StringBuilder completeTokenStringBuilder) {
        if (completeIdentifierToken == null || completeIdentifierToken.size() == 0) {
            return null;
        }
        if (identifierMatcherMap == null || identifierMatcherMap.size() == 0) {
            return null;
        }
        SealedToken firstToken = completeIdentifierToken.get(0);
        if (firstToken == null || firstToken.literalStr == null || firstToken.literalStr.length() == 0) {
            return null;
        }

        /*
        用 completeIdentifierToken 的第一个token（即 如果有dot，则取第一个 dot 之前的token） 和 availableResourceStarters 中的每一个元素（即lastId）进行 equals 比较，
            如果相同，说明当前这个 identifier 有可能是要找的资源文件，
                如果这个 identifier == R，则需要判断后续的 identifier 必须命中 某种正则，比如 R.layout.xxx 或者 R.drawable.xxx
                如果这个 identifier != R，比如是 identifier == layout，则找到 命中 availableResourceStarters 的那个元素对应的 postfix（比如形式是 layout 或者 layout.xxx）
                    如果这个 postfix 的形式 是 layout，则说明 identifier 还需要继续往后找 identifier 来判断是否命中
                    如果这个 postfix 的形式 是 layout.xxx，则说明 identifier 当前命中的是 xxx，此时要确认 identifier 后面不再连着 dot

         5. 遍历所有 SealedToken，找到以 identifier 开头的 token，用这个 token 和 availableResourceStarters 中的每一个元素（即lastId）进行 equals 比较，
            todo 这里有点疑问，要把静态引入的情况也包含进来，比如 int x = my_button;
            todo 思路差不多好了
            如果相同，说明当前这个 identifier 有可能是要找的资源文件，
                如果这个 identifier == R，则需要判断后续的 identifier 必须命中 某种正则，比如 R.layout.xxx 或者 R.drawable.xxx
                如果这个 identifier != R，比如是 identifier == layout，则找到 命中 availableResourceStarters 的那个元素对应的 postfix（比如形式是 layout 或者 layout.xxx）
                    如果这个 postfix 的形式 是 layout，则说明 identifier 还需要继续往后找 identifier 来判断是否命中
                    如果这个 postfix 的形式 是 layout.xxx，则说明 identifier 当前命中的是 xxx，此时要确认 identifier 后面不再连着 dot
         */

        for (Map.Entry<String, IdentifierMatcher> entry : identifierMatcherMap.entrySet()) {
            String key = entry.getKey();
            // 如果是import到一个静态变量，比如 my_textview，如果此时在函数内声明了一个 my_textview 同名变量，则会有问题
            if (firstToken.literalStr.equals(key)) {
                // identifier 开头和 引入的资源匹配开始匹配，说明当前这个 identifier 有可能是要找的资源文件
                //     如果这个 identifier == R，则需要判断后续的 identifier 必须命中 某种正则，比如 R.layout.xxx 或者 R.drawable.xxx
                if ("R".equals(firstToken.literalStr)) {
                    // todo wang 必须命中某种规则? 比如 R.layout
                    // 这里暂时直接把这个 token 拿出来
                    // 命中！
                    // todo wang 这里一定有问题，IdentifierMatcher 必须重新复制
                    IdentifierMatcher value = entry.getValue();
                    return new ReferencedToken(value, completeTokenStringBuilder)
                            .setStandardSimpleReference(value.iPrefix + completeTokenStringBuilder.toString());
                } else {
                    // todo wang
                    /*
                    如果这个 identifier != R，比如是 identifier == layout，则找到 命中 availableResourceStarters 的那个元素对应的 postfix（比如形式是 layout 或者 layout.xxx）
                    如果这个 postfix 的形式 是 layout，则说明 identifier 还需要继续往后找 identifier 来判断是否命中
                    如果这个 postfix 的形式 是 layout.xxx，则说明 identifier 当前命中的是 xxx，此时要确认 identifier 后面不再连着 dot
                     */
                    /**
                     * [IdentifierMatcher] -->
                     * importPath : cn.carbs.ttt.R.layout
                     * lastIdentifier : layout
                     * rPrefix : R.
                     * rPostfix : .layout
                     * [IdentifierMatcher] -->
                     * importPath : cn.carbs.ttt.R.layout.my_textview
                     * lastIdentifier : my_textview
                     * rPrefix : R.layout.
                     * rPostfix : .layout.my_textview
                     * [IdentifierMatcher] -->
                     * importPath : cn.carbs.tools.R
                     * lastIdentifier : R
                     * rPrefix :
                     * rPostfix :
                     */

                    // 此时需要补充一下 prefix，补充到 R
                    // todo wang
                    // todo wang 这里一定有问题，IdentifierMatcher 必须重新复制
                    IdentifierMatcher value = entry.getValue();
                    return new ReferencedToken(value, completeTokenStringBuilder)
                            .setStandardSimpleReference(value.iPrefix + completeTokenStringBuilder.toString());
                }
            }
        }
        return null;
    }

}
