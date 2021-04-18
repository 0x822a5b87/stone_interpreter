package com.xxx.stone.ast;

import com.xxx.stone.StoneException;
import com.xxx.stone.interpreter.Environment;
import java.util.List;

/**
 * @author 0x822a5b87
 */
public abstract class Postfix extends AbstractSyntaxList {

    public Postfix(List<AbstractSyntaxTree> c) {
        super(c);
    }

    /**
     * 这里不直接实现 eval，是为了以后的扩展。
     * 后续如果需要支持数组，直接增加一个 ArrayRef 实现 Postfix 即可。
     * @param env 执行环境
     * @param obj 执行对象
     * @return 执行结果
     */
    public abstract Object eval(Environment env, Object obj);

    @Override
    public Object eval(Environment env) {
        throw new StoneException("postfix should call with function!");
    }
}
