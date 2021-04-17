package com.xxx.stone.ast;

import com.xxx.stone.interpreter.Environment;
import java.util.List;

/**
 * @author 0x822a5b87
 */
public class DefStatement extends AbstractSyntaxList {

    public DefStatement(List<AbstractSyntaxTree> c) {
        super(c);
    }

    public String name() {
        return ((AbstractSyntaxLeaf) child(0)).getToken().getText();
    }

    public ParameterList parameters() {
        return (ParameterList) child(1);
    }

    public BlockStatement body() {
        return (BlockStatement) child(2);
    }

    @Override
    public String toString() {
        return "(def " + name() + " " + parameters() + " " + body() + ")";
    }

    @Override
    public Object eval(Environment env) {
        // TODO 实现 def 的 eval
        return super.eval(env);
    }
}
