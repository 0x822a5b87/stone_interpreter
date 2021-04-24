package com.xxx.stone.nat1ve;

import com.xxx.stone.StoneException;
import com.xxx.stone.ast.AbstractSyntaxTree;
import java.lang.reflect.Method;
import lombok.Data;

/**
 * <pre>
 * @author 0x822a5b87
 *
 * native function declare
 * </pre>
 */
@Data
public class NativeFunction {

    /**
     * native method
     */
    private final Method method;
    /**
     * native method name
     */
    private final String methodName;
    /**
     * num of native method parameter
     */
    private final int    numParams;

    public NativeFunction(String n, Method m) {
        methodName = n;
        method = m;
        numParams = m.getParameterTypes().length;
    }

    public Object invoke(Object[] args, AbstractSyntaxTree tree) {
        try {
            return method.invoke(null, args);
        } catch (Exception e) {
            throw new StoneException("bad native function call: " + methodName, tree);
        }
    }

    @Override
    public String toString() {
        return "<native:" + hashCode() + ">";
    }
}
