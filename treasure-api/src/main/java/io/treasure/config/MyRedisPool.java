package io.treasure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

//@ConfigurationProperties(prefix = "spring.redis")
//@Configuration
public class MyRedisPool {
    private static volatile JedisPool jedisPool = null;

    public MyRedisPool() {
    }

    public static JedisPool getJedisPoolInstance() {
        if (jedisPool == null) {
            synchronized (MyRedisPool.class) {
                if (jedisPool == null) {
                    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                    jedisPoolConfig.setMaxTotal(3000);
                    jedisPoolConfig.setMaxIdle(60);
                    jedisPoolConfig.setMaxWaitMillis(100 * 1000);
                    jedisPoolConfig.setTestOnBorrow(true);
                                                            //118.190.203.10747.105.143.248
                    jedisPool = new JedisPool(jedisPoolConfig, "47.105.143.248", 6379);

                }
            }
        }
        return jedisPool;
    }


    public static Jedis getJedis() {
        return getJedisPoolInstance().getResource();
    }

}
