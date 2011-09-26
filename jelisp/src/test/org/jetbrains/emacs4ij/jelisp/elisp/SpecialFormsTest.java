package org.jetbrains.emacs4ij.jelisp.elisp;

import org.jetbrains.emacs4ij.jelisp.Environment;
import org.jetbrains.emacs4ij.jelisp.Parser;
import org.jetbrains.emacs4ij.jelisp.exception.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 9/26/11
 * Time: 3:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpecialFormsTest {
    private Environment environment;

    @Before
    public void setUp() throws Exception {
        Environment.ourEmacsPath = "/usr/share/emacs/23.2";
        environment = new Environment(new Environment());
    }

    private LObject evaluateString (String lispCode) throws LispException {
        Parser parser = new Parser();
        return parser.parseLine(lispCode).evaluate(environment);
    }

    @Test
    public void testQuote() throws Exception {
        LObject LObject = evaluateString("'5");
        junit.framework.Assert.assertEquals(new LispInteger(5), LObject);
    }

    @Test
    public void testQuotedQuotedList () {
        LObject LObject = evaluateString("'(quote 5)");
        junit.framework.Assert.assertEquals(new LispList(new LispSymbol("quote"), new LispInteger(5)), LObject);
    }

    @Test
    public void testQuotedFunctionArg() {
        LObject kit = evaluateString("(defun kit (a) (car-safe a))");
        junit.framework.Assert.assertEquals("kit ", new LispSymbol("kit"), kit);
        LObject LObject = evaluateString("(kit 'test)");
        junit.framework.Assert.assertEquals(LispSymbol.ourNil, LObject);
    }

    @Test
    public void testLetEmpty() {
        LObject LObject = evaluateString("(let ())");
        junit.framework.Assert.assertEquals(LispSymbol.ourNil, LObject);
    }

    @Test
    public void testLetEmptyVar() {
        LObject LObject = evaluateString("(let () 5)");
        junit.framework.Assert.assertEquals(new LispInteger(5), LObject);
    }

    @Test
    public void testLetNilVar() {
        LObject LObject = evaluateString("(let (a) a)");
        junit.framework.Assert.assertEquals(LispSymbol.ourNil, LObject);
    }

    @Test
    public void testLetEmptyBody() {
        LObject LObject = evaluateString("(let ((a 5)) )");
        junit.framework.Assert.assertEquals(LispSymbol.ourNil, LObject);
    }

    @Test
    public void testLetAtomVar() {
        LObject LObject = evaluateString("(let ((a 5)) a)");
        junit.framework.Assert.assertEquals(new LispInteger(5), LObject);
    }

    @Test
    public void testLetStar() {
        LObject LObject = evaluateString("(let* ((a 5) (b (+ 2 a))) (+ a b))");
        junit.framework.Assert.assertEquals(new LispInteger(12), LObject);
    }

    @Test
    public void testCond() {
        LObject cond = evaluateString("(cond)");
        junit.framework.Assert.assertEquals(LispSymbol.ourNil, cond);
        cond = evaluateString("(cond (5))");
        junit.framework.Assert.assertEquals(new LispInteger(5), cond);
        cond = evaluateString("(cond (nil 10 15) (1 2 3))");
        junit.framework.Assert.assertEquals(new LispInteger(3), cond);
        cond = evaluateString("(cond (1 10 15) 5)");
        junit.framework.Assert.assertEquals(new LispInteger(15), cond);
    }

    @Test (expected = WrongTypeArgument.class)
    public void testCondWrongArg() {
        evaluateString("(cond 5)");
    }

    @Test
    public void testWhile() {
        evaluateString("(set 'my-list '(1 2 3))");
        LObject LObject = evaluateString("(while my-list (car my-list) (set 'my-list (cdr my-list)))");
        junit.framework.Assert.assertEquals(LispSymbol.ourNil, LObject);
    }

    @Test
    public void testIfT () {
        LObject LObject = evaluateString("(if t)");
        junit.framework.Assert.assertEquals(LispSymbol.ourT, LObject);
    }

    @Test
    public void testIfNil () {
        LObject LObject = evaluateString("(if nil)");
        junit.framework.Assert.assertEquals(LispSymbol.ourNil, LObject);
    }

    @Test
    public void testIfTrue () {
        LObject LObject = evaluateString("(if 5 'true 'false)");
        junit.framework.Assert.assertEquals(new LispSymbol("true"), LObject);
    }

    @Test
    public void testIfFalse () {
        LObject LObject = evaluateString("(if () 'true 'one 'two 'false)");
        junit.framework.Assert.assertEquals(new LispSymbol("false"), LObject);
    }

    @Test
    public void testAndEmpty() {
        LObject and = evaluateString("(and)");
        junit.framework.Assert.assertEquals(LispSymbol.ourT, and);
    }

    @Test
    public void testAndNil() {
        LObject and = evaluateString("(and 1 2 3 nil)");
        junit.framework.Assert.assertEquals(LispSymbol.ourNil, and);
    }

    @Test
    public void testAndVal() {
        LObject and = evaluateString("(and 1 2 3 4 5)");
        junit.framework.Assert.assertEquals(new LispInteger(5), and);
    }

    @Test
    public void testOrEmpty() {
        LObject or = evaluateString("(or)");
        junit.framework.Assert.assertEquals(LispSymbol.ourNil, or);
    }

    @Test
    public void testOrMulti() {
        LObject or = evaluateString("(or nil nil nil 5)");
        junit.framework.Assert.assertEquals(new LispInteger(5), or);
    }

    @Test (expected = VoidVariableException.class)
    public void testDefvar1() {
        evaluateString("(defvar a)");
        evaluateString("a");
    }

    @Test
    public void testDefvar2() {
        evaluateString("(defvar a 5 \"doc\")");
        LispSymbol a = environment.find("a");
        junit.framework.Assert.assertEquals(new LispInteger(5), a.getValue());
        LObject varDoc = evaluateString("(get 'a 'variable-documentation)");
        junit.framework.Assert.assertEquals(new LispString("doc"), varDoc);
        LObject b = evaluateString("(set 'a 10)");
        junit.framework.Assert.assertEquals(new LispInteger(10), b);
        varDoc = evaluateString("(get 'a 'variable-documentation)");
        junit.framework.Assert.assertEquals(new LispString("doc"), varDoc);
    }

    @Test
    public void testDefun3args() {
        LObject fun = evaluateString("(defun mult7 (arg) (* 7 arg))");
        junit.framework.Assert.assertEquals("defun return value assertion", new LispSymbol("mult7"), fun);
        LObject value = evaluateString("(mult7 5)");
        junit.framework.Assert.assertEquals("mult7 return value assertion", new LispInteger(35), value);
    }

    @Test
    public void testDefun4args() {
        LObject fun = evaluateString("(defun mult7 (arg) \"multiplies arg*7\" (* 7 arg))");
        junit.framework.Assert.assertEquals("defun return value assertion", new LispSymbol("mult7"), fun);
        LObject value = evaluateString("(mult7 5)");
        junit.framework.Assert.assertEquals("mult7 return value assertion", new LispInteger(35), value);
    }

    @Test (expected = WrongNumberOfArgumentsException.class)
    public void testDefunWrongNumberOfArgs() {
        LObject fun = evaluateString("(defun mult7 () ())");
        junit.framework.Assert.assertEquals("defun return value assertion", new LispSymbol("mult7"), fun);
        evaluateString("(mult7 5)");
    }

    @Test
    public void testDefunEmptyBody() {
        LObject fun = evaluateString("(defun nilFun () ())");
        junit.framework.Assert.assertEquals("defun return value assertion", new LispSymbol("nilFun"), fun);
        LObject value = evaluateString("(nilFun)");
        junit.framework.Assert.assertEquals("nilFun return value assertion", LispSymbol.ourNil, value);
    }

    @Test
    public void testDefunEmptyBody2 () {
        LObject fun = evaluateString("(defun nilFun ())");
        junit.framework.Assert.assertEquals("defun return value assertion", new LispSymbol("nilFun"), fun);
        LObject value = evaluateString("(nilFun)");
        junit.framework.Assert.assertEquals("nilFun return value assertion", LispSymbol.ourNil, value);
    }

    @Test
    public void testDefunIntBody () {
        LObject fun = evaluateString("(defun testFun () 5)");
        junit.framework.Assert.assertEquals("defun return value assertion", new LispSymbol("testFun"), fun);
        LObject value = evaluateString("(testFun)");
        junit.framework.Assert.assertEquals("testFun return value assertion", new LispInteger(5), value);
    }

    @Test (expected = InvalidFunctionException.class)
    public void testDefunWrongBody () {
        LObject fun = evaluateString("(defun testFun () (5))");
        junit.framework.Assert.assertEquals("defun return value assertion", new LispSymbol("testFun"), fun);
        evaluateString("(testFun)");
    }

    @Test
    public void testDefunComplexBody () {
        LObject fun = evaluateString("(defun testFun () 5 6 7 8 'ann)");
        junit.framework.Assert.assertEquals("defun return value assertion", new LispSymbol("testFun"), fun);
        LObject value = evaluateString("(testFun)");
        junit.framework.Assert.assertEquals("testFun return value assertion", new LispSymbol("ann"), value);
    }

    @Test
    public void testFunctionSymbolArgumentsSubstitution() {
        evaluateString("(defun test (a) a)");
        LObject LObject = evaluateString("(test 5)");
        junit.framework.Assert.assertEquals(new LispInteger(5), LObject);
    }

    @Test
    public void testFunctionArgumentsSubstitution() {
        evaluateString("(defun test (a) (+ a (+ a 1)))");
        LObject LObject = evaluateString("(test 5)");
        junit.framework.Assert.assertEquals(new LispInteger(11), LObject);
    }

    @Test
    public void testDefunInsideLet() {
        evaluateString("(let ((x 2)) (defun one () 1))");
        LObject result = evaluateString("(one)");
        junit.framework.Assert.assertEquals(new LispInteger(1), result);
    }

    @Test
    public void testDoubleDefvar() {
        evaluateString("(defvar a 1)");
        evaluateString("(defvar a 2 \"doc\")");
    }

    @Test
    public void testDefvarInsideLet() {
        evaluateString("(let ((x 2)) (defvar one 1))");
        LObject result = evaluateString("one");
        junit.framework.Assert.assertEquals(new LispInteger(1), result);
    }

    @Test
    public void testSymbolWithValueAndFunctionCells() {
        evaluateString("(defvar a 1)");
        evaluateString("(defun a () 2)");
        junit.framework.Assert.assertEquals(new LispInteger(1), evaluateString("a"));
        junit.framework.Assert.assertEquals(new LispInteger(2), evaluateString("(a)"));
    }

    @Test
    public void testProgn() throws Exception {
        LObject lispObject = evaluateString("(progn 1 2 3)");
        Assert.assertEquals(new LispInteger(3), lispObject);
    }

    @Ignore
    @Test
    public void testDefineMacro() throws Exception {
        throw new RuntimeException("not implemented");
    }

    @Ignore
    @Test
    public void testInteractive() throws Exception {
        throw new RuntimeException("not implemented");
    }
}