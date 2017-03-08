package com.navercorp.pinpoint.web.dao;

import com.navercorp.pinpoint.common.topo.domain.TopoLine;
import com.navercorp.pinpoint.web.vo.Range;

import java.util.List;
import java.util.Set;

public interface InstanceIndexDao {
    List<TopoLine> getTopoLineSet(String appName, Range range);
}
