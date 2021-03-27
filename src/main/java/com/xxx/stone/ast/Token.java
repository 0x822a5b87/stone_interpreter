package com.xxx.stone.ast;

import com.xxx.stone.StoneException;

/**
 * @author 0x822a5b87
 */
public abstract class Token {

    public static final Token  EOF = new Token(-1) {
    };
    public static final String EOL = "\\n";

    private final int lineNumber;

    protected Token(int line) {
        this.lineNumber = line;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public boolean isIdentifier() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    public int getNumber() {
        throw new StoneException("not number token!");
    }

    public String getText() {
        return "";
    }
}
