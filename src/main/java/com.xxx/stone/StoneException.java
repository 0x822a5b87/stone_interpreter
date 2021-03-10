package com.xxx.stone;

import com.xxx.stone.ast.AbstractSyntaxTree;

public class StoneException extends RuntimeException {

    public StoneException(String m) {
        super(m);
    }

    public StoneException(String m, AbstractSyntaxTree t) {
        super(m + " " + t.location());
    }
}
