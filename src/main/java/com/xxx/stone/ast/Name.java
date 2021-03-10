package com.xxx.stone.ast;

/**
 * @author 0x822a5b87
 *
 * 操作符
 */
public class Name extends AbstractSyntaxLeaf {

    public Name(Token token) {
        super(token);
    }

    public String value() {
        return token.getText();
    }
}
