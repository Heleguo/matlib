package me.matl114.matlib.common.lang.exceptions;

import me.matl114.matlib.algorithms.dataStructures.struct.Holder;

public class FastAbort extends Abort{
    Object reason;
    public <T> T getAbortReason(){
        return (T)reason;
    }
    public FastAbort(Object val){
        super();
        this.reason = val;
    }
}
