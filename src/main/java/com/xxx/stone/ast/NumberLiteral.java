package com.xxx.stone.ast;

/**
 * 数字字面量
 *
 * @author 0x822a5b87
 */
public class NumberLiteral extends AbstractSyntaxLeaf {

    public NumberLiteral(Token token) {
        super(token);
    }

    public int number() {
        return token.getNumber();
    }
}
