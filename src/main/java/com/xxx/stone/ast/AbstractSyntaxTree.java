package com.xxx.stone.ast;

import com.xxx.stone.exception.StoneException;
import com.xxx.stone.interpreter.Environment;
import com.xxx.stone.optimizer.Symbols;
import java.util.Iterator;

/**
 * @author 0x822a5b87
 *
 */
public abstract class AbstractSyntaxTree implements Iterable<AbstractSyntaxTree> {

    /**
     * 返回第 i 个子节点
     * @param i i
     * @return 子节点
     */
    public abstract AbstractSyntaxTree child(int i);

    /**
     * 返回子节点个数
     *
     * @return 子节点个数
     */
    public abstract int numChildren();

    /**
     * 返回所有的子节点
     *
     * @return 所有的子节点
     */
    public abstract Iterator<AbstractSyntaxTree> children();

    /**
     * 返回一个用于表示抽象语法书节点在程序内所处位置的字符串
     *
     * @return 节点在程序内所处位置
     */
    public abstract String location();

    @Override
    public Iterator<AbstractSyntaxTree> iterator() {
        return children();
    }

    /**
     * 执行 AST 节点。
     * @param env 环境
     * @return 执行结果
     */
    public abstract Object eval(Environment env);

    /**
     * lookup 方法从抽象语法树的根节点开始依次遍历所有的节点最终到达叶子节点。
     * lookup 方法在遍历的时候如果发现了赋值表达式左边的变量名，就会查找 {@link Symbols} 对象，
     * 判断该对象是否第一次出现、尚未记录。
     * @param symbols symbols
     */
    public void lookup(Symbols symbols) {

    }
}
