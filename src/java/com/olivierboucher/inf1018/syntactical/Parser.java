package com.olivierboucher.inf1018.syntactical;

import com.olivierboucher.inf1018.lexical.Lexer;
import com.olivierboucher.inf1018.lexical.LexerException;
import com.olivierboucher.inf1018.lexical.Token;
import com.olivierboucher.inf1018.lexical.TokenType;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by olivier on 2015-11-15.
 */
public class Parser {
    private Lexer lexer;
    private Token currentToken;
    private Token previousToken;
    private String currentProcedureId = "";
    private ArrayList<String> declaredVariables = new ArrayList<>();

    public Parser(File file) {
        lexer = new Lexer(file);
    }

    private void nextToken() throws LexerException, ParserException {
        previousToken = currentToken;
        currentToken = lexer.getNextToken();
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
        expect(TokenType.WHITESPACE);
        expect(TokenType.IDENTIFICATOR);
        currentProcedureId = previousToken.data;
        expect(TokenType.WHITESPACE);
        declarations();
        affectations();
        expect(TokenType.IDENTIFICATOR);
        //Semantic rule
        if(!previousToken.data.equals(currentProcedureId)){
            throw new ParserException("Procedure id does not match declaration.");
        }
    }

    private void declarations() throws LexerException, ParserException {
        while(accept(TokenType.DECLARATION_START)){
            accept(TokenType.WHITESPACE);
            declaration();
            expect(TokenType.SEMICOLON);
            accept(TokenType.WHITESPACE);
        }
    }

    private void declaration() throws LexerException, ParserException {
        variable();
        declaredVariables.add(previousToken.data); //Keep track of declared variables
        accept(TokenType.WHITESPACE);
        expect(TokenType.COLON);
        accept(TokenType.WHITESPACE);
        type();
        accept(TokenType.WHITESPACE);
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
        while(accept(TokenType.SEMICOLON) && (accept(TokenType.WHITESPACE) && !accept(TokenType.FUNC_END)));
        accept(TokenType.WHITESPACE);
    }

    private void affectation() throws LexerException, ParserException {
        accept(TokenType.WHITESPACE);
        variable();
        if(!declaredVariables.contains(previousToken.data)){
            throw new ParserException(String.format("\"%s\" was not declared prior to usage.", previousToken.data));
        }
        accept(TokenType.WHITESPACE);
        expect(TokenType.AFFECTATION);
        accept(TokenType.WHITESPACE);
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
        accept(TokenType.WHITESPACE);
        if(accept(TokenType.IDENTIFICATOR)){
            accept(TokenType.WHITESPACE);
            return;
        }

        if(accept(TokenType.NUMBER)){
            accept(TokenType.WHITESPACE);
            return;
        }
        exprArithm();
    }

    public void parse() throws ParserException, LexerException {
        nextToken();
        try {
            accept(TokenType.FUNC_START);
            do {
                procedure();
            }
            while(currentToken != null && (accept(TokenType.WHITESPACE) && accept(TokenType.FUNC_START)));
        }
        catch (NullPointerException e){
            throw new ParserException("Reached the end of the file but was expecting more");
        }
    }
}
