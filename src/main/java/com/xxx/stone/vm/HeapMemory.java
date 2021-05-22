package com.xxx.stone.vm;

/**
 * stone 虚拟机使用的堆区。
 *
 * @author 0x822a5b87
 */
public interface HeapMemory {

    /**
     * 根据索引从堆中读取数据
     * @param index index
     * @return object
     */
    Object read(int index);

    /**
     * 向堆中写入一个数据
     * @param index index
     * @param value 写入的对象
     */
    void write(int index, Object value);
}
