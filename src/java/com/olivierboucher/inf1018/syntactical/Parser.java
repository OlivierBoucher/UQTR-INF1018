package com.olivierboucher.inf1018.syntactical;

import com.olivierboucher.inf1018.lexical.Lexer;
import com.olivierboucher.inf1018.lexical.LexerException;
import com.olivierboucher.inf1018.lexical.Token;
import com.olivierboucher.inf1018.lexical.TokenType;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import com.sun.org.apache.xpath.internal.operations.Variable;

import java.io.File;

/**
 * Created by olivier on 2015-11-15.
 */
public class Parser {
    private Lexer lexer;
    private Token currentToken;

    public Parser(File file) {
        lexer = new Lexer(file);
    }

    private void nextToken() throws LexerException, ParserException {
        currentToken = lexer.getNextToken();
        if(currentToken == null){
            throw new ParserException("Reached the end of the file but was expecting more.");
        }
    }

    private boolean accept(TokenType t) throws LexerException, ParserException {
        if(currentToken.type == t) {
            nextToken();
            return true;
        }
        return false;
    }

    private void expect(TokenType t) throws LexerException, ParserException {
        if(!accept(t)){
            throw new ParserException(String.format("Unexpected token: %s was expecting type %s.", currentToken,  t));
        }
    }

    private void procedure() throws LexerException, ParserException {
        expect(TokenType.FUNC_START);
        expect(TokenType.IDENTIFICATOR);
        declarations();
        affectations();
        expect(TokenType.IDENTIFICATOR);
    }

    private void declarations() throws LexerException, ParserException {
        while(accept(TokenType.DECLARATION_START)){
            declaration();
            expect(TokenType.SEMICOLON);
        }
    }

    private void declaration() throws LexerException, ParserException {
        variable();
        expect(TokenType.COLON);
        type();
    }

    private void variable() throws LexerException, ParserException {
        expect(TokenType.IDENTIFICATOR);
    }

    private void type() throws LexerException, ParserException {
        expect(TokenType.TYPE);
    }

    private void affectations() throws LexerException, ParserException {
        do{
            affectation();
        }
        while(accept(TokenType.SEMICOLON) && !accept(TokenType.FUNC_END));

    }

    private void affectation() throws LexerException, ParserException {
        variable();
        expect(TokenType.AFFECTATION);
        exprArithm();
    }

    private void exprArithm() throws LexerException, ParserException {
        do {
            terme();
        }
        while (accept(TokenType.PLUS) || accept(TokenType.MINUS));
    }

    private void terme() throws LexerException, ParserException {
        do {
            facteur();
        }
        while (accept(TokenType.FACTOR) || accept(TokenType.DIVIDE));
    }

    private void facteur() throws LexerException, ParserException {
        if(accept(TokenType.IDENTIFICATOR)){
            return;
        }

        if(accept(TokenType.NUMBER)){
            return;
        }

        exprArithm();
    }

    public void parse() throws ParserException, LexerException {
        nextToken();
        procedure();
    }
}
