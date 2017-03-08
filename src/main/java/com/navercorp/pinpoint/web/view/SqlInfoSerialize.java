package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.web.vo.Sql;
import com.navercorp.pinpoint.web.vo.SqlInfo;

import java.io.IOException;

import static com.navercorp.pinpoint.web.view.StringWrapper.*;

/**
 * Created by root on 16-10-8.
 */
public class SqlInfoSerialize extends JsonSerializer<SqlInfo> {
    @Override
    public void serialize(SqlInfo sqlInfo, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField("sqlInfo", sqlInfo.getSqlInfo());
        jgen.writeStringField("simplifyName", simplifyStr(sqlInfo.getSqlInfo()));
        jgen.writeNumberField("avgResponseTime", wrapNumberDouble(sqlInfo.getAvgElapsed()));
        jgen.writeNumberField("calls", sqlInfo.getCalls());
        jgen.writeNumberField("maxResponseTime", sqlInfo.getMaxElapsed());
        jgen.writeFieldName("sqlInvokeList");
        jgen.writeStartArray();
        for (Sql sql : sqlInfo.getSqlList()) {
            jgen.writeStartObject();
            jgen.writeStringField("startTime", FullDateStr(sql.getStartTime()));
            jgen.writeNumberField("elapsed", sql.getElapsed());
            jgen.writeStringField("transactionName", sql.getTransactionName());
            jgen.writeStringField("transactionId", sql.getTransactionId());
            jgen.writeStringField("dbName", sql.getDBName());
            jgen.writeEndObject();
        }
        jgen.writeEndArray();
        jgen.writeEndObject();
    }
}
