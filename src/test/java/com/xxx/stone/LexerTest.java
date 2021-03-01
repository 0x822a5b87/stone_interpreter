package com.xxx.stone;

import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;

public class LexerTest {

    private final Lexer lexer = new Lexer(new StringReader(SOURCE_CODE));

    private static final String SOURCE_CODE = "while i < \"\n\" {\n"
                                              + "    sum = sum + i\n"
                                              + "    i = i + 1\n"
                                              + "}\n"
                                              + "\n"
                                              + "sum";

    @Test
    public void testPattern() throws ParseException {
        testStringLiteralPattern();
        testParse();
    }

    private void testStringLiteralPattern() {
        Pattern slp = Pattern.compile(Lexer.STRING_LITERAL_PATTERN);
        Assert.assertTrue(slp.matcher("\"\\\"\"").matches());

        System.out.println(Lexer.TOTAL_PATTERN);
        Pattern tp = Pattern.compile(Lexer.TOTAL_PATTERN);
        Matcher tpm = tp.matcher("// hello world");
        tpm.useTransparentBounds(true).useAnchoringBounds(false);
        Assert.assertTrue(tpm.matches());
        Assert.assertNotNull(tpm.group(1));
        Assert.assertNotNull(tpm.group(2));
    }

    private void testParse() throws ParseException {
        lexer.readLine();
    }
}