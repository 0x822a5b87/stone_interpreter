package com.xxx.stone.ast;

import static com.xxx.stone.interpreter.BasicEvaluator.FALSE;

import com.xxx.stone.interpreter.Environment;
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

    @Override
    public Object eval(Environment env) {
        // condition() 返回条件对应的抽象语法树，如果该语法树的执行结果为真则执行 block 中的内容。
        Object c = condition().eval(env);
        if (c instanceof Integer && ((Integer) c) != FALSE) {
            return thenBlock().eval(env);
        } else {
            AbstractSyntaxTree b = elseBlock();
            if (b == null) {
                return 0;
            } else {
                return b.eval(env);
            }
        }
    }
}
