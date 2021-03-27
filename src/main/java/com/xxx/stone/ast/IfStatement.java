package com.xxx.stone.ast;

import java.util.List;

/**
 * @author 0x822a5b87
 */
public class IfStatement extends AbstractSyntaxList {

    public IfStatement(List<AbstractSyntaxTree> c) {
        super(c);
    }

    public AbstractSyntaxTree condition() {
        return child(0);
    }

    public AbstractSyntaxTree thenBlock() {
        return child(1);
    }

    public AbstractSyntaxTree elseBlock() {
        return numChildren() > 2 ? child(2) : null;
    }

    @Override
    public String toString() {
        return "(if " + condition() + " " + thenBlock()
               + " else " + elseBlock() + ")";
    }
}
