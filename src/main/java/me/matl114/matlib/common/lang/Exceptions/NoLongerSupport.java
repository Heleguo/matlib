package me.matl114.matlib.common.lang.Exceptions;

public class NoLongerSupport extends UnsupportedOperationException {
    public NoLongerSupport() {
        super("this method is no longer supported");
    }
}
