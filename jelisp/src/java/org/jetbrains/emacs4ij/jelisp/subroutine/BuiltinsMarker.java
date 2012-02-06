package org.jetbrains.emacs4ij.jelisp.subroutine;

import org.jetbrains.emacs4ij.jelisp.Environment;
import org.jetbrains.emacs4ij.jelisp.GlobalEnvironment;
import org.jetbrains.emacs4ij.jelisp.elisp.*;
import org.jetbrains.emacs4ij.jelisp.exception.WrongTypeArgumentException;

/**
 * Created by IntelliJ IDEA.
 * User: kate
 * Date: 10/3/11
 * Time: 4:38 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BuiltinsMarker {
    @Subroutine("make-marker")
    public static LispMarker makeMarker () {
        return new LispMarker();
    }

    @Subroutine("point-marker")
    public static LispMarker pointMarker (Environment environment) {
        LispBuffer buffer = environment.getBufferCurrentForEditing();
        return new LispMarker(buffer.point(), buffer);
    }

    @Subroutine ("marker-insertion-type")
    public static LispSymbol markerInsertionType (LispMarker marker) {
        return marker.getInsertionType();
    }

    @Subroutine("set-marker-insertion-type")
    public static LObject setMarkerInsertionType (LispMarker marker, LObject type) {
        return marker.setInsertionType(type);
    }

    @Subroutine("point-min-marker")
    public static LispMarker pointMinMarker (Environment environment) {
        LispBuffer buffer = environment.getBufferCurrentForEditing();
        return new LispMarker(buffer.pointMin(), buffer);
    }

    @Subroutine("point-max-marker")
    public static LispMarker pointMaxMarker (Environment environment) {
        LispBuffer buffer = environment.getBufferCurrentForEditing();
        return new LispMarker(buffer.pointMax(), buffer);
    }

    @Subroutine("copy-marker")
    public static LispMarker copyMarker (Environment environment, LObject markerOrInteger, @Optional LObject insertionType) {
        if (markerOrInteger instanceof LispMarker) {
            LispMarker marker = new LispMarker((LispMarker) markerOrInteger);
            if (insertionType != null)
                marker.setInsertionType(insertionType);
            return marker;
        }
        LispBuffer buffer = environment.getBufferCurrentForEditing();
        LispMarker marker = new LispMarker(markerOrInteger, buffer);
        if (insertionType != null) {
            marker.setInsertionType(insertionType);
        }
        return marker;
    }

    @Subroutine("set-marker")
    public static LispMarker setMarker (LispMarker marker, LObject position, @Optional LispBuffer buffer) {
        if (BuiltinPredicates.integerOrMarkerP(position).equals(LispSymbol.ourNil) && !position.equals(LispSymbol.ourNil))
            throw new WrongTypeArgumentException("integer-or-marker-p", position.toString());
        if (buffer == null)
            buffer = GlobalEnvironment.INSTANCE.getBufferCurrentForEditing();
        marker.setBuffer(buffer);
        marker.setPosition(position);
        return marker;
    }
    
    @Subroutine("marker-position")
    public static LObject markerPosition (LispMarker marker) {
        return marker.getPosition();
    }

    @Subroutine("marker-buffer")
    public static LObject markerBuffer (LispMarker marker) {
        return marker.getBuffer() == null ? LispSymbol.ourNil : marker.getBuffer();
    }
    
    @Subroutine("buffer-has-markers-at")
    public static LispSymbol bufferHasMarkersAt (LObject position) {
        if (position instanceof LispInteger) 
            return LispSymbol.bool(GlobalEnvironment.INSTANCE.getBufferCurrentForEditing().hasMarkersAt(((LispInteger) position).getData()));
        return LispSymbol.ourNil;
    }

}
