package com.xxx.stone.ast;

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
}
