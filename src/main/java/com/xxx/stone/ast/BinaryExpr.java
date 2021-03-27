package com.xxx.stone.ast;

import java.util.List;

/**
 * @author 0x822a5b87
 *
 * 二元操作符
 */
public class BinaryExpr extends AbstractSyntaxList {

    public BinaryExpr(List<AbstractSyntaxTree> children) {
        super(children);
    }

    public String operator() {
        return ((AbstractSyntaxLeaf) children.get(1)).getToken().getText();
    }

    public AbstractSyntaxTree left() {
        return children.get(0);
    }

    public AbstractSyntaxTree right() {
        return children.get(2);
    }
}
