package com.example.VoiceTasker.utils;

import org.springframework.data.util.Pair;

import java.util.Set;

public class StemmerHelper {
    public static Pair<String, String> getPlaceHolders(String text, Set<String> protectedWords){
        String variable=null;
        for (String term: protectedWords) {
            if(text.contains(term))
            {
                variable=term;
                text=text.replace(term, "_PLACEHOLDER_");
            }
        }
        return Pair.of(text,variable);
    }
}
