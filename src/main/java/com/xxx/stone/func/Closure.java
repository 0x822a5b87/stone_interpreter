package com.xxx.stone.func;

import com.xxx.stone.ast.AbstractSyntaxList;
import com.xxx.stone.ast.AbstractSyntaxTree;
import com.xxx.stone.ast.BlockStatement;
import com.xxx.stone.ast.ParameterList;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.optimizer.OptFunction;
import com.xxx.stone.optimizer.Symbols;
import java.util.List;

/**
 * 闭包
 *
 * @author 0x822a5b87
 */
public class Closure extends AbstractSyntaxList {

    private int size = -1;

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
        return new OptFunction("opt-closure", parameters(), body(), env, size);
    }

    @Override
    public void lookup(Symbols symbols) {
        size = lookup(symbols, parameters(), body());
    }

    /**
     * {@link com.xxx.stone.ast.DefStatement#lookup(Symbols)} 和 {@link #lookup(Symbols)}
     * 与 {@link AbstractSyntaxList#lookup(Symbols)} 的区别是，它的 nest 层级改变了。
     *
     * @return 新的 {@link Symbols#size()}
     */
    public static int lookup(Symbols symbols, ParameterList params, BlockStatement body) {
        Symbols newSymbols = new Symbols(symbols);
        params.lookup(newSymbols);
        body.lookup(newSymbols);
        return newSymbols.size();
    }
}
