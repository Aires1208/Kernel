package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.navercorp.pinpoint.web.vo.XHost;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 16-10-17.
 */
@JsonSerialize(using = XServerListSerializer.class)
public class XHostList {
    private List<String> fullNames = newArrayList();
    public XHostList(Set<XHost> serverIds) {
        Preconditions.checkArgument(!serverIds.isEmpty(), new NullPointerException("serverList must not bu null."));

        this.fullNames.addAll(serverIds.stream().map(XHost::getHostId).collect(Collectors.toList()));
    }

    public List<String> getFullNames() {
        return fullNames;
    }
}
