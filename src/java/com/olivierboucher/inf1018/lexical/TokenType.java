package com.olivierboucher.inf1018.lexical;

/**
 * Created by olivier on 2015-11-15.
 */
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
