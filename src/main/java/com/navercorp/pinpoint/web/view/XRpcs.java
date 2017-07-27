package com.navercorp.pinpoint.web.view;

import com.navercorp.pinpoint.web.vo.VoXRpc;

import java.util.List;

/**
 * Created by ${aires} on 11/24/16.
 */
public class XRpcs {
    private List<VoXRpc> URLAggregation;
    private List<VoXRpc> UseCaseAggregation;

    public List<VoXRpc> getUseCaseAggregation() {
        return UseCaseAggregation;
    }

    public void setUseCaseAggregation(List<VoXRpc> useCaseAggregation) {
        UseCaseAggregation = useCaseAggregation;
    }

    public List<VoXRpc> getURLAggregation() {
        return URLAggregation;
    }

    public void setURLAggregation(List<VoXRpc> URLAggregation) {
        this.URLAggregation = URLAggregation;
    }

    @Override
    public String toString() {
        return "XRpcs{" +
                "URLAggregation=" + URLAggregation +
                ", UseCaseAggregation=" + UseCaseAggregation +
                '}';
    }
}
