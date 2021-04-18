package com.xxx.stone.func;

import com.xxx.stone.ast.AbstractSyntaxList;
import com.xxx.stone.ast.AbstractSyntaxTree;
import com.xxx.stone.ast.BlockStatement;
import com.xxx.stone.ast.ParameterList;
import com.xxx.stone.interpreter.Environment;
import java.util.List;

/**
 * @author 0x822a5b87
 *
 *         闭包
 */
public class Closure extends AbstractSyntaxList {

    public Closure(List<AbstractSyntaxTree> children) {
        super(children);
    }

    public ParameterList parameters() {
        return (ParameterList) child(0);
    }

    public BlockStatement body() {
        return (BlockStatement) child(1);
    }


    @Override
    public Object eval(Environment env) {
        return new Function("closure", parameters(), body(), env);
    }
}
