package cn.carbs.tokenizer.util;

import cn.carbs.tokenizer.capsule.StringOrArrayList;
import cn.carbs.tokenizer.core.ITokenParser;
import cn.carbs.tokenizer.core.JavaTokenParser;
import cn.carbs.tokenizer.core.KotlinTokenParser;
import cn.carbs.tokenizer.entity.SealedToken;
import cn.carbs.tokenizer.reference.IdentifierMatcher;
import cn.carbs.tokenizer.reference.ReferencedToken;
import cn.carbs.tokenizer.xml.XMLParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
            Log.wtf("文件不存在: " + filePath);
            return null;
        }

        if (!file.isFile()) {
            Log.wtf("指定路径不是文件: " + filePath);
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
                            .setStandardReferenceStr(value.iPrefix + completeTokenStringBuilder.toString());
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
                            .setStandardReferenceStr(value.iPrefix + completeTokenStringBuilder.toString());
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
            Log.wtf("无效的文件夹路径：" + directory.getAbsolutePath());
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
            Log.wtf("无效的文件夹路径：" + directory.getAbsolutePath());
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
            Log.wtf("无效的文件夹路径：" + directory.getAbsolutePath());
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

    public static String getLastFolderName(File file) {
        if (file == null) {
            return null;
        }

        // 获取文件的父目录
        File parentFile = file.getParentFile();

        // 如果父目录存在，返回其名称；否则返回null
        return parentFile != null ? parentFile.getName() : null;
    }

    /**
     * 获取文件名中第一个点(.)前面的部分
     *
     * @param file 文件对象
     * @return 第一个点前面的字符串，如果没有点则返回完整文件名
     */
    public static String getFileNameBeforeFirstDot(File file) {
        if (file == null) {
            return null;
        }

        // 获取文件名（不包含路径）
        String fileName = file.getName();

        // 查找第一个点的位置
        int dotIndex = fileName.indexOf('.');

        // 如果存在点，返回点前面的部分；否则返回完整文件名
        if (dotIndex != -1) {
            return fileName.substring(0, dotIndex);
        } else {
            return fileName;
        }
    }

    /**
     * 计算文件的MD5值
     *
     * @param file 要计算MD5的文件对象
     * @return 文件的MD5值，若发生错误则返回null
     */
    public static String getFileMD5(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }

        MessageDigest md5 = null;
        FileInputStream fis = null;

        try {
            md5 = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);

            byte[] buffer = new byte[8192];
            int length;

            // 读取文件内容并更新MD5摘要
            while ((length = fis.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }

            // 将MD5摘要转换为十六进制字符串
            byte[] bytes = md5.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭文件输入流
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * 将 xml 中的  @drawable/xxx  转为 R.drawable.xxx
     * OK
     *
     * @param refInXml
     * @return
     */
    public static String transformRefInXmlToResourceId(String refInXml) {
        if (refInXml == null) {
            return null;
        }
        if (refInXml.charAt(0) != '@') {
            return null;
        }
        int indexOfSlash = refInXml.indexOf('/');
        if (indexOfSlash > 0 && indexOfSlash < refInXml.length() - 1) {
            return "R." + refInXml.substring(1, indexOfSlash) + "." + refInXml.substring(indexOfSlash + 1);
        }
        return null;
    }

    // TODO 怎么结束呢？
    public static void startColor(HashMap<String, StringOrArrayList> mapResourceToFiles) {
        if (mapResourceToFiles == null) {
            return;
        }
        for (Map.Entry<String, StringOrArrayList> entry : mapResourceToFiles.entrySet()) {
            StringOrArrayList value = entry.getValue();
            if (value.getState() == 1) {
                // id 已经被染色了，说明这个 id 和对应的文件会被用到
                // 从这个已经被染色的 id 出发，只分析 layout xml 和 drawable xml
                String resourceIDStr = entry.getKey();
                // drawable 有可能是一张图片，也有可能是 xml，要区别对待
                if (resourceIDStr.startsWith("R.layout.") || resourceIDStr.startsWith("R.drawable.")) {
                    // 收集对应的xml文件中的 string，并辨别是否为 @drawable 或者 @layout
                    ArrayList<String> concernedXMLIDs = analyseStringOrArrayListObjAndExtractConcernedIDS(value);
                    traversColor(mapResourceToFiles, concernedXMLIDs);
                }
            }
        }
    }

    // 把 concernedXMLIDs 对应的 给 color
    public static void traversColor(HashMap<String, StringOrArrayList> mapResourceToFiles, ArrayList<String> concernedXMLIDs) {
        if (concernedXMLIDs == null || concernedXMLIDs.size() == 0) {
            return;
        }
        if (mapResourceToFiles == null || mapResourceToFiles.size() == 0) {
            return;
        }
        for (String resourceID : concernedXMLIDs) {
            if (resourceID == null) {
                continue;
            }
            if (!mapResourceToFiles.containsKey(resourceID)) {
                continue;
            }
            StringOrArrayList value = mapResourceToFiles.get(resourceID);
            if (value == null) {
                mapResourceToFiles.remove(resourceID);
                continue;
            }
            if (value.getState() == 1) {
                // 已经染色，跳过
                continue;
            }
            // 1. 染色，下次不再进入这个 xml 了
            value.setState(1);
            // 2. 只分析 R.layout.xxx xml  和  R.drawable.xxx xml 中的内容
            if (resourceID.startsWith("R.layout.") || resourceID.startsWith("R.drawable.")) {
                ArrayList<String> nextRoundConcernedXMLIDs = analyseStringOrArrayListObjAndExtractConcernedIDS(value);
                traversColor(mapResourceToFiles, nextRoundConcernedXMLIDs);
            }
        }
    }

    private static ArrayList<String> analyseStringOrArrayListObjAndExtractConcernedIDS(StringOrArrayList stringOrArrayList) {
        if (stringOrArrayList == null) {
            return null;
        }
        ArrayList<String> concernedXMLIDs = null;
        if (stringOrArrayList.isArray()) {
            ArrayList<String> arr = stringOrArrayList.getArr();
            if (arr != null && arr.size() > 0) {
                concernedXMLIDs = new ArrayList<>(arr.size() * 16);
                for (String filePath : arr) {
                    if (filePath != null && filePath.endsWith(".xml")) {
                        ArrayList<String> concernedXMLIDsForOneFile = pickConcernedXMLIDs(filePath);
                        if (concernedXMLIDsForOneFile != null) {
                            concernedXMLIDs.addAll(concernedXMLIDsForOneFile);
                        }
                    }
                }
            }
        } else {
            String str = stringOrArrayList.getStr();
            concernedXMLIDs = pickConcernedXMLIDs(str);
        }
        return concernedXMLIDs;
    }

    private static ArrayList<String> analyseXMLStrings(String absFilePath) {
        if (absFilePath == null || !absFilePath.endsWith(".xml")) {
            return null;
        }
        ArrayList<String> arrayList = Utils.readLinesForAbsFilePath(absFilePath);
        XMLParser xmlParser = new XMLParser();
        ArrayList<String> arr = xmlParser.getResourceRefs(arrayList);
//        System.out.println("testXML() file name : " + fileName);
//        for (String s : arr) {
//            System.out.println(s);
//        }
        return arr;
    }

    /**
     * 如果xml中存在 @drawable/xxx 或者 @layout/xxx
     * 则返回 R.drawable.xxx R.layout.xxx
     *
     * @param absFilePath
     * @return
     */
    public static ArrayList<String> pickConcernedXMLIDs(String absFilePath) {
        ArrayList<String> stringsInXml = analyseXMLStrings(absFilePath);
        if (stringsInXml == null || stringsInXml.size() == 0) {
            return null;
        } else {
            ArrayList<String> concernedXMLStrings = new ArrayList<>(16);
            for (String strInXml : stringsInXml) {
//                System.out.println("strInXml ---> " + strInXml);
                if (strInXml != null
                        && (strInXml.startsWith("@layout") || strInXml.startsWith("@drawable"))) {
                    String transformedStr = transformRefInXmlToResourceId(strInXml);
                    if (transformedStr == null) {
                        continue;
                    }
                    concernedXMLStrings.add(transformedStr);
                }
            }
            return concernedXMLStrings;
        }
    }

}
