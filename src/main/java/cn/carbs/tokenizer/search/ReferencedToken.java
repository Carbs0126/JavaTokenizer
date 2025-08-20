package cn.carbs.tokenizer.search;

import cn.carbs.tokenizer.entity.SealedToken;
import cn.carbs.tokenizer.type.TokenType;

import java.util.ArrayList;

public class ReferencedToken {

    public TokenType tokenType = TokenType.None;

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
        this.tokenType = TokenType.Identifier;
    }

    public ReferencedToken(IdentifierMatcher identifierMatcher, StringBuilder completeTokenStringBuilder) {
        this.identifierMatcher = identifierMatcher;
        this.completedTokenStr = completeTokenStringBuilder.toString();
        this.tokenType = TokenType.Identifier;
    }

    public ReferencedToken(IdentifierMatcher identifierMatcher, String completeTokenStr) {
        this.identifierMatcher = identifierMatcher;
        this.completedTokenStr = completeTokenStr;
        this.tokenType = TokenType.Identifier;
    }

    public ReferencedToken(String str) {
        this.completedTokenStr = str;
        this.tokenType = TokenType.String;
    }

    public ReferencedToken setStandardSimpleReference(String simpleReferenceStr) {
        this.simpleReferenceStr = simpleReferenceStr;
        return this;
    }

    public ReferencedToken setIdentifierMatcher(IdentifierMatcher identifierMatcher) {
        this.identifierMatcher = identifierMatcher;
        return this;
    }

    @Override
    public String toString() {
        return ">>> ReferencedToken{" +
                "tokenType=" + tokenType.name() +
                ", identifierMatcher=" + identifierMatcher +
                ", completedTokenStr='" + completedTokenStr + '\'' +
                ", simpleReferenceStr='" + simpleReferenceStr + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        if (identifierMatcher != null && identifierMatcher.standardImport != null && simpleReferenceStr != null) {
            return identifierMatcher.standardImport.hashCode() + simpleReferenceStr.hashCode();
        } else if (completedTokenStr != null) {
            return completedTokenStr.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ReferencedToken)) {
            return false;
        }
        ReferencedToken other = (ReferencedToken) obj;
        return this.hashCode() == other.hashCode();
    }
}
