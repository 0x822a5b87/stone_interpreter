package com.xxx.stone.ast;

import com.xxx.stone.interpreter.Environment;
import java.util.List;

/**
 * @author 0x822a5b87
 */
public class BlockStatement extends AbstractSyntaxList {

    public BlockStatement(List<AbstractSyntaxTree> c) {
        super(c);
    }


    @Override
    public Object eval(Environment env) {
        Object result = 0;
        for (AbstractSyntaxTree t : this) {
            if (!(t instanceof NullStatement)) {
                result = t.eval(env);
            }
        }
        return result;
    }
}
