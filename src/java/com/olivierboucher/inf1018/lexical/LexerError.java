package com.olivierboucher.inf1018.lexical;

/**
 * Created by olivier on 2015-11-15.
 */
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