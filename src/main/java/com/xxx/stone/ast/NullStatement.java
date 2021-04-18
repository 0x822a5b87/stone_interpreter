package com.xxx.stone.ast;

import com.xxx.stone.interpreter.Environment;
import java.util.List;

/**
 * @author 0x822a5b87
 */
public class NullStatement extends AbstractSyntaxList {

    public NullStatement(List<AbstractSyntaxTree> c) {
        super(c);
    }

    @Override
    public Object eval(Environment env) {
        return null;
    }
}
