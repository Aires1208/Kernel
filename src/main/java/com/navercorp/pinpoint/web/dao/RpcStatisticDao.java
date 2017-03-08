package com.navercorp.pinpoint.web.dao;

import com.navercorp.pinpoint.common.topo.domain.XRpc;
import com.navercorp.pinpoint.web.vo.Range;

import java.util.List;

/**
 * Created by ${10183966} on 11/24/16.
 */
public interface RpcStatisticDao {
    List<XRpc> getXRpcList(String appliactionName, Range range);

}
