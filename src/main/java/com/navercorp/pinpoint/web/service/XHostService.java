package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XHostDetail;

/**
 * Created by root on 2016/11/23.
 */
public interface XHostService {
    XHostDetail getHostDetail(String hostId, Range range);
}
