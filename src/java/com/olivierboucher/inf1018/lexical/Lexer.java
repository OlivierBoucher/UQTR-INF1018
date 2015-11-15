package com.olivierboucher.inf1018.lexical;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * Created by olivier on 2015-11-02.
 */
public class Lexer {

    private Scanner scanner;
    private Pattern tokensPattern;
    private int lastResultEndIndex = 0;

    public Lexer(File file){
        StringBuilder sb = new StringBuilder();

        for(TokenType tokenType : TokenType.values()) {
            //NOTE(Olivier): We can't make use of named groups unless we load the entire file into a matcher
            //sb.append(String.format("|(?<%s>%s)", tokenType.name(), tokenType.stringPattern));
            sb.append(String.format("|(%s)", tokenType.stringPattern));
        }
        tokensPattern = Pattern.compile(sb.substring(1)); //NOTE(Olivier): Remove the first "|"

        try {
            scanner = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Token getNextToken() throws LexerException {

        Token t = null;
        String result = scanner.findWithinHorizon(tokensPattern, 0);

        if(result != null) {
            MatchResult match = scanner.match();
            //NOTE(Olivier): Important to start at 1 because 0 is full string
            //NOTE(Olivier): That implies <=
            for(int i = 1; i <= TokenType.values().length; i++) {
                int startIndex = match.start(i);
                if( startIndex != -1) {
                    //NOTE(Olivier): Let's verify we don't skip any content
                    if (startIndex != lastResultEndIndex) {
                       throw new LexerException(new LexerError(lastResultEndIndex, startIndex));
                    }
                    //NOTE(Olivier): The previous also implies i-1
                    t = new Token(TokenType.values()[i-1], result);
                    lastResultEndIndex = match.end(i);
                    break;
                }
            }
        }
        return t;
    }
}
