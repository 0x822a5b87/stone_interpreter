package com.xxx.stone.ast;

import com.xxx.stone.StoneException;
import com.xxx.stone.interpreter.Environment;

/**
 * @author 0x822a5b87
 *
 * 操作符
 */
public class Name extends AbstractSyntaxLeaf {

    public Name(Token token) {
        super(token);
    }

    public String name() {
        return token.getText();
    }

    @Override
    public Object eval(Environment env) {
        Object value = env.get(name());
        if (value == null) {
            throw new StoneException("undefined name: " + name(), this);
        } else {
            return value;
        }
    }
}
