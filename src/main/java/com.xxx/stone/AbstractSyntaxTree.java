package com.xxx.stone;

import java.util.Iterator;

/**
 * @author 0x822a5b87
 *
 */
public abstract class AbstractSyntaxTree implements Iterable<AbstractSyntaxTree> {

    public abstract AbstractSyntaxTree child(int i);

    public abstract int numChildren();

    public abstract Iterator<AbstractSyntaxTree> children();

    public abstract String location();

    public Iterator<AbstractSyntaxTree> iterator() {
        return children();
    }
}
