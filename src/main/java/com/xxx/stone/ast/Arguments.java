package com.xxx.stone.ast;

import java.util.List;

/**
 * @author 0x822a5b87
 */
public class Arguments extends Postfix {

    public Arguments(List<AbstractSyntaxTree> c) {
        super(c);
    }

    public int size() {
        return numChildren();
    }
}
