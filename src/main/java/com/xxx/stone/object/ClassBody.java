package com.xxx.stone.object;

import com.xxx.stone.ast.AbstractSyntaxList;
import com.xxx.stone.ast.AbstractSyntaxTree;
import com.xxx.stone.interpreter.Environment;
import java.util.List;

/**
 * <pre>
 * class body
 *
 * @author 0x822a5b87
 * </pre>
 */
public class ClassBody extends AbstractSyntaxList {

    public ClassBody(List<AbstractSyntaxTree> children) {
        super(children);
    }

    /**
     * stone 语言 class 的初始化，就是将 classBody 的语句从头到尾执行。
     * @param env 执行环境
     * @return 无返回值
     */
    @Override
    public Object eval(Environment env) {
        for (AbstractSyntaxTree tree : this) {
            tree.eval(env);
        }
        return null;
    }
}
