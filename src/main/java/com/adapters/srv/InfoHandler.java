package com.adapters.srv;

import com.google.common.collect.Maps;

import java.util.Map;

public class InfoHandler {
    interface Handler{
        InfoResponse process(InfoRequest request);
    }
    Map<String, Handler> handlers= Maps.newHashMap();

    public InfoHandler(){
        handlers.put("views", new Handler() {
            @Override
            public InfoResponse process(InfoRequest request) {
                return InfoResponse.unimpl();
            }
        });
    }

    public InfoResponse handleRequest(InfoRequest request){
        Handler handler=handlers.getOrDefault(request.getTopic(), new Handler() {
            @Override
            public InfoResponse process(InfoRequest request) {
                return InfoResponse.unimpl();
            }
        });
        return handler.process(request);
    }
}


