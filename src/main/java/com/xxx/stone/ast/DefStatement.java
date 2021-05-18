package com.xxx.stone.ast;

import com.xxx.stone.func.Closure;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.optimizer.OptFunction;
import com.xxx.stone.optimizer.Symbols;
import java.util.List;

/**
 * @author 0x822a5b87
 */
public class DefStatement extends AbstractSyntaxList {

    /**
     * DefStatement 也是一个访问量非常大，所以也需要做个优化
     */
    protected int index;
    protected int size;

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
    public Object eval(Environment callerEnv) {
        callerEnv.put(0, index, new OptFunction(name(), parameters(), body(), callerEnv, size));
        return "<func: [" + name() + "]>";
    }

    @Override
    public void lookup(Symbols symbols) {
        index = symbols.putNew(name());
        size = Closure.lookup(symbols, parameters(), body());
    }
}
