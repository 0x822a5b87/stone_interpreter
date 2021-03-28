package com.xxx.stone.ast;

import static com.xxx.stone.interpreter.BasicEvaluator.FALSE;

import com.xxx.stone.interpreter.Environment;
import java.util.List;

/**
 * @author 0x822a5b87
 */
public class WhileStatement extends AbstractSyntaxList {

    public WhileStatement(List<AbstractSyntaxTree> c) {
        super(c);
    }

    public AbstractSyntaxTree condition() {
        return child(0);
    }

    public AbstractSyntaxTree body() {
        return child(1);
    }

    @Override
    public String toString() {
        return "(while " + condition() + " " + body() + ")";
    }

    @Override
    public Object eval(Environment env) {
        Object result = 0;
        for (; ; ) {
            Object c = condition().eval(env);
            // 如果 while 条件判断为真则在子节点上继续执行 eval
            if (c instanceof Integer && (Integer) c == FALSE) {
                return result;
            } else {
                result = body().eval(env);
            }
        }
    }
}
