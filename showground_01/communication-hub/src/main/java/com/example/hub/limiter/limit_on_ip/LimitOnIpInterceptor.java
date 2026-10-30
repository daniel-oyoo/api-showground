package com.example.hub.limiter.limit_on_ip;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component 
public  class LimitOnIpInterceptor implements  HandlerInterceptor{

    private final static Logger logger = Logger.getLogger(LimitOnIpInterceptor.class.getName());

    private final LimitOnIp limitOnIp;

    //@Autowired 
    public LimitOnIpInterceptor(LimitOnIp limitOnIp) {
        this.limitOnIp=limitOnIp;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws IOException {

        //get ip
        String ip = request.getRemoteAddr();

        if(!limitOnIp.exist(ip)){
           limitOnIp.addNew(ip.toString(),new AtomicInteger(10));
           return true;
        }

        AtomicInteger remaining = limitOnIp.getCount(ip);
        if(remaining.intValue()<=0){
        logger.info("Rate Limit hit");
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        //response.setHeader("Retry-After", Long.toString(rateLimiter.secondsUntilReset()));
        response.getWriter().write(
                "{\"error\":\"Too Many Requests\",\"message\":\"System saturated. Retry later.\"}");
       
            return false;
        }else{
            logger.info("Remaining req " + remaining.intValue());
            remaining.getAndDecrement();
            //return true;
        }

        return true;
    }
}