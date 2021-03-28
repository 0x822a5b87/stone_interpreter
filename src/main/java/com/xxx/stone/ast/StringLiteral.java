package com.xxx.stone.ast;

import com.xxx.stone.interpreter.Environment;

public class StringLiteral extends AbstractSyntaxLeaf {

    public StringLiteral(Token t) {
        super(t);
    }

    public String value() {
        return getToken().getText();
    }

    @Override
    public Object eval(Environment e) {
        return value();
    }
}
