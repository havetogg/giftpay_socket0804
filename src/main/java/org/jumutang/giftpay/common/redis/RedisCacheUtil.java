package org.jumutang.giftpay.common.redis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jumutang.giftpay.tools.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/***
 * @author chencq
 * @date 2017/8/1
 **/
@Service
public class RedisCacheUtil<T> {


    @Autowired
    @Qualifier("redisTemplate")
    public RedisTemplate redisTemplate;


    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     * @return 缓存的对象
     */
    public String setCacheObject(String key, String value) {
        ShardedJedis jedis =null;
        try {
            jedis = ((ShardedJedisPool) SpringContextUtil.getBean("shardedJedisPool")).getResource();
            return jedis.set(key,value);
        }finally {
            jedis.close();
        }
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public String getCacheObject(String key) {
        ShardedJedis jedis =null;
        try {
            jedis = ((ShardedJedisPool) SpringContextUtil.getBean("shardedJedisPool")).getResource();
            return jedis.get(key);
        }finally {
            jedis.close();
        }
    }


    /**
     * 获得缓存的Map
     *
     * @param key
     * @param hashOperation
     * @return
     */
    public String getCacheMap(String key/*,HashOperations<String,String,T> hashOperation*/) {
        ShardedJedis jedis =null;
        try {
            jedis = ((ShardedJedisPool) SpringContextUtil.getBean("shardedJedisPool")).getResource();
            return jedis.get(key);
        }finally {
            jedis.close();
        }
    }



    public long incrRedisCacheMap(String key,long pageView){
        ShardedJedis jedis =null;
        try {
            jedis = ((ShardedJedisPool) SpringContextUtil.getBean("shardedJedisPool")).getResource();
            return jedis.incrBy(key, pageView);
        }finally {
            jedis.close();
        }
    }

}
