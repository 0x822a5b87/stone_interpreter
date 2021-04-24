package com.xxx.stone.ast;

import com.xxx.stone.exception.StoneException;
import com.xxx.stone.interpreter.Environment;
import java.util.List;

public class NegativeExpr extends AbstractSyntaxList {

    public NegativeExpr(List<AbstractSyntaxTree> c) {
        super(c);
    }

    public AbstractSyntaxTree operand() {
        return child(0);
    }

    @Override
    public String toString() {
        return "-" + operand();
    }

    @Override
    public Object eval(Environment env) {
        Object v = operand().eval(env);
        if (v instanceof Integer) {
            return -((Integer) v);
        } else {
            throw new StoneException("bad type for -", this);
        }
    }
}
