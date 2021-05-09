package com.xxx.stone.array;

import com.xxx.stone.ParseException;
import com.xxx.stone.object.ObjectParser;
import java.io.IOException;

/**
 * @author 0x822a5b87
 *
 *         array parser
 */
public class ArrayParser extends ObjectParser {

    public static String ARRAY_CODE = "a = [2, 1 + 2, 4]\n"
                                      + "print a[0]\n"
                                      + "print a[1]\n"
                                      + "print a[2]\n"
                                      + "a[1] = \"three\"\n"
                                      + "print \"a[1] = \" + a[1]\n"
                                      + "b = [[\"one\", 1], [\"two\", 2]]\n"
                                      + "print \"b[1][0]\" + \": \" + b[1][0]\n"
                                      + "print \"b[1][1]\" + \": \" + b[1][1]\n";

    public static void main(String[] args) throws ParseException, IOException {
        ArrayParser parser = new ArrayParser();
        parser.run(ARRAY_CODE);
    }
}
