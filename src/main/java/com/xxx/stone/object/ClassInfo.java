package com.xxx.stone.object;

import com.xxx.stone.exception.StoneException;
import com.xxx.stone.interpreter.Environment;

/**
 * <pre>
 * {@link ClassStatement} 对象的 eval 方法会创建 {@link ClassInfo}，并添加到 env 中
 * {@link ClassStatement} 的对象的定义，而 {@link ClassInfo} 是对象的实例
 * 所以有一个 {@link #env} 的 field
 * @author 0x822a5b87
 * </pre>
 */
public class ClassInfo {

    private final ClassStatement classStatement;

    private final Environment env;

    private final ClassInfo superClassStatement;

    public ClassInfo(ClassStatement classStatement, Environment env) {
        this.classStatement = classStatement;
        this.env = env;
        Object obj = env.get(classStatement.getSuperClassName());
        if (obj == null) {
            superClassStatement = null;
        } else if (obj instanceof ClassInfo) {
            superClassStatement = (ClassInfo) obj;
        } else {
            throw new StoneException("unknown super class : "
                                     + classStatement.getSuperClassName()
                                     + ":" + obj);
        }
    }

    public String getClassName() {
        return classStatement.getClassName();
    }

    public ClassInfo getSuperClass() {
        return superClassStatement;
    }

    public ClassBody getClassBody() {
        return classStatement.getBody();
    }

    public Environment getEnv() {
        return env;
    }

    @Override
    public String toString() {
        return "<class " + getClassName() + ">";
    }
}
