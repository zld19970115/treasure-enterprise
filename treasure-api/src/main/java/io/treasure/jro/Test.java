package io.treasure.jro;


import io.treasure.config.MyRedisPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import java.util.Map;

public class Test{

    @Autowired
    Jedis myJedis;


    public void addFlag(){

    }


}
