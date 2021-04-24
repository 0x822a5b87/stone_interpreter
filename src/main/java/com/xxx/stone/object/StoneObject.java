package com.xxx.stone.object;

import com.xxx.stone.exception.AccessException;
import com.xxx.stone.interpreter.Environment;

/**
 * <pre>
 * stone 语言的对象，对 {@link Dot} 的调用，如果右侧不是关键字 <em>new</em> 的话，将返回本对象。
 * @author 0x822a5b87
 * </pre>
 */
public class StoneObject {

    private Environment environment;

    public StoneObject(Environment environment) {
        this.environment = environment;
    }

    public Object read(String member) {
        return findEnvironment(member).get(member);
    }

    public void write(String member, Object value) {
        findEnvironment(member).putNew(member, value);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * class 只能访问自己内部的对象，所以使用 {@link Environment} 是错误的
     * @param member 对象
     * @return 对象
     */
    public Environment findEnvironment(String member) {
        Environment env = environment.where(member);
        if (env != null && env == environment) {
            return env;
        } else {
            throw new AccessException("unknown member : " + member);
        }
    }

    @Override
    public String toString() {
        return "<object:" + environment + ">";
    }
}