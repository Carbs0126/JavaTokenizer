package cn.carbs.tokenizer.reference;

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
public class IdentifierMatcher {

    public String lastIdentifier;
    public String importPath;
    // identifier prefix
    public String iPrefix;
    // resource import path postfix
    public String rPostfix;
    // 全路径引用时，此选项不为空
    public String wholePath;
    // xxx.xxx.xxx.R
    public String standardImportStr;

    public IdentifierMatcher(String lastIdentifier, String importPath, String standardImportStr, String iPrefix, String rPostfix) {
        this.lastIdentifier = lastIdentifier;
        this.importPath = importPath;
        this.standardImportStr = standardImportStr;
        this.iPrefix = iPrefix;
        this.rPostfix = rPostfix;
    }

    public IdentifierMatcher(String importPath, String wholePath) {
        this.importPath = importPath;
        this.wholePath = wholePath;
    }

    public IdentifierMatcher() {

    }

    public IdentifierMatcher setStandardImport(String standardImportStr) {
        this.standardImportStr = standardImportStr;
        return this;
    }

    @Override
    public String toString() {
        return "IdentifierMatcher{" +
                "standardImportStr='" + standardImportStr + '\'' +
                ", importPath='" + importPath + '\'' +
                ", lastIdentifier='" + lastIdentifier + '\'' +
                ", iPrefix='" + iPrefix + '\'' +
                ", rPostfix='" + rPostfix + '\'' +
                ", wholePath='" + wholePath + '\'' +
                '}';
    }
}
