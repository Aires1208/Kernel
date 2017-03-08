package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.view.HostStat;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XHost;

import java.util.List;
import java.util.Set;

/**
 * Created by root on 2016/11/23.
 */
public interface XHostsService {
    List<HostStat> getHostsDashBoard(Range range);

    Set<XHost> getXHosts();
}
