package com.navercorp.pinpoint.web.vo;


import com.alibaba.fastjson.JSONObject;

/**
 * Created by 10183966 on 2016/10/10.
 */
public class ResultBuilder {
    public static final int SUCCESS = 1;
    public static final int FAIL = 0;
    private int status = SUCCESS;
    private JSONObject data;
    private String message;

    protected ResultBuilder() {
    }

    public static ResultBuilder newResult() {
        return new ResultBuilder();
    }

    public static ResultBuilder newResult(int status, JSONObject data, String message) {
        ResultBuilder resultBuilder = new ResultBuilder();
        resultBuilder.status(status);
        resultBuilder.message(message);
        resultBuilder.data(data);
        return resultBuilder;
    }

    public ResultBuilder status(int status) {
        this.status = status;
        return this;
    }

    public ResultBuilder data(JSONObject data) {
        this.data = data;
        return this;
    }

    public ResultBuilder message(String message) {
        this.message = message;
        return this;
    }

    public Result build() {
        Result result = new Result();
        result.setResMsg(message);
        result.setData(data);
        result.setStatus(status);
        return result;
    }

}
