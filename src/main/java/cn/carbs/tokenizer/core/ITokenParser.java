package cn.carbs.tokenizer.core;

import cn.carbs.tokenizer.entity.SealedToken;

import java.util.ArrayList;

public interface ITokenParser {

    ArrayList<SealedToken> getTokens(ArrayList<String> arrayList);

}