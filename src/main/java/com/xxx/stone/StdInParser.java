package com.xxx.stone;

import com.xxx.stone.array.ArrayParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author 0x822a5b87
 */
public class StdInParser extends ArrayParser {

    BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws ParseException, IOException {
        StdInParser parser = new StdInParser();
        StringBuilder code = new StringBuilder();
        String line;
        while ((line = parser.r.readLine()) != null) {
            System.out.println(line);
            code.append(line).append("\n");
        }
        parser.run(code.toString());
    }
}
