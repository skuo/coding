package com.coding.util;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RandomUtil {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private Random random = new Random();
    
    public boolean oneInN(int n) {
        int randomInt = random.nextInt((3-1)+1)+1;
        log.info("randomInt=" + randomInt);
        if (randomInt == n)
            return true;
        else
            return false;
    }
    
    public void randomRunLong(int sleepTimeInMs, int n) {
        if (oneInN(n)) {
            try {
                log.info("Sleep " + sleepTimeInMs + " (ms)");
                Thread.sleep(sleepTimeInMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
