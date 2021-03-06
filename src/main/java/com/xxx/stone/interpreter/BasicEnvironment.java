package com.xxx.stone.interpreter;

import com.xxx.stone.exception.StoneException;
import com.xxx.stone.optimizer.Symbols;
import com.xxx.stone.vm.Code;
import com.xxx.stone.vm.Vm;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 0x822a5b87
 *
 * 基本环境变量管理，用变量名做为 key。很显然，我们缺乏 namespace 的管理
 */
public class BasicEnvironment implements Environment {

    protected Map<String, Object> values;

    public BasicEnvironment() {
        values = new HashMap<>();
    }

    @Override
    public Symbols symbols() {
        throw new StoneException("no symbols!");
    }

    @Override
    public Vm stoneVm() {
        throw new StoneException("no such stone vm");
    }

    @Override
    public Code code() {
        throw new StoneException("no such code");
    }


    @Override
    public void put(String name, Object value) {
        values.put(name, value);
    }

    @Override
    public Object get(String name) {
        return values.get(name);
    }

    @Override
    public void putNew(String name, Object value) {

    }

    @Override
    public Environment where(String name) {
        return null;
    }

    @Override
    public void setOuter(Environment e) {

    }

    @Override
    public Object get(int nest, int index) {
        throw new StoneException("unsupported method!");
    }

    @Override
    public void put(int nest, int index, Object value) {
        throw new StoneException("unsupported method!");
    }
}
