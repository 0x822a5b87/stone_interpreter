package com.xxx.stone;

public class StoneException extends RuntimeException {

    public StoneException(String m) {
        super(m);
    }

    public StoneException(String m, AbstractSyntaxTree t) {
        super(m + " " + t.location());
    }
}
