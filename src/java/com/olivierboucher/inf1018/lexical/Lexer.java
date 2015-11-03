package com.olivierboucher.inf1018.lexical;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

/**
 * Created by olivier on 2015-11-02.
 */
public class Lexer {

    public enum TokenType {

        TYPE("entier|reel"),
        COLON(":"),
        SEMICOLON(";"),
        AFFECTATION("="),
        WHITESPACE("[ \t\f\r\n]+"),
        DECLARATION_START("declare"),
        FUNC_START("Procedure"),
        FUNC_END("Fin_Procedure"),
        IDENTIFICATOR("[a-zA-Z][a-zA-Z0-9_]{0,7}"), //NOTE(Olivier): We only allow the underscore special character
        NUMBER("[1-9][0-9]?"); //NOTE(Olivier): A number cannot begin by zero

        public final String stringPattern;

        TokenType(String pattern) {
            this.stringPattern = pattern;
        }
    }

    public class Token {
        public final TokenType type;
        public final String data;

        public Token(TokenType type, String data) {
            this.type = type;
            this.data = data;
        }

        @Override
        public String toString() {
            return String.format("%s -> %s", type.name(), data);
        }
    }

    public class LexerException extends Exception {
        private List<LexerError> errors;
        public LexerException(List<LexerError> errors) {
            this.errors = errors;
        }

        public List<LexerError> getErrors() {
            return errors;
        }
    }

    public class LexerError {
        private int startIndex;
        private int endIndex;

        public LexerError(int startIndex, int endIndex) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }
    }


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

    public List<Token> getNextTokens(int amount) throws LexerException{

        ArrayList<Token> tokens = new ArrayList<>();
        ArrayList<LexerError> errors = new ArrayList<>();

        String result = "";

        while ((amount < 0 || tokens.size() < amount) && result != null) {
            result = scanner.findWithinHorizon(tokensPattern, 0);
            if(result != null) {
                MatchResult match = scanner.match();
                //NOTE(Olivier): Important to start at 1 because 0 is full string
                //NOTE(Olivier): That implies <=
                for(int i = 1; i <= TokenType.values().length; i++) {
                    int startIndex = match.start(i);
                    if( startIndex != -1) {
                        //NOTE(Olivier): Let's verify we don't skip any content
                        if (startIndex != lastResultEndIndex) {
                            errors.add(new LexerError(lastResultEndIndex, startIndex));
                        }
                        //NOTE(Olivier): The previous also implies i-1
                        Token token = new Token(TokenType.values()[i-1], result);
                        tokens.add(token);
                        lastResultEndIndex = match.end(i);
                        break;
                    }
                }
            }
            //NOTE(Olivier): If we encountered any errors, we throw them
            if(errors.size() > 0) {
                throw new LexerException(errors);
            }
        }
        return tokens;
    }

    public List<Token> getAllTokens() throws LexerException {
        return getNextTokens(-1);
    }
}
