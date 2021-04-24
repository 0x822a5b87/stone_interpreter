package com.xxx.stone.exception;

import com.xxx.stone.ast.AbstractSyntaxTree;

/**
 * <pre>
 *     {@link com.xxx.stone.object.StoneObject} 对象访问时可能抛出的异常
 * @author 0x822a5b87
 * </pre>
 */
public class AccessException extends StoneException {
    public AccessException(String m) {
        super(m);
    }

    public AccessException(String m, AbstractSyntaxTree t) {
        super(m, t);
    }
}

