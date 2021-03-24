package com.adapters.srv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ofbiz.entity.GenericDelegator;
import org.apache.ofbiz.entity.GenericValue;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;

import static org.junit.Assert.*;

public class CcTestSrvsTest {

    @Test
    public void testService() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        // Map<String, Object> mapValues=new HashMap<>();
        String json = "{ \"color\" : \"Black\", \"type\" : \"BMW\" }";
        Map<String, Object> mapValues
                = objectMapper.readValue(json, new TypeReference<Map<String,Object>>(){});
        System.out.println(mapValues);
    }

    @Test
    public void testMapValues() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String fileName="map_values.json";
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                this.getClass().getResourceAsStream("/" + fileName)));
        Map<String, Object> mapValues
                = objectMapper.readValue(reader, new TypeReference<Map<String,Object>>(){});
        System.out.println("result map -> "+mapValues);

        {
            Map<String, Object> entMap = (Map<String, Object>) mapValues.get("message");
            String objType = (String) entMap.get("type");
            if (objType.equals("entity")) {
                String entName = (String) entMap.get("entityName");
                System.out.println("entity " + entName + " with attrs " + entMap.get("value"));
            }
        }

        // ...
        for (Map.Entry<String,Object> entry : mapValues.entrySet()){
            if(entry.getValue() instanceof Map){
                Map<String, Object> valMap=(Map<String, Object>)entry.getValue();
                String objType=(String)valMap.getOrDefault("type", "raw");
                if(objType.equals("entity")){
                    String entName=(String)valMap.get("entityName");
                    System.out.println("entity "+entName+" with attrs "+valMap.get("value"));
                }
            }
        }

    }
}

