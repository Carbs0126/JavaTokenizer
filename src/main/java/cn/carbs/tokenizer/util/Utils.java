package cn.carbs.tokenizer.util;

import cn.carbs.tokenizer.core.ITokenParser;
import cn.carbs.tokenizer.core.JavaTokenParser;
import cn.carbs.tokenizer.core.KotlinTokenParser;
import cn.carbs.tokenizer.entity.SealedToken;
import cn.carbs.tokenizer.search.IdentifierMatcher;
import cn.carbs.tokenizer.search.ReferencedToken;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

public class Utils {

    public static final HashSet<String> sAndroidResourceType = new HashSet<>();
//    public static final String sAndroidResourceFilePrefixRegex = "^[a-zA-Z][a-zA-Z0-9_]*$";
    public static final String sAndroidResourceFilePrefixRegex = "^[a-z][a-z0-9_]*$";
    public static final Pattern sAndroidResourceFilePrefixPattern = Pattern.compile(sAndroidResourceFilePrefixRegex);

    static {
        sAndroidResourceType.add("drawable");
        sAndroidResourceType.add("layout");
        sAndroidResourceType.add("string");
        sAndroidResourceType.add("id");
        sAndroidResourceType.add("color");
        sAndroidResourceType.add("dimen");
        sAndroidResourceType.add("style");
        sAndroidResourceType.add("array");
        sAndroidResourceType.add("anim");
        sAndroidResourceType.add("animator");
        sAndroidResourceType.add("mipmap");
        sAndroidResourceType.add("raw");
        sAndroidResourceType.add("bool");
        sAndroidResourceType.add("integer");
        sAndroidResourceType.add("transition");
        sAndroidResourceType.add("xml");
        sAndroidResourceType.add("font");
    }

    public static ArrayList<String> readLinesForAbsFilePath(String filePath) {

        File file = new File(filePath);

        if (!file.exists()) {
            Log.e("文件不存在: " + filePath);
            return null;
        }

        if (!file.isFile()) {
            Log.e("指定路径不是文件: " + filePath);
            return null;
        }

        ArrayList arrayList = new ArrayList();

        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                arrayList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    public static ArrayList<String> readLinesForFileInResourcesFolder(String fileName) {

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

    // todo 暂时没想好怎么写这个方法
    public static boolean isAbsFilePathMatchResourcePattern(String str, HashSet<String> excludeMap, Pattern includePattern) {
        str = trimStringIfWithQuotation(str);
        if (str == null) {
            return false;
        }
        if (excludeMap != null && excludeMap.contains(str)) {
            return false;
        }
        if (includePattern == null) {
            return true;
        }
        return includePattern.matcher(str).matches();
    }

    static boolean sCheckStringInCode = false;
    // todo 需要添加灵活配置的选项，是否打开，是否
    public static boolean isStringMatchResourcePattern(String str, HashSet<String> excludeMap, Pattern includePattern) {
        if (!sCheckStringInCode) {
            return false;
        }
        str = trimStringIfWithQuotation(str);
        if (str == null) {
            return false;
        }
        if (excludeMap != null && excludeMap.contains(str)) {
            return false;
        }
        if (includePattern == null) {
            return true;
        }
        return includePattern.matcher(str).matches();
    }

    public static boolean isStringMatchAndroidResourcePattern(String str) {
        return isStringMatchResourcePattern(str, sAndroidResourceType, sAndroidResourceFilePrefixPattern);
    }

    public static String trimStringIfWithQuotation(String str) {
        if (str == null || str.length() < 2) {
            return str;
        }
        if (str.charAt(0) == '"' && str.charAt(str.length() - 1) == '"') {
            return str.substring(1, str.length() - 1);
        }
        if (str.charAt(0) == '\'' && str.charAt(str.length() - 1) == '\'') {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    public static ITokenParser genCodeTokenParserByFileName(String fileName) {
        if (fileName == null) {
            throw new RuntimeException("genTokenParserByFileName failed, fileName is " + fileName);
        }
        if (fileName.endsWith(".java")) {
            return new JavaTokenParser(fileName);
        }
        if (fileName.endsWith(".kt")) {
            return new KotlinTokenParser(fileName);
        }
        throw new RuntimeException("genTokenParserByFileName failed, fileName is " + fileName);
    }

    public static ArrayList<File> findCertainFormatFiles(File directory, ArrayList<String> postfixArrWithoutDot) {
        ArrayList<File> retFiles = new ArrayList<>();

        // 检查目录是否存在且是一个有效的文件夹
        if (!directory.exists() || !directory.isDirectory()) {
//            System.out.println("无效的文件夹路径：" + directory.getAbsolutePath());
            Log.e("无效的文件夹路径：" + directory.getAbsolutePath());
            return retFiles;
        }

        // 获取目录下的所有文件和子目录
        File[] files = directory.listFiles();
        if (files == null) { // 处理权限问题导致的无法访问
            return retFiles;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // 如果是子目录，递归查找
                retFiles.addAll(findCertainFormatFiles(file, postfixArrWithoutDot));
            } else {
                // 如果是文件，检查是否为Java文件
                if (isCertainFormatFile(file, postfixArrWithoutDot)) {
                    retFiles.add(file);
                }
            }
        }

        return retFiles;
    }

    public static ArrayList<File> findCertainFormatFiles(File directory, String postfixWithoutDot) {
        ArrayList<File> retFiles = new ArrayList<>();

        // 检查目录是否存在且是一个有效的文件夹
        if (!directory.exists() || !directory.isDirectory()) {
//            System.out.println("无效的文件夹路径：" + directory.getAbsolutePath());
            Log.e("无效的文件夹路径：" + directory.getAbsolutePath());
            return retFiles;
        }

        // 获取目录下的所有文件和子目录
        File[] files = directory.listFiles();
        if (files == null) { // 处理权限问题导致的无法访问
            return retFiles;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // 如果是子目录，递归查找
                retFiles.addAll(findCertainFormatFiles(file, postfixWithoutDot));
            } else {
                // 如果是文件，检查是否为Java文件
                if (isCertainFormatFile(file, postfixWithoutDot)) {
                    retFiles.add(file);
                }
            }
        }
        return retFiles;
    }

    // 只检查下一层
    public static ArrayList<File> findFilesInCertainPrefixDir(File directory, ArrayList<String> prefixDirArr) {
        ArrayList<File> retFiles = new ArrayList<>();

        // 检查目录是否存在且是一个有效的文件夹
        if (!directory.exists() || !directory.isDirectory()) {
//            System.out.println("无效的文件夹路径：" + directory.getAbsolutePath());
            Log.e("无效的文件夹路径：" + directory.getAbsolutePath());
            return retFiles;
        }

        // 获取目录下的所有文件和子目录
        File[] files = directory.listFiles();
        if (files == null) { // 处理权限问题导致的无法访问
            return retFiles;
        }

        for (File file : files) {
            if (file.isDirectory()) {

                if (prefixDirArr == null || prefixDirArr.size() == 0) {
                    // 如果是子目录，递归查找
                    retFiles.addAll(findFilesInCertainPrefixDir(file, prefixDirArr));
                } else {
                    for (String prefix : prefixDirArr) {
                        if (file.getName().startsWith(prefix)) {
                            retFiles.addAll(findFilesInCertainPrefixDir(file, prefixDirArr));
                        }
                    }
                }
            } else {
                // 如果是文件，检查是否为Java文件
                if (isNormalFile(file)) {
                    retFiles.add(file);
                }
            }
        }
        return retFiles;
    }


    private static boolean isCertainFormatFile(File file, ArrayList<String> postfixArr) {
        String fileName = file.getName();
//        System.out.println("===========> isCertainFormatFile fileName : " + fileName);
        // 检查文件名是否以.java结尾，且不是隐藏文件（macOS下以.开头的文件）
        if (fileName.startsWith(".")) {
            return false;
        }
        if (postfixArr == null) {
            return true;
        }
        for (String postfix : postfixArr) {
            if (fileName.endsWith("." + postfix)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isCertainFormatFile(File file, String postfix) {
        String fileName = file.getName();
        if (fileName.startsWith(".")) {
            return false;
        }
        if (postfix == null || postfix.length() == 0) {
            return true;
        }
        // 检查文件名是否以.java结尾，且不是隐藏文件（macOS下以.开头的文件）
        return fileName.endsWith("." + postfix);
    }

    private static boolean isNormalFile(File file) {
        if (file == null) {
            return false;
        }
        if (file.getName().startsWith(".")) {
            return false;
        }
        return true;
    }

}
