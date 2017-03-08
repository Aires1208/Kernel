package com.navercorp.pinpoint.web.topo.usercases.Util;

import com.navercorp.pinpoint.common.events.ResultEvent;
import com.navercorp.pinpoint.common.topo.domain.AgentInfoDigest;
import com.navercorp.pinpoint.common.topo.domain.NodeHealth;
import com.navercorp.pinpoint.common.util.AgentLifeCycleState;
import com.navercorp.pinpoint.web.service.AgentInfoService;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.Range;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.navercorp.pinpoint.web.report.usercase.HealthLevel.*;

/**
 * Created by root on 7/18/16.
 */
public class XNodeHelper {
    public static NodeHealth getNodeHealth(List<AgentInfoDigest> agentInfoDigests) {

        long critical = 0;
        long warning = 0;
        long normal = 0;

        if (agentInfoDigests == null || agentInfoDigests.isEmpty()) {
            return new NodeHealth(critical, warning, normal);
        }

        for (AgentInfoDigest each : agentInfoDigests) {
            short stateCode = each.getStateCode();
            AgentLifeCycleState agentLifeCycleState = AgentLifeCycleState.getStateByCode(stateCode);
            if (agentLifeCycleState == AgentLifeCycleState.UNEXPECTED_SHUTDOWN ||
                    agentLifeCycleState == AgentLifeCycleState.DISCONNECTED ||
                    agentLifeCycleState == AgentLifeCycleState.UNKNOWN) {
                critical++;
            } else if (agentLifeCycleState == AgentLifeCycleState.SHUTDOWN) {
                warning++;
            } else {
                normal++;
            }
        }

        return new NodeHealth(critical, warning, normal);
    }


    public static List<AgentInfoDigest> getAgentInfoDigests(String serviceName, AgentInfoService agentInfoService, Range range) {
        long timestamp = range.getTo();
        if (timestamp < 0) {
            return newArrayList();
        }

        List<AgentInfoDigest> serverInstances = newArrayList();
        Set<AgentInfo> agentList = agentInfoService.getAgentsByApplicationName(serviceName, timestamp);
        for (AgentInfo agentInfo : agentList) {
            short stateCode = agentInfo.getStatus().getState().getCode();
            serverInstances.add(new AgentInfoDigest(
                    agentInfo.getApplicationName(),
                    agentInfo.getAgentId(),
                    agentInfo.getHostName(),
                    stateCode,
                    agentInfo.getServiceTypeCode()));
        }

        return serverInstances;
    }

    public static AgentInfoDigest getAgentInfoDigest(String agentId, AgentInfoService agentInfoService, Range range) {
        long timestamp = range.getTo();
        if (timestamp <= 0) {
            return null;
        }

        AgentInfo agentInfo = agentInfoService.getAgentInfo(agentId, timestamp);
        if (agentInfo == null) {
            return null;
        }

        return new AgentInfoDigest(agentInfo.getApplicationName(), agentId, agentInfo.getHostName(), agentInfo.getStatus().getState().getCode(), agentInfo.getServiceTypeCode());
    }

    public static NodeHealth getDnHealth(List<ResultEvent> events) {
        if (events.isEmpty()) {
            return new NodeHealth(0L, 0L, 0L);
        }

        int flag = 0;
        for (ResultEvent event : events) {
            int level = event.getEventType() % 10;
            flag = (flag < level) ? level : flag;
        }

        return new NodeHealth(CRITICAL.getCode() == flag ? 1L : 0L, WARNING.getCode() == flag ? 1L : 0L, NORMAL.getCode() == flag ? 1L : 0L);
    }
}
