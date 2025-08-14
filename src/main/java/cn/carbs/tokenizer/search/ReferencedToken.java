package cn.carbs.tokenizer.search;

import cn.carbs.tokenizer.entity.SealedToken;

import java.util.ArrayList;

public class ReferencedToken {

    public IdentifierMatcher identifierMatcher;

    public String completedTokenStr;

    // R.layout.main_activity
    public String simpleReferenceStr;

    public ReferencedToken(IdentifierMatcher identifierMatcher, ArrayList<SealedToken> completeIdentifierToken) {
        this.identifierMatcher = identifierMatcher;
        StringBuilder tempStringBuilder = new StringBuilder();
        for (int m = 0; m < completeIdentifierToken.size(); m++) {
            tempStringBuilder.append(completeIdentifierToken.get(m).literalStr);
        }
        this.completedTokenStr = tempStringBuilder.toString();
    }

    public ReferencedToken(IdentifierMatcher identifierMatcher, StringBuilder completeTokenStringBuilder) {
        this.identifierMatcher = identifierMatcher;
        this.completedTokenStr = completeTokenStringBuilder.toString();
    }

    public ReferencedToken(IdentifierMatcher identifierMatcher, String completeTokenStr) {
        this.identifierMatcher = identifierMatcher;
        this.completedTokenStr = completeTokenStr;
    }

    public ReferencedToken setStandardSimpleReference(String simpleReferenceStr) {
        this.simpleReferenceStr = simpleReferenceStr;
        return this;
    }

    @Override
    public String toString() {
        return ">>> ReferencedToken{" +
                "identifierMatcher=" + identifierMatcher +
                ", completedTokenStr='" + completedTokenStr + '\'' +
                ", simpleReferenceStr='" + simpleReferenceStr + '\'' +
                '}';
    }
}
