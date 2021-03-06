package org.jetbrains.emacs4ij.jelisp.exception;

import org.jetbrains.emacs4ij.jelisp.JelispBundle;

/**
 * Created by IntelliJ IDEA.
 * User: Ekaterina.Polishchuk
 * Date: 7/11/11
 * Time: 6:28 PM
 * To change this template use File | Settings | File Templates.
 *
 * this is the base elisp exception
 */
public class LispException extends RuntimeException {
    //TODO: store the position where the exception raised
    protected StringBuilder myStackTrace;

    public LispException () {
        super (JelispBundle.message("unknown.exception"));
    }

    public LispException (String message) {
        super(message);
    }

    public LispException(String message, StringBuilder stackTrace) {
        super(message);
        myStackTrace = stackTrace;
    }
}
