package com.navercorp.pinpoint.web.topo.usercases;


import com.navercorp.pinpoint.common.topo.domain.TopoLine;

import java.util.Comparator;

/**
 * Created by aires on 7/25/16.
 */
public class XTopoLineComparable implements Comparator<TopoLine> {
    public static boolean sortASC = true;

    @Override
    public int compare(TopoLine topoLine1, TopoLine topoLine2) {
        int result = 0;
        if (sortASC) {
            result = Long.compare(topoLine1.getTimestamp(), topoLine2.getTimestamp());
        } else {
            result = -Long.compare(topoLine1.getTimestamp(), topoLine2.getTimestamp());
        }
        return result;
    }
}
