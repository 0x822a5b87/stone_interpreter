package com.xxx.stone.ast;

import java.util.List;

/**
 * @author 0x822a5b87
 *
 * primary expression
 */
public class PrimaryExpr extends AbstractSyntaxList {

    public PrimaryExpr(List<AbstractSyntaxTree> c) {
        super(c);
    }

    public static AbstractSyntaxTree create(List<AbstractSyntaxTree> c) {
        return c.size() == 1 ? c.get(0) : new PrimaryExpr(c);
    }
}
