package com.xxx.stone.ast;

import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.optimizer.Symbols;
import java.util.List;

/**
 * {@link #eval(Environment, int, Object)} 和 {@link #lookup(Symbols)} 方法的调用者都是
 * {@link com.xxx.stone.func.Closure#lookup(Symbols, ParameterList, BlockStatement)}
 * 所以无需构造 nest。
 *
 * 这个设计是合理的，因为它和 {@link BlockStatement} 共用一个 {@link Symbols}
 *
 * @author 0x822a5b87
 */
public class ParameterList extends AbstractSyntaxList {

    protected int[] offsets = null;

    public ParameterList(List<AbstractSyntaxTree> c) {
        super(c);
    }

    public String name(int i) {
        return ((AbstractSyntaxLeaf) child(i)).getToken().getText();
    }

    public int size() {
        return numChildren();
    }

    public void eval(Environment callerEnv, int index, Object value) {
        callerEnv.put(0, offsets[index], value);
    }

    @Override
    public void lookup(Symbols symbols) {
        int s = size();
        offsets = new int[s];
        for (int i = 0; i < numChildren(); i++) {
            // 这里只是初始化了 offsets
            offsets[i] = symbols.putNew(name(i));
         }
    }
}
