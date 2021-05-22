package com.xxx.stone.interpreter;

import com.xxx.stone.optimizer.Symbols;
import com.xxx.stone.vm.Code;
import com.xxx.stone.vm.Vm;

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
     * 返回 {@link Symbols}
     * @return symbols
     */
    Symbols symbols();

    /**
     * 返回环境对应的虚拟机
     * @return 虚拟机
     */
    Vm stoneVm();

    /**
     * 返回虚拟机器语言转换过程中所有需要的信息
     * @return Code
     */
    Code code();

    /**
     * 为 env 添加数据
     *
     * @param name 变量名
     * @param value 变量值
     */
    void put(String name, Object value);

    /**
     * 从 env 获取数据
     *
     * @param name 变量名
     * @return 变量值
     */
    Object get(String name);

    /**
     * 增加新的变量
     *
     * @param name name
     * @param value value
     */
    void putNew(String name, Object value);

    /**
     * 查找变量所在的 env
     *
     * @param name name
     * @return 变量所在的 env
     */
    Environment where(String name);

    /**
     * 设置外层 env
     *
     * @param e 外层env
     */
    void setOuter(Environment e);

    /**
     * {@link com.xxx.stone.optimizer.ArrayEnvironment#get(int, int)} 优化的方法
     *
     * @param nest 嵌套层数
     * @param index 索引
     * @return 对象的值
     */
    Object get(int nest, int index);

    /**
     * {@link com.xxx.stone.optimizer.ArrayEnvironment#put(int, int, Object)} 优化的 put 方法
     *
     * @param nest 嵌套的层数
     * @param index 索引
     * @param value 更新的值
     */
    void put(int nest, int index, Object value);
}
