package com.redissouce;

import com.GLOBALSINGLETON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Samuel on 16/8/1.
 */
public class SRedis {
    /**
     * 单例
     */
    private static SRedis sRedis;

    /**
     * 非切片链接池
     */
    private JedisPool jedisPool;
    /**
     * 切片链接池
     */
    private ShardedJedisPool shardedJedisPool;
    /**
     * log记录
     */
    static Logger logger = LogManager.getLogger(SRedis.class.getName());
//    /** * 非切片客户端链接 */
//    private Jedis jedis;
//    /** * 切片客户端链接 */
//    private ShardedJedis shardedJedis;
    /**
    * 获取 ip
    * */
    private static String getIp() {
        if (GLOBALSINGLETON.S().ENVIRONMENT == GLOBALSINGLETON.ENVIRONMENTENUM.RELASE){
            return GLOBALSINGLETON.S().REDIS_PUBLIC_HOST;
        }
        else if (GLOBALSINGLETON.S().ENVIRONMENT == GLOBALSINGLETON.ENVIRONMENTENUM.DEVELOP){
            return GLOBALSINGLETON.S().REDIS_DEVELOP_HOST;
        }
        return GLOBALSINGLETON.S().REDIS_TEST_HOST;
    }

    private SRedis(){
    }
    public static SRedis s(){
        if(sRedis==null){
            sRedis=new SRedis();
            sRedis.jedisPool = sRedis.initialPool();
            sRedis.shardedJedisPool = sRedis.initialShardedPool();
//            sRedis.show();
        }
        return sRedis;
    }

    public void clearSelf(){
        this.sRedis = null;
    }

    /**
     * 初始化切片池
     */
    private JedisPool initialPool() {
        logger.warn("initialPool");
        if (jedisPool==null)
        {
            // 池基本配置
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(20);
            config.setMaxIdle(5);
            config.setMaxWaitMillis(1000l);
            config.setTestOnBorrow(false);
            jedisPool = new JedisPool(config, getIp(), GLOBALSINGLETON.S().REDIS_PORT);
        }
        return jedisPool;
    }
    private ShardedJedisPool initialShardedPool() {
        logger.warn("initialShardedPool");
        if (shardedJedisPool==null)
        {
            // 池基本配置
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(200);
            config.setMaxIdle(10);
            config.setMaxWaitMillis(10000);
            config.setTestOnBorrow(false);
            // slave链接
            List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
            shards.add(new JedisShardInfo(getIp(), GLOBALSINGLETON.S().REDIS_PORT, "master"));
            // 构造池
            shardedJedisPool = new ShardedJedisPool(config, shards);
        }
        return shardedJedisPool;
    }

    private synchronized Jedis getJedis() {
        try {
            if (this.jedisPool != null) {
                Jedis jedis = this.jedisPool.getResource();
                return jedis;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private synchronized ShardedJedis getShardedJedis() {
        try {
            if (this.shardedJedisPool != null) {
                ShardedJedis shardedJedis = this.shardedJedisPool.getResource();
                return shardedJedis;
            }
            return null;
        } catch (Exception e) {
            logger.warn("getShardedJedis error:"+e);
            e.printStackTrace();
            return null;
        }
    }


    public boolean cleanAll(){
        try {
            Jedis jedis = this.getJedis();
            String stats = jedis.flushDB();
            jedis.close();
            return stats.equals("OK");
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 缓存string 用于类似商品详情的map,不需要修改,转成json后存储 有过期时间
     * @param stringName
     * @param value
     * @return
     */
    public boolean setStringToRedis(String stringName,String value) {
        try {
            String stats = "";
            ShardedJedis shardedJedis = this.getShardedJedis();
            if (shardedJedis!=null){
                stats = shardedJedis.set(stringName, value);
                return stats.equals("OK");
            }
            else {
                Jedis jedis = this.getJedis();
                if (jedis!=null){
                    stats = jedis.set(stringName, value);
                    jedis.close();
                }
            }
            shardedJedis.close();
            return stats.equals("OK");
        }catch (Exception e){
            logger.warn("setStringToRedis error:"+e);
            logger.warn("setStringToRedis stringName="+stringName);
            logger.warn("setStringToRedis value="+value);
            clearSelf();
            return false;
        }
    }
    public boolean setStringToRedisbyTime(String stringName,String value,Integer second){
        try {
            String stats = "";
            ShardedJedis shardedJedis = this.getShardedJedis();
            if (shardedJedis!=null){
                stats = shardedJedis.setex(stringName,second, value);
                return stats.equals("OK");
            }
            else {
                Jedis jedis = this.getJedis();
                if (jedis!=null){
                    stats = jedis.setex(stringName,second, value);
                    jedis.close();
                }
            }
            shardedJedis.close();
            return stats.equals("OK");
        }catch (Exception e){
            logger.warn("setStringToRedisbyTime error:"+e);
            clearSelf();
            return false;
        }
    }

    public String getStringFromRedis(String stringName){
        try {
            ShardedJedis shardedJedis = this.getShardedJedis();
            if (shardedJedis!=null && shardedJedis.exists(stringName)){
                String string = shardedJedis.get(stringName);
                shardedJedis.close();
                return string;
            }
            else {
                Jedis jedis = this.getJedis();
                if (jedis!=null){
                    String string = jedis.get(stringName);
                    jedis.close();
                    return string;
                }
            }
            shardedJedis.close();
            return null;
        }catch (Exception e){
            logger.warn("getStringFromRedis error:"+e);
            logger.warn("getStringFromRedis stringName="+stringName);
            clearSelf();
            return null;
        }

    }
    public boolean deleteStringFromRedis(String stringName){
        try {
            ShardedJedis shardedJedis = this.getShardedJedis();
            if (shardedJedis!=null && shardedJedis.exists(stringName)){
                Long stats = shardedJedis.del(stringName);
                shardedJedis.close();
                return stats > 0;
            }
            else {
                Jedis jedis = this.getJedis();
                if (jedis!=null){
                    Long stats = jedis.del(stringName);
                    jedis.close();
                    return stats > 0;
                }
            }
            shardedJedis.close();
            return true;
        }catch (Exception e){
            logger.warn("deleteStringFromRedis error:"+e);
            logger.warn("deleteStringFromRedis stringName="+stringName);
            clearSelf();
            return false;
        }
    }


//    public void show() {
//        // key检测
//        testKey();
//        // string检测
//        testString();
//        // list检测
//        testList();
//        // set检测
//        testSet();
//        // sortedSet检测
//        testSortedSet();
//        // hash检测
//        testHash();
//        shardedJedisPool.returnResource(shardedJedis);
//    }
//
//    private void testKey() {
//        System.out.println("=============key==========================");
//        // 清空数据
//        System.out.println(jedis.flushDB());
//        System.out.println(jedis.echo("foo"));
//        // 判断key否存在
//        System.out.println(shardedJedis.exists("foo"));
//        shardedJedis.set("key", "values");
//        System.out.println(shardedJedis.exists("key"));
//    }
//
//    private void testString() {
//        System.out.println("=============String==========================");
//        // 清空数据
//        System.out.println(jedis.flushDB());
//        // 存储数据
////        shardedJedis.set("foo", "bar");
//        System.out.println(shardedJedis.get("foo"));
//        // 若key不存在，则存储
//        shardedJedis.setnx("foo", "foo not exits");
//        System.out.println(shardedJedis.get("foo"));
//        // 覆盖数据
//        shardedJedis.set("foo", "foo update");
//        System.out.println(shardedJedis.get("foo"));
//        // 追加数据
//        shardedJedis.append("foo", " hello, world");
//        System.out.println(shardedJedis.get("foo"));
//        // 设置key的有效期，并存储数据
//        System.out.println(shardedJedis.setex("foo", 2, "foo not exits"));
//
//        System.out.println(shardedJedis.get("foo"));
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//        }
//        System.out.println(shardedJedis.get("foo"));
//        // 获取并更改数据
//        shardedJedis.set("foo", "foo update");
//        System.out.println(shardedJedis.getSet("foo", "foo modify"));
//        // 截取value的值
//        System.out.println(shardedJedis.getrange("foo", 1, 3));
//        System.out.println(jedis.mset("mset1", "mvalue1", "mset2", "mvalue2",
//                "mset3", "mvalue3", "mset4", "mvalue4"));
//        System.out.println(jedis.mget("mset1", "mset2", "mset3", "mset4"));
//        System.out.println(jedis.del(new String[]{"foo", "foo1", "foo3"}));
//    }
//
//    private void testList() {
//        System.out.println("=============list==========================");
//        // 清空数据
//        System.out.println(jedis.flushDB());
//        // 添加数据
//        shardedJedis.lpush("lists", "vector");
//        shardedJedis.lpush("lists", "ArrayList");
//        shardedJedis.lpush("lists", "LinkedList");
//        // 数组长度
//        System.out.println(shardedJedis.llen("lists"));
//        // 排序
//        System.out.println(shardedJedis.sort("lists"));
//        // 字串
//        System.out.println(shardedJedis.lrange("lists", 0, 3));
//        // 修改列表中单个值
//        shardedJedis.lset("lists", 0, "hello list!");
//        // 获取列表指定下标的值
//        System.out.println(shardedJedis.lindex("lists", 1));
//        // 删除列表指定下标的值
//        System.out.println(shardedJedis.lrem("lists", 1, "vector"));
//        // 删除区间以外的数据
//        System.out.println(shardedJedis.ltrim("lists", 0, 1));
//        // 列表出栈
//        System.out.println(shardedJedis.lpop("lists"));
//        // 整个列表值
//        System.out.println(shardedJedis.lrange("lists", 0, -1));
//    }
//
//    private void testSet() {
//        System.out.println("=============set==========================");
//        // 清空数据
//        System.out.println(jedis.flushDB());
//        // 添加数据
//        shardedJedis.sadd("sets", "HashSet");
//        shardedJedis.sadd("sets", "SortedSet");
//        shardedJedis.sadd("sets", "TreeSet");
//        // 判断value是否在列表中
//        System.out.println(shardedJedis.sismember("sets", "TreeSet"));
//        ;
//        // 整个列表值
//        System.out.println(shardedJedis.smembers("sets"));
//        // 删除指定元素
//        System.out.println(shardedJedis.srem("sets", "SortedSet"));
//        // 出栈
//        System.out.println(shardedJedis.spop("sets"));
//        System.out.println(shardedJedis.smembers("sets"));
//        //
//        shardedJedis.sadd("sets1", "HashSet1");
//        shardedJedis.sadd("sets1", "SortedSet1");
//        shardedJedis.sadd("sets1", "TreeSet");
//        shardedJedis.sadd("sets2", "HashSet2");
//        shardedJedis.sadd("sets2", "SortedSet1");
//        shardedJedis.sadd("sets2", "TreeSet1");
//        // 交集
//        System.out.println(jedis.sinter("sets1", "sets2"));
//        // 并集
//        System.out.println(jedis.sunion("sets1", "sets2"));
//        // 差集
//        System.out.println(jedis.sdiff("sets1", "sets2"));
//    }
//
//    private void testSortedSet() {
//        System.out.println("=============zset==========================");
//        // 清空数据
//        System.out.println(jedis.flushDB());
//        // 添加数据
//        shardedJedis.zadd("zset", 10.1, "hello");
//        shardedJedis.zadd("zset", 10.0, ":");
//        shardedJedis.zadd("zset", 9.0, "zset");
//        shardedJedis.zadd("zset", 11.0, "zset!");
//        // 元素个数
//        System.out.println(shardedJedis.zcard("zset"));
//        // 元素下标
//        System.out.println(shardedJedis.zscore("zset", "zset"));
//        // 集合子集
//        System.out.println(shardedJedis.zrange("zset", 0, -1));
//        // 删除元素
//        System.out.println(shardedJedis.zrem("zset", "zset!"));
//        System.out.println(shardedJedis.zcount("zset", 9.5, 10.5));
//        // 整个集合值
//        System.out.println(shardedJedis.zrange("zset", 0, -1));
//    }
//
//    private void testHash() {
//        System.out.println("=============hash==========================");
//        // 清空数据
//        System.out.println(jedis.flushDB());
//        // 添加数据
//        shardedJedis.hset("hashs", "entryKey", "entryValue");
//        shardedJedis.hset("hashs", "entryKey1", "entryValue1");
//        shardedJedis.hset("hashs", "entryKey2", "entryValue2");
//        // 判断某个值是否存在
//        System.out.println(shardedJedis.hexists("hashs", "entryKey"));
//        // 获取指定的值
//        System.out.println(shardedJedis.hget("hashs", "entryKey"));
//        // 批量获取指定的值
//        System.out
//                .println(shardedJedis.hmget("hashs", "entryKey", "entryKey1"));
//        // 删除指定的值
//        System.out.println(shardedJedis.hdel("hashs", "entryKey"));
//        // 为key中的域 field 的值加上增量 increment
//        System.out.println(shardedJedis.hincrBy("hashs", "entryKey", 123l));
//        // 获取所有的keys
//        System.out.println(shardedJedis.hkeys("hashs"));
//        // 获取所有的values
//        System.out.println(shardedJedis.hvals("hashs"));
//    }
}
