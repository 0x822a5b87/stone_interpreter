package com.xxx.stone.ast;

import com.xxx.stone.exception.StoneException;
import com.xxx.stone.interpreter.Environment;
import java.util.List;

/**
 * <pre>
 *     Postfix 是一个抽象类，实现类有 {@link com.xxx.stone.object.Dot}, {@link Arguments} 等。
 *     Postfix 在 EBNF 中是 {@link BasicParser#primary} 的组成部分，所以它不能执行 {@link AbstractSyntaxList#eval(Environment)}
 *     方法。
 *     它额外提供了一个 {@link #eval(Environment, Object)} 方法，这个方法会被 {@link PrimaryExpr} 的 {@link PrimaryExpr#eval(Environment)}
 *     方法调用。
 * @author 0x822a5b87
 * </pre>
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
