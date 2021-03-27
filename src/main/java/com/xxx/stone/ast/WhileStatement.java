package com.xxx.stone.ast;

import java.util.List;

/**
 * @author 0x822a5b87
 */
public class WhileStatement extends AbstractSyntaxList {

    public WhileStatement(List<AbstractSyntaxTree> c) {
        super(c);
    }

    public AbstractSyntaxTree condition() {
        return child(0);
    }

    public AbstractSyntaxTree body() {
        return child(1);
    }

    @Override
    public String toString() {
        return "(while " + condition() + " " + body() + ")";
    }
}
