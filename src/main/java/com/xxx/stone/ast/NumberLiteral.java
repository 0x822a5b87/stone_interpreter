package com.xxx.stone.ast;

import com.xxx.stone.interpreter.Environment;

/**
 * 数字字面量
 *
 * @author 0x822a5b87
 */
public class NumberLiteral extends AbstractSyntaxLeaf {

    public NumberLiteral(Token token) {
        super(token);
    }

    public int value() {
        return token.getNumber();
    }

    @Override
    public Object eval(Environment e) {
        return value();
    }
}
