package io.treasure.jro;


import io.treasure.config.MyRedisPool;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Map;


public class Test{

    Jedis myJedis = MyRedisPool.getJedis();


}
