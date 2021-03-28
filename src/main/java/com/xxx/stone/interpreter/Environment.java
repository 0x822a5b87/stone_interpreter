package com.xxx.stone.interpreter;

/**
 * 执行器，在生成 AST 之后我们需要执行代码得到最终的结果。
 * 我们只需要遍历生成的 AST，并执行每个节点得到每个节点的结果，当遍历回到根节点的时候就是
 * 代码执行完毕的时候。
 *
 * 在执行代码的时候我们需要一个上下文环境，比如对于下面的代码：
 *
 * y * 10
 *
 * 我们必须让程序知道 y 的实际值是多少
 *
 * @author 0x822a5b87
 */
public interface Environment {

    /**
     * 为 env 添加数据
     * @param name      变量名
     * @param value     变量值
     */
    void put(String name, Object value);

    /**
     * 从 env 获取数据
     * @param name 变量名
     * @return     变量值
     */
    Object get(String name);
}
