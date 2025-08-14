package cn.carbs.tokenizer.search;

import cn.carbs.tokenizer.entity.SealedToken;

import java.util.ArrayList;

public class ReferencedToken {

    public IdentifierMatcher identifierMatcher;

    public String completedTokenStr;

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

    @Override
    public String toString() {
        return ">>> ReferencedToken{" +
                "identifierMatcher=" + identifierMatcher +
                ", completedTokenStr='" + completedTokenStr + '\'' +
                '}';
    }
}
