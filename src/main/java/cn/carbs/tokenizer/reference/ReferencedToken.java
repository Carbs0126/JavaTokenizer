package cn.carbs.tokenizer.reference;

import cn.carbs.tokenizer.entity.SealedToken;
import cn.carbs.tokenizer.type.TokenType;

import java.util.ArrayList;

public class ReferencedToken {

    public TokenType tokenType = TokenType.None;

    public IdentifierMatcher identifierMatcher;

    public String completedTokenStr;

    // R.layout.main_activity
    public String standardReferenceStr;

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

    public ReferencedToken setStandardReferenceStr(String standardReferenceStr) {
        this.standardReferenceStr = standardReferenceStr;
        return this;
    }

    public ReferencedToken setIdentifierMatcher(IdentifierMatcher identifierMatcher) {
        this.identifierMatcher = identifierMatcher;
        return this;
    }

    @Override
    public String toString() {
        return ">>> ReferencedToken{" +
                "identifierMatcher=" + identifierMatcher +
                ", standardReferenceStr='" + standardReferenceStr + '\'' +
                ", completedTokenStr='" + completedTokenStr + '\'' +
                ", tokenType=" + tokenType.name() +
                '}';
    }

    @Override
    public int hashCode() {
        if (identifierMatcher != null && identifierMatcher.standardImportStr != null && standardReferenceStr != null) {
            return identifierMatcher.standardImportStr.hashCode() + standardReferenceStr.hashCode();
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
