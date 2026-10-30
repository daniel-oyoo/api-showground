package com.example.hub.limiter.limit_on_ip;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;
@Component 
public class LimitOnIp{
    private final static Logger logger = Logger.getLogger(LimitOnIp.class.getName());
    private final Map<String,AtomicInteger>limitDb = new ConcurrentHashMap<>();
    
    public AtomicInteger getCount(String key){
      logger.info("Retreving count for ip " + key);
      synchronized(limitDb){

        return limitDb.get(key);
        
      }
    }

    public void addNew(String key,AtomicInteger count){
        logger.info("Adding new client ip " + key);
        synchronized(limitDb){limitDb.put(key,count);}
    }

    public boolean exist(String key){
        logger.info("Checking if ip exists " + key);
        synchronized(limitDb){return limitDb.containsKey(key);}
    }
}

