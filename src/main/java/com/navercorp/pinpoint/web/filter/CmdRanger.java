package com.navercorp.pinpoint.web.filter;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.navercorp.pinpoint.web.filter.TransactionType.*;

public class CmdRanger {

    private static List<CmdRange> cmdRanges = newArrayList(
            Range(TRANSACTION_CM,CM_CBC_RANGE,CM_MOVESITE_RANGE,CM_EMB_RANGE,CM_OTHER_RANGE),
            Range(TRANSACTION_PM,PM_F_RANGE,PM_EMB_RANGE),
            Range(TRANSACTION_FM,FM_F_RANGE,FM_EMB_RANGE),
            Range(TRANSACTION_DDM,DDM_RANGE),
            Range(TRANSACTION_RM,RM_EMF_RANGE,RM_F_RANGE,RM_EMS_RANGE),
            Range(TRANSACTION_NRM,NRM_RM_RANGE,NRM_EMB_RANGE),
            Range(TRANSACTION_TOPO,TOPO_RANGE),
            Range(TRANSACTION_FCT,FCT_RANGE),
            Range(TRANSACTION_CHK,CHK_EMB_RANGE));


    public CmdRanger() {

    }

    private static CmdRange Range(String name,CodeRange ... codeRanges) {
        return new CmdRange(name, newArrayList(codeRanges));
    }

    public String getType(int cmdCode) {
        for(CmdRange cmdRange : cmdRanges) {

            if(cmdRange.include(cmdCode)) {
                return cmdRange.getType();
            }
        }

        return TRANSACTION_OTHER;
    }

    public List<CodeRange> getRange(String type) {
        for(CmdRange cmdRange : cmdRanges) {
            if(cmdRange.getType().equals(type)) {
                return cmdRange.getRanges();
            }
        }

        return newArrayList();
    }
}
