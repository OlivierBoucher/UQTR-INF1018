package com.olivierboucher.inf1018.lexical;

/**
 * Created by olivier on 2015-11-15.
 */
public class LexerException extends Exception {
    private LexerError error;
    public LexerException(LexerError error) {
        this.error = error;
    }

    public LexerError getError() {
        return error;
    }
}
