package me.matl114.matlib.Common.Lang.Exceptions;

public class NoLongerSupport extends UnsupportedOperationException {
    public NoLongerSupport() {
        super("this method is no longer supported");
    }
}
