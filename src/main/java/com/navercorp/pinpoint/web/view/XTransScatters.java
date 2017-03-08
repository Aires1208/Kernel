package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by root on 8/2/16.
 */
@JsonSerialize(using = XTransScattersSerializer.class)
public class XTransScatters {
    private List<XTransScatter> normals ;
    private List<XTransScatter> warnings ;
    private List<XTransScatter> criticals ;

    private XTransScatters(List<XTransScatter> normals
            ,List<XTransScatter> warnings,List<XTransScatter> criticals) {

        this.normals = normals;
        this.warnings = warnings;
        this.criticals = criticals;
    }

    public List<XTransScatter> getNormals() {
        return normals;
    }

    public List<XTransScatter> getWarnings() {
        return warnings;
    }

    public List<XTransScatter> getCriticals() {
        return criticals;
    }


    public static class Builder{
        private List<XTransScatter> normals = newArrayList();
        private List<XTransScatter> warnings = newArrayList();
        private List<XTransScatter> criticals = newArrayList();

        public Builder() {

        }
        public Builder Normals(List<XTransScatter> normals) {
            this.normals = normals;
            return this;
        }

        public Builder Warings(List<XTransScatter> warnings) {
            this.warnings = warnings;
            return this;
        }

        public Builder Criticals(List<XTransScatter> criticals) {
            this.criticals = criticals;
            return this;
        }


        public XTransScatters build() {
            return new XTransScatters(normals,warnings,criticals);

        }
    }


}
