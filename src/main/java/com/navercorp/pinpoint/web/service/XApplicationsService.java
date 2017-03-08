package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.view.XApplication;
import com.navercorp.pinpoint.web.view.XApplicationsDashBoard;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.XService;

import java.util.List;

public interface XApplicationsService {

    List<XApplication> getXApplications();

    XApplicationsDashBoard getXApplicationsDashBoard(Range range);

    List<XService> getXServices(String productName);

    List<XApplication> getFullAppList();
}
