package com.xxx.stone.ast;

public class StringLiteral extends AbstractSyntaxLeaf {

    public StringLiteral(Token t) {
        super(t);
    }

    public String value() {
        return getToken().getText();
    }
}
