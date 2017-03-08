package com.navercorp.pinpoint.web.filter;

import com.google.common.base.Preconditions;

public class CodeRange {
    private final int beginCode;
    private final int endCode;

    public CodeRange(int beginCode, int endCode){
        Preconditions.checkArgument(beginCode < endCode, "endCode must be bigger then beginCode");
        this.beginCode = beginCode;
        this.endCode = endCode;

    }

    public int getBeginCode()
    {
        return this.beginCode;
    }

    public int getEndCode()
    {
        return this.endCode;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CodeRange codeRange = (CodeRange)o;
        if (codeRange.beginCode != beginCode) return false;
        if (codeRange.endCode != endCode) return false;

        return true;
    }

    @Override
    public int hashCode(){
        int result = (int) (beginCode ^ (beginCode >>> 32));
        result = 31 * result + (int) (endCode ^ (endCode >>> 32));
        return result;
    }

    @Override
    public String toString()
    {
        return "CodeRange{" +
                "beginCode=" + beginCode +
                ", endCode=" + endCode +
                '}';
    }

}
