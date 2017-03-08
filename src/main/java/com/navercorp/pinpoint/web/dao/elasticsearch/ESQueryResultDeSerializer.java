package com.navercorp.pinpoint.web.dao.elasticsearch;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;

import java.io.IOException;
import java.util.*;


public class ESQueryResultDeSerializer extends JsonDeserializer<ESQueryResult> {

    @Override
    public ESQueryResult deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        ObjectNode objectNode = jsonParser.getCodec().readTree(jsonParser);
        TextNode nameNode = (TextNode)objectNode.get("name");
        ArrayNode metricsNodes = (ArrayNode) objectNode.get("metrics");

        List<ESMetrics> esMetricses = new ArrayList<>();
        for (JsonNode node : metricsNodes) {
            Iterator<String> it =  node.fieldNames();
            Map<String,Object> map = new HashMap<>();
            while(it.hasNext()) {
                String fieldName = it.next();
                JsonNode valueNode = node.get(fieldName);
                if(valueNode instanceof IntNode) {
                    map.put(fieldName,valueNode.intValue());
                } else if(valueNode instanceof LongNode) {
                    map.put(fieldName,valueNode.longValue());
                } else if(valueNode instanceof DoubleNode) {
                    map.put(fieldName,valueNode.doubleValue());
                } else {
                    map.put(fieldName,valueNode.textValue());
                }
            }
            esMetricses.add(new ESMetrics(map));
        }
        return new ESQueryResult(nameNode.asText(),esMetricses);
    }
}
