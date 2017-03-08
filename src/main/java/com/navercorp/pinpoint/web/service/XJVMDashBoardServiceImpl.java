package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.common.bo.ServerMetaDataBo;
import com.navercorp.pinpoint.web.topo.usercases.CalculateVMDashBoardServiceUserCase;
import com.navercorp.pinpoint.web.view.XJVMDashBoard;
import com.navercorp.pinpoint.web.vo.AgentInfo;
import com.navercorp.pinpoint.web.vo.AgentStat;
import com.navercorp.pinpoint.web.vo.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Service
public class XJVMDashBoardServiceImpl {

    @Autowired
    private AgentStatService agentStatService;

    @Autowired
    private AgentInfoService agentInfoService;

    public void setAgentStatService(AgentStatService agentStatService) {
        this.agentStatService = agentStatService;
    }

    public void setAgentInfoService(AgentInfoService agentInfoService) {
        this.agentInfoService = agentInfoService;
    }

    public XJVMDashBoard getXJVMDashBoard(String agentId, Range range) {
        List<AgentStat> agentStatList = agentStatService.selectAgentStatList(agentId, range);
        CalculateVMDashBoardServiceUserCase userCase = new CalculateVMDashBoardServiceUserCase(agentStatList, range);
        XJVMDashBoard xjvmDashBoard = userCase.getVMDashBoardServiceUserCase();
        AgentInfo agentInfo = agentInfoService.getAgentInfo(agentId, System.currentTimeMillis());
        ServerMetaDataBo serverMetaDataBo = agentInfo.getServerMetaData();
        List<String> vmArgs = newArrayList();
        if (null != serverMetaDataBo) {
            vmArgs = serverMetaDataBo.getVmArgs();
            xjvmDashBoard.setJvmArgs(vmArgs);
        }
        xjvmDashBoard.setJvmVersion(agentInfo.getVmVersion());
        xjvmDashBoard.setGcTypeName(agentInfo.getJvmInfo().getGcTypeName());

        xjvmDashBoard.setJvmArgs(vmArgs);
        return xjvmDashBoard;
    }
}
