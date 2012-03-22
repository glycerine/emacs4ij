package org.jetbrains.emacs4ij.jelisp.subroutine;

import junit.framework.Assert;
import org.jetbrains.emacs4ij.jelisp.TestSetup;
import org.jetbrains.emacs4ij.jelisp.elisp.LispInteger;
import org.jetbrains.emacs4ij.jelisp.elisp.LispObject;
import org.jetbrains.emacs4ij.jelisp.elisp.LispString;
import org.jetbrains.emacs4ij.jelisp.elisp.LispSymbol;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 26/02/12
 * Time: 18:19
 * To change this template use File | Settings | File Templates.
 */
public class BuiltinsStringTest extends BaseSubroutineTest {
    @Test
    public void testFormatEmpty() {
        LispObject s = evaluateString("(format \"\")");
        Assert.assertEquals(new LispString(""), s);
    }

    @Test
    public void testFormatNoNeedInArgs() {
        LispObject s = evaluateString("(format \"hello\" 1 2 3)");
        Assert.assertEquals(new LispString("hello"), s);
    }

    @Test
    public void testFormatNotEnoughArgs() {
        try {
            evaluateString("(format \"%d\")");
        } catch (Exception e) {
            Assert.assertEquals("Not enough arguments for format string", TestSetup.getCause(e));
            return;
        }
        Assert.fail();
    }

    @Test
    public void testFormatUnexpectedEndOfFormatString() {
        try {
            evaluateString("(format \"%123456\" 1)");
        } catch (Exception e) {
            Assert.assertEquals("Format string ends in middle of format specifier", TestSetup.getCause(e));
            return;
        }
        Assert.fail();
    }

    @Test
    public void testFormatInvalidFormatCharacter() {
        try {
            evaluateString("(format \"%123456q\" 1)");
        } catch (Exception e) {
            Assert.assertEquals("Invalid format operation %q", TestSetup.getCause(e));
            return;
        }
        Assert.fail();
    }

    @Test
    public void testFormatSimple() {
        LispObject s = evaluateString("(format \"%1234d\" 1 2 3)");
        Assert.assertEquals(new LispString("1"), s);
    }

    @Test
    public void testStringMatchSimple() {
        LispObject r = evaluateString("(string-match \"a\" \"africa\")");
        Assert.assertEquals(new LispInteger(0), r);
        evaluateString("(setq case-fold-search t)");
        r = evaluateString("(string-match \"A\" \"africa\" 1)");
        Assert.assertEquals(new LispInteger(5), r);
        evaluateString("(setq case-fold-search nil)");
        r = evaluateString("(string-match \"A\" \"africa\")");
        Assert.assertEquals(LispSymbol.ourNil, r);
    }

    @Test
    public void testStringMatchOutOfRange() {
        try {
            evaluateString("(string-match \"a\" \"africa\" 10)");
        } catch (Exception e) {
            Assert.assertEquals("'(args-out-of-range \"africa\" 10)", TestSetup.getCause(e));
            return;
        }
        Assert.fail();
    }

    @Test
    public void testStringMatchTick() {
        LispObject r = evaluateString("(string-match \"^[ACHMsS]-.\" \"M-x\")");
        Assert.assertEquals(new LispInteger(0), r);
        r = evaluateString("(string-match \"^[ACHMsS]-.\" \"kvaM-x\")");
        Assert.assertEquals(LispSymbol.ourNil, r);
    }

    @Test
    public void testStringMatchTickAfterNewLine() {
        LispObject r = evaluateString("(string-match \"^[ACHMsS]-.\" \"kva\nM-x\")");
        Assert.assertEquals(new LispInteger(4), r);
    }

    @Test
    public void testStringMatchCharAlternative() {
        LispObject r = evaluateString("(string-match \"[abc]\" \"d\\^alla\")");
        Assert.assertEquals(new LispInteger(3), r);
    }

    @Test
    public void testStringMatchStar() {
        LispObject r = evaluateString("(string-match \"hi*\" \"hihihi5\")");
        Assert.assertEquals(new LispInteger(0), r);
        r = evaluateString("(string-match \"a*\" \"sdf\")");
        Assert.assertEquals(new LispInteger(0), r);
    }

    @Test
    public void testStringMatchBackSlash() {
        LispObject r = evaluateString("(string-match \"a\\\\\\\\.b\" \"a\\\\.b\")");
        Assert.assertEquals(new LispInteger(0), r);
    }

    @Test
    public void testStringMatchQuestion() {
        LispObject r = evaluateString("(string-match \"caca?r\" \"cacr\")");
        Assert.assertEquals(new LispInteger(0), r);
        r = evaluateString("(string-match \"a?\" \"crtcr\")");
        Assert.assertEquals(new LispInteger(0), r);
    }

    @Test
    public void testStringMatchDollar() {
        LispObject r = evaluateString("(string-match \"u?hhhh$\" \"ah\")");
        Assert.assertEquals(LispSymbol.ourNil, r);
        r = evaluateString("(string-match \"u?h$\" \"ah\")");
        Assert.assertEquals(new LispInteger(1), r);
    }

    @Test
    public void testStringMatchDot() {
        LispObject r = evaluateString("(string-match \"[.]\" \"a.\")");
        Assert.assertEquals(new LispInteger(1), r);
        r = evaluateString("(string-match \".\" \"a.\")");
        Assert.assertEquals(new LispInteger(0), r);
    }

    @Test
    public void testStringMatch() {
        LispObject r = evaluateString("(string-match \"^[ACHMsS]-.\" \"M-x\")");
        Assert.assertEquals(new LispInteger(0), r);
    }

    @Test
    public void testCapitalize() {
        LispObject r = evaluateString("?a");
        Assert.assertEquals(new LispInteger(97), r);
        r = evaluateString("(capitalize ?a)");
        Assert.assertEquals(new LispInteger(65), r);
        r = evaluateString("(capitalize 97)");
        Assert.assertEquals(new LispInteger(65), r);
        r = evaluateString("(capitalize 5)");
        Assert.assertEquals(new LispInteger(5), r);
        r = evaluateString("(capitalize \"EVERY day\")");
        Assert.assertEquals(new LispString("Every Day"), r);
    }
    
    @Test
    public void testMatchBeginningNil() {
        LispObject r = evaluateString("(match-beginning 0)");
        Assert.assertEquals(LispSymbol.ourNil, r);
    }
    
    @Test
    public void testMatchBeginningOutOfRange() {
        try {
            evaluateString("(match-beginning -10)");
        } catch (Exception e) {
            Assert.assertEquals("'(args-out-of-range -10 0)", TestSetup.getCause(e));
            return;
        }
        Assert.fail();
    }

    @Test
    public void testMatchBeginningZero() {
        evaluateString("(string-match \"quick\" \"The quick fox jumped quickly.\")");
        LispObject r = evaluateString("(match-beginning 0)");
        Assert.assertEquals(new LispInteger(4), r);
        r = evaluateString("(match-beginning 1)");
        Assert.assertEquals(LispSymbol.ourNil, r);
    }

    @Test
    public void testMatchBeginningTwo() {
        evaluateString("(string-match \"one\" \"Test string one.\")");
        evaluateString("(string-match \"\\(qu\\)\\(ick\\)\" \"The quick fox jumped quickly.\")");
        LispObject r = evaluateString("(match-beginning 0)");
        Assert.assertEquals(new LispInteger(4), r);
        r = evaluateString("(match-beginning 1)");
        Assert.assertEquals(new LispInteger(4), r);
        r = evaluateString("(match-beginning 2)");
        Assert.assertEquals(new LispInteger(6), r);
        r = evaluateString("(match-beginning 3)");
        Assert.assertEquals(LispSymbol.ourNil, r);
    }

    @Test
    public void testMatchEnd() {
        evaluateString("(string-match \"one\" \"Test string one.\")");
        evaluateString("(string-match \"\\(qu\\)\\(ick\\)\" \"The quick fox jumped quickly.\")");
        LispObject r = evaluateString("(match-end 0)");
        Assert.assertEquals(new LispInteger(9), r);
        r = evaluateString("(match-end 1)");
        Assert.assertEquals(new LispInteger(6), r);
        r = evaluateString("(match-end 2)");
        Assert.assertEquals(new LispInteger(9), r);
        r = evaluateString("(match-end 3)");
        Assert.assertEquals(LispSymbol.ourNil, r);
    }

    @Test
    public void testMatchStringOutOfRange() {
        try {
            evaluateString("(match-string -1)");
        } catch (Exception e) {
            Assert.assertEquals("'(args-out-of-range -1 0)", TestSetup.getCause(e));
            return;
        }
        Assert.fail();
    }

    @Test
    public void testSubstringOutOfRange1() {
        try {
            evaluateString("(substring \"hello\" -6 5)");
        } catch (Exception e) {
            Assert.assertEquals("'(args-out-of-range \"hello\" -1 5)", TestSetup.getCause(e));
            return;
        }
        Assert.fail();
    }

    @Test
    public void testSubstringOutOfRange2() {
        try {
            evaluateString("(substring \"hello\" 1 7)");
        } catch (Exception e) {
            Assert.assertEquals("'(args-out-of-range \"hello\" 1 7)", TestSetup.getCause(e));
            return;
        }
        Assert.fail();
    }

    @Test
    public void testSubstringOutOfRange3() {
        try {
            evaluateString("(substring \"hello\" 0 -6)");
        } catch (Exception e) {
            Assert.assertEquals("'(args-out-of-range \"hello\" 0 -1)", TestSetup.getCause(e));
            return;
        }
        Assert.fail();
    }

    @Test
    public void testSubstringOutOfRange4() {
        try {
            evaluateString("(substring \"hello\" 3 2)");
        } catch (Exception e) {
            Assert.assertEquals("'(args-out-of-range \"hello\" 3 2)", TestSetup.getCause(e));
            return;
        }
        Assert.fail();
    }

    @Test
    public void testSubstring() {
        LispObject r = evaluateString("(substring \"hello\" 3 3)");
        Assert.assertEquals(new LispString(""), r);
        r = evaluateString("(substring \"hello\" -1)");
        Assert.assertEquals(new LispString("o"), r);
        r = evaluateString("(substring \"hello\" 0 4)");
        Assert.assertEquals(new LispString("hell"), r);
    }
    
    @Test
    public void testMatchString() {
        LispObject r = evaluateString("(string-match \"\\(qu\\)\\(ick\\)\" \"The quick fox jumped quickly.\")");
        Assert.assertEquals(new LispInteger(4), r);
        r = evaluateString("(match-string 0 \"The quick fox jumped quickly.\")");
        Assert.assertEquals(new LispString("quick"), r);
        r = evaluateString("(match-string 1 \"The quick fox jumped quickly.\")");
        Assert.assertEquals(new LispString("qu"), r);
        r = evaluateString("(match-string 2 \"The quick fox jumped quickly.\")");
        Assert.assertEquals(new LispString("ick"), r);
        r = evaluateString("(match-string 3 \"The quick fox jumped quickly.\")");
        Assert.assertEquals(LispSymbol.ourNil, r);
    }
}
