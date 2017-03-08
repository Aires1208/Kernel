package com.navercorp.pinpoint.web.filter;

import java.util.List;

public class CmdRange {
    private final String type;
    private final List<CodeRange> ranges;

    public CmdRange(String type, List<CodeRange> ranges)
    {
        this.type = type;
        this.ranges = ranges;
    }

    public boolean include(int code)
    {
        for (CodeRange range : ranges) {
            if (code >= range.getBeginCode() && code <= range.getEndCode())
                return true;
        }
        return false;
    }

    public String getType()
    {
        return this.type;
    }

    public List<CodeRange> getRanges()
    {
        return this.ranges;
    }


    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CmdRange{type=" + type);
        for (CodeRange codeRange : ranges)
        {
            stringBuilder.append("," + codeRange.toString());
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

}
