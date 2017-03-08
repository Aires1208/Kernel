package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.common.topo.domain.TopoLine;
import com.navercorp.pinpoint.web.dao.ApplicationIndexDao;
import com.navercorp.pinpoint.web.dao.InstanceIndexDao;
import com.navercorp.pinpoint.web.topo.usercases.CalculateTopoServiceUserCase;
import com.navercorp.pinpoint.web.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.google.common.collect.Sets.newHashSet;

@Service
public class XInstanceServiceImpl {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationIndexDao applicationIndexDao;

    @Autowired
    private InstanceIndexDao instanceIndexDao;


    public String getXServiceName(String agentId) {
        List<Application> applications = applicationIndexDao.selectAllApplicationNames();

        for (Application application : applications) {
            String applicationName = application.getName();
            Set<String> agentIds = newHashSet(applicationIndexDao.selectAgentIds(applicationName));
            if (agentIds.contains(agentId)) {
                return applicationName;
            }
        }

        return "";
    }

    public XServiceTopo getInstanceTopo(String appName, Range range, String instanceName) {

        List<TopoLine>  topoLines = instanceIndexDao.getTopoLineSet(appName,range);
        CalculateTopoServiceUserCase userCase = new CalculateTopoServiceUserCase(topoLines);

        return userCase.getBound1ServiceTopo(instanceName);
    }
}