package com.example.VoiceTasker.Analyzers;

import com.example.VoiceTasker.utils.StemmerHelper;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class MyAnalyzer extends Analyzer {

    private final CharArraySet stopwords;
    private final Set<String> protectedTerms;

    public MyAnalyzer(Set<String> stopwordsList, Set<String> protectedTermsList) {
        this.stopwords = new CharArraySet(stopwordsList, true);
        this.protectedTerms = new HashSet<>(protectedTermsList);
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();
        TokenStream tokenStream = new StopFilter(tokenizer, stopwords);
        tokenStream=new TokenFilter(tokenStream) {
            @Override
            public boolean incrementToken() throws IOException {
               if(!input.incrementToken()){
                   return false;
               }
               CharTermAttribute termAttribute = getAttribute(CharTermAttribute.class);
               String term = termAttribute.toString();
               if(protectedTerms.contains(term)){
                   return true;
               }
               return true;
            }
        };
        tokenStream = new PorterStemFilter(tokenStream);
        return new TokenStreamComponents(tokenizer, tokenStream);
    }

    public String stem(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        Pair<String,String> placeHolders =StemmerHelper.getPlaceHolders(text, protectedTerms);
        System.out.println(text);
        text=placeHolders.getFirst();
        try (TokenStream tokenStream = tokenStream(null, new StringReader(text))) {
            StringBuilder result = new StringBuilder();
            CharTermAttribute charTermAttr = tokenStream.getAttribute(CharTermAttribute.class);

            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                result.append(charTermAttr.toString()).append(" ");
            }
            tokenStream.end();
            String stemmedText = result.toString();
            stemmedText = stemmedText.replace("_PLACEHOLDER_", placeHolders.getSecond());
            return stemmedText.trim();
        } catch (Exception e) {
            throw new RuntimeException("Error during text processing", e);
        }
    }
}
