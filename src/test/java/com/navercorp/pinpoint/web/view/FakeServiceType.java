package com.navercorp.pinpoint.web.view;

import com.navercorp.pinpoint.common.trace.HistogramSchema;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.trace.ServiceTypeCategory;

/**
 * Created by root on 9/25/16.
 */
public class FakeServiceType implements ServiceType {

    public FakeServiceType() {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public short getCode() {
        return 0;
    }

    @Override
    public String getDesc() {
        return null;
    }

    @Override
    public boolean isInternalMethod() {
        return false;
    }

    @Override
    public boolean isRpcClient() {
        return false;
    }

    @Override
    public boolean isRecordStatistics() {
        return false;
    }

    @Override
    public boolean isUnknown() {
        return false;
    }

    @Override
    public boolean isUser() {
        return false;
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public boolean isIncludeDestinationId() {
        return false;
    }

    @Override
    public ServiceTypeCategory getCategory() {
        return null;
    }

    @Override
    public HistogramSchema getHistogramSchema() {
        return null;
    }

    @Override
    public boolean isWas() {
        return false;
    }
}
