package com.navercorp.pinpoint.web.topo.usercases;

import com.google.common.collect.Lists;
import com.navercorp.pinpoint.common.topo.domain.TopoLine;
import com.navercorp.pinpoint.common.topo.domain.XLink;
import com.navercorp.pinpoint.common.topo.domain.XNode;
import com.navercorp.pinpoint.web.vo.XServiceTopo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

public class CalculateTopoServiceUserCase {

    private List<TopoLine> topoLines;

    public CalculateTopoServiceUserCase(List<TopoLine> topoLines) {
        this.topoLines = topoLines;
    }

    public XServiceTopo getServiceTopo() {
        List<XNode> resultXNodes = new ArrayList<XNode>();
        List<XLink> resultXLinks = new ArrayList<XLink>();
        mappingAndMergeXnodesAndXlinks(resultXNodes, resultXLinks, topoLines);
        return new XServiceTopo(resultXNodes, resultXLinks);
    }

    public XServiceTopo getBound1ServiceTopo(String serviceName) {
        List<XNode> xNodesAfterMerge = new ArrayList<XNode>();
        List<XLink> xLinksAfterMerge = new ArrayList<XLink>();
        mappingAndMergeXnodesAndXlinks(xNodesAfterMerge, xLinksAfterMerge, topoLines);
        return generateBound1XServiceTopo(xLinksAfterMerge, xNodesAfterMerge, serviceName);
    }


    public XServiceTopo generateBound1XServiceTopo(List<XLink> xLinksAfterMerge, List<XNode> xNodesAfterMerge, String serviceName) {
        if (null == xLinksAfterMerge || xLinksAfterMerge.size() == 0) {
            return new XServiceTopo(Lists.<XNode>newArrayList(), Lists.<XLink>newArrayList());
        }
        List<XNode> upstreamXNodes = newArrayList();
        List<XLink> upstreamXLinks = newArrayList();

        List<XNode> downstreamXNodes = newArrayList();
        List<XLink> downstreamXLinks = newArrayList();

        List<XNode> resultXNodes = newArrayList();
        List<XLink> resultXLinks = newArrayList();

        for (XLink xlink : xLinksAfterMerge) {
            String fromName = xlink.getFrom();
            String toName = xlink.getTo();
            if (fromName.equals(serviceName) && toName.equals(serviceName)) {
                resultXLinks.add(xlink);
            }
            if (fromName.equals(serviceName) && !toName.equals(serviceName)) {
                downstreamXLinks.add(xlink);
                findAndAddXNodeToResultXNodes(xNodesAfterMerge, xlink.getTo(), resultXNodes);
            }
            if (!fromName.equals(serviceName) && toName.equals(serviceName)) {
                upstreamXLinks.add(xlink);
                findAndAddXNodeToResultXNodes(xNodesAfterMerge, xlink.getFrom(), resultXNodes);
            }
        }
        findAndAddXNodeToResultXNodes(xNodesAfterMerge, serviceName, resultXNodes);

        resultXNodes.addAll(upstreamXNodes);
        resultXNodes.addAll(downstreamXNodes);
        resultXLinks.addAll(upstreamXLinks);
        resultXLinks.addAll(downstreamXLinks);
        return new XServiceTopo(resultXNodes, resultXLinks);
    }

    private void findAndAddXNodeToResultXNodes(List<XNode> resultXNodes, String xnodeName, List<XNode> xNodes) {
        resultXNodes.stream().filter(node -> node.getName().equals(xnodeName)).filter(node -> !xNodes.contains(node)).forEachOrdered(xNodes::add);
    }


    public void mappingAndMergeXnodesAndXlinks(List<XNode> resultXNodes, List<XLink> resultXLinks, List<TopoLine> topoLines) {
        Map<String, List<XNode>> xNodesMap = new HashMap<String, List<XNode>>();
        Map<String, List<XLink>> xLinksMap = new HashMap<String, List<XLink>>();
        mergeTopoLineSet(topoLines, xNodesMap, xLinksMap);
        mergeXNodes2ResultXNodes(xNodesMap, resultXNodes);
        mergeXLinks2ResultXLinks(xLinksMap, resultXNodes, resultXLinks);
    }

    public void mergeXLinks2ResultXLinks(Map<String, List<XLink>> xLinksMap, List<XNode> resultXNodes, List<XLink> resultXLinks) {
        for (Map.Entry<String, List<XLink>> entry : xLinksMap.entrySet()) {
            List<XLink> xLinksByFromTo = entry.getValue();
            mergerXLinks(xLinksByFromTo, resultXLinks, resultXNodes);
        }
    }


    public void mergeXNodes2ResultXNodes(Map<String, List<XNode>> xNodesMap, List<XNode> resultXNodes) {
        for (Map.Entry<String, List<XNode>> entry : xNodesMap.entrySet()) {
            List<XNode> xNodeMap = entry.getValue();
            long responseTime = 0;
            long calls = 0;
            long errors = 0;
            short serviceType = (short) 0;
            for (XNode xnode : xNodeMap) {
                responseTime += xnode.getResponseTime();
                calls += xnode.getCalls();
                errors += xnode.getErrors();
                serviceType = xnode.getServiceType();
            }
            XNode xnodeTemp = new XNode(xNodeMap.get(0).getName());
            xnodeTemp.setResponseTime(responseTime);
            xnodeTemp.setErrors(errors);
            xnodeTemp.setCalls(calls);
            xnodeTemp.setServiceType(serviceType);
            if (!resultXNodes.contains(xnodeTemp)) {
                resultXNodes.add(xnodeTemp);
            }
        }
    }

    public void mergeTopoLineSet(List<TopoLine> topoLines, Map<String, List<XNode>> xNodesMap, Map<String, List<XLink>> xLinksMap) {
        for (TopoLine topoLine : topoLines) {
            List<XNode> xNodesPerTopoLine = topoLine.getXNodes();
            List<XLink> xLinksPerTopoLine = topoLine.getXLinks();
            for (XNode xnode : xNodesPerTopoLine) {
                if (xNodesMap.containsKey(xnode.getName())) {
                    xNodesMap.get(xnode.getName()).add(xnode);
                } else {
                    List<XNode> listXnode = new ArrayList<XNode>();
                    listXnode.add(xnode);
                    xNodesMap.put(xnode.getName(), listXnode);
                }
            }
            for (XLink xlink : xLinksPerTopoLine) {
                String key = xlink.getFrom() + xlink.getTo();
                if (xLinksMap.containsKey(key)) {
                    xLinksMap.get(key).add(xlink);
                } else {
                    List<XLink> listXlink = new ArrayList<XLink>();
                    listXlink.add(xlink);
                    xLinksMap.put(key, listXlink);
                }
            }
        }
    }


    private void mergerXLinks(List<XLink> xLinksByFromTo, List<XLink> resultXLinks, List<XNode> resultXnodes) {
        long responseTime = 0;
        long calls = 0;
        long errors = 0;
        for (XLink xlink : xLinksByFromTo) {
            responseTime += xlink.getResponseTime();
            calls += xlink.getCalls();
            errors += xlink.getErrors();
        }
        XLink xLink = new XLink(xLinksByFromTo.get(0).getFrom(), (xLinksByFromTo.get(0).getTo()));
        xLink.setCalls(calls);
        if (errors != 0) {
            xLink.setErrors(errors);
            xLink.setHasError(true);
        }
        xLink.setResponseTime(responseTime);
        resultXLinks.add(xLink);
        attachFromsAndTosXlinks2ResultXNodes(xLinksByFromTo, resultXnodes, xLink);
    }

    private void attachFromsAndTosXlinks2ResultXNodes(List<XLink> xLinksByFromTo, List<XNode> resultXnodes, XLink xLink) {
        for (XNode xnode : resultXnodes) {
            if (xnode.getName().equals(xLinksByFromTo.get(0).getFrom())) {
                List<XLink> froms = xnode.getFroms();
                froms.add(xLink);
                xnode.setFroms(froms);
            }
            if (xLinksByFromTo.get(0).getTo().equals(xnode.getName())) {
                List<XLink> tos = xnode.getTos();
                tos.add(xLink);
                xnode.setTos(tos);
            }
        }
    }

    public List<TopoLine> getTopoLines() {
        return topoLines;
    }

    public void setTopoLines(List<TopoLine> topoLines) {
        this.topoLines = topoLines;
    }
}
