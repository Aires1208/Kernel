package com.navercorp.pinpoint.web.view;

public class XType {
    private String id;
    private String value;

    public XType(String id, String value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XType xType = (XType) o;

        return id.equals(xType.id);
    }

    @Override
    public int hashCode(){
        return -1;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
