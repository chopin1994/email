package com.nexus.website.impl.utils;

import com.nexus.website.impl.exceptions.RedisClientException;
import com.nexus.website.impl.spring.AppSpringContext;
import com.nexus.website.impl.utils.jedis.JedisCallback;
import com.nexus.website.impl.utils.jedis.PipelineCallback;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
* @ClassName: JedisUtil 
* @Description: jedis工具类
 */
public class JedisUtil {
	private Logger log = LoggerFactory.getLogger(JedisUtil.class);
	private static int RETRY_NUM = 5;
	private static final String KEY_ENCODING = "UTF-8";

	private static JedisUtil jedisUtil = new JedisUtil();
	
	private ShardedJedisPool sharedJedisPool = null;
	
	private JedisUtil(){
		sharedJedisPool = AppSpringContext.getBean("shardedJedisPool",ShardedJedisPool.class);
	}
	
	public static JedisUtil getJedisInstance(){
		return jedisUtil;
	}
	/**
	 * 获取ShardedJedis池资源
	 * @return
	 */
	public ShardedJedis getJedis(){
		return sharedJedisPool.getResource();
	}
	/**
	 * 返回ShardedJedis池资源
	 */
	public void returnJedisResource(ShardedJedis shardedJedis) {
		if(shardedJedis == null){
			return;
		}
		sharedJedisPool.returnResource(shardedJedis);
	}
	
	public void returnJedisBrokenResource(ShardedJedis shardedJedis){
		if(shardedJedis == null){
			return;
		}
		sharedJedisPool.returnBrokenResource(shardedJedis);
	}
	
	/**
	 * 
	* @Title: openPipeline 
	* @Description: jedis管道批量操作  支持异常情况下的5次补偿操作
	* @param @param callback    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	public void execJedisPipelineOperate(PipelineCallback callback){
		for(int index = 1; index <= RETRY_NUM ; index++){
			try{
				invokePipeline(callback);
			}catch(RedisClientException ex){
				if(index < RETRY_NUM){
					continue;
				}
				throw ex;
			}
			break;
		}
	}
	
	/**
	* @Title: execJedisOperate 
	* @Description: jedis操作  支持异常情况下的5次补偿操作
	* @param @param callback
	* @param @return
	* @return T    返回类型
	 */
	public <T> T execJedisOperate(JedisCallback<T> callback){
		T result = null;
		for(int index = 1; index <= RETRY_NUM ; index++){
			try{
				result = invokeJedis(callback);
			}catch(RedisClientException ex){
				if(index < RETRY_NUM){
					continue;
				}
				throw ex;
			}
			break;
		}
		return result;
	}
	
	/**
	* @Title: invokePipeline 
	* @Description: jedis管道操作核心方法
	* @param @param callback
	* @return void    返回类型
	 */
	private void invokePipeline(PipelineCallback callback){
		boolean isReturn = true;
		ShardedJedis shardedJedis = null;
		try{
			shardedJedis = getJedis();
			ShardedJedisPipeline pipeline = shardedJedis.pipelined();
			callback.invokePipeline(pipeline);
			pipeline.sync();
		}catch(Exception ex){
			if(ex instanceof JedisConnectionException){
				isReturn = false;
				returnJedisBrokenResource(shardedJedis);
			}
			throw new RedisClientException(ex);
		}finally{
			if(isReturn){
				returnJedisResource(shardedJedis);
			}
		}
	}
	
	/**
	* @Title: invokeJedis 
	* @Description: jedis操作核心方法
	* @param @param callback
	* @param @return
	* @return T    返回类型
	 */
	private <T> T invokeJedis(JedisCallback<T> callback){
		boolean isReturn = true;
		ShardedJedis shardedJedis = null;
		try{
			shardedJedis = getJedis();
			return callback.invoke(shardedJedis);
		}catch(Exception ex){
			if(ex instanceof JedisConnectionException){
				isReturn = false;
				returnJedisBrokenResource(shardedJedis);
			}
			throw new RedisClientException(ex);
		}finally{
			if(isReturn){
				returnJedisResource(shardedJedis);
			}
		}
	}
	
	/**
	* @Title: executeIncrValueToCache 
	* @Description: 得到从缓冲中自增num的值
	* @param @param pkKey
	* @param @param pkNum
	* @param @return
	* @return Long    返回类型
	 */
	public Long execIncrByToCache(final String cacheKey,final int num){
		Long pkVal = execJedisOperate(new JedisCallback<Long>() {
			@Override
			public Long invoke(ShardedJedis jedis) {
				return jedis.incrBy(cacheKey, (long)num);
			}
		});
		return pkVal;
	}
	
	/**
	* @Title: execIncrToCache 
	* @Description: 得到从缓冲中自增1的值
	* @param @param cacheKey
	* @param @return
	* @return Long    返回类型
	 */
	public Long execIncrToCache(final String cacheKey){
		Long pkVal = execJedisOperate(new JedisCallback<Long>() {
			@Override
			public Long invoke(ShardedJedis jedis) {
				return jedis.incr(cacheKey);
			}
		});
		return pkVal;
	}
	
	/**
	* @Title: execDecrByToCache 
	* @Description: 
	* @param @param cacheKey
	* @param @param num
	* @param @return
	* @return Long    返回类型
	 */
	public Long execDecrByToCache(final String cacheKey,final int num){
		Long pkVal = execJedisOperate(new JedisCallback<Long>() {
			@Override
			public Long invoke(ShardedJedis jedis) {
				return jedis.decrBy(cacheKey, (long)num);
			}
		});
		return pkVal;
	}
	
	/**
	* @Title: execDelToCache 
	* @Description: 删除缓存
	* @param @param cacheKey
	* @param @return
	* @return boolean    返回类型
	 */
	public boolean execDelToCache(final String cacheKey){
		return execJedisOperate(new JedisCallback<Boolean>() {
			@Override
			public Boolean invoke(ShardedJedis jedis) {
				return jedis.del(cacheKey)==
						0?false:true;
			}
		});
	}
	
	/**
    * @Title: execDelToCache 
    * @Description: 批量删除缓存
    * @param @param patternKey
    * @param @return
    * @return boolean  全部删除成功，返回true
     */
    public boolean execDelPatternKeyToCache(final String patternKey){
        return execJedisOperate(new JedisCallback<Boolean>() {
            @Override
            public Boolean invoke(ShardedJedis shardedJedis) {
                Collection<Jedis> jedises = shardedJedis.getAllShards();
                boolean result = true;
                for (Jedis jedis : jedises) {
                    Set<String> keys = jedis.keys(patternKey);
                    for (String key : keys) {
                        Long delNum = jedis.del(key);
                        if (result) {
                            result = (delNum == 
                               0?false:true);
                        }
                    }
                }
                return result;
            }
        });
    }
	
	/**
	* @Title: execSetToCache 
	* @Description: 存入缓存值 LRU
	* @param @param cacheKey
	* @param @param value
	* @return void  返回类型
	 */
	public void execSetToCache(final String cacheKey,final String value){
		execJedisOperate(new JedisCallback<Void>() {
			@Override
			public Void invoke(ShardedJedis jedis) {
				jedis.set(cacheKey, value);
				return null;
			}
		});
	}
	
	/**
	* @Title: execSetexToCache 
	* @Description: 执行
	* @param @param key
	* @param @param seconds
	* @param @param value
	* @return void    返回类型
	 */
	public void execSetexToCache(final String cacheKey,final int seconds,final String value){
		execJedisOperate(new JedisCallback<Void>() {
			@Override
			public Void invoke(ShardedJedis jedis) {
				jedis.setex(cacheKey, seconds,value);
				return null;
			}
		});
	}
	
	/**
	* @Title: execGetFromCache 
	* @Description: 
	* @param @param cacheKey
	* @param @return
	* @return String    返回类型
	 */
	public String execGetFromCache(final String cacheKey){
		return execJedisOperate(new JedisCallback<String>(){
			@Override
			public String invoke(ShardedJedis jedis) {
				return jedis.get(cacheKey);
			}
		});
	}
	
	/**
	* @Title: execExistsFromCache 
	* @Description: 是否已经缓存
	* @param @param cacheKey
	* @param @return
	* @return Boolean    返回类型
	 */
	public Boolean execExistsFromCache(final String cacheKey){
		return execJedisOperate(new JedisCallback<Boolean>() {
			@Override
			public Boolean invoke(ShardedJedis jedis) {
				return jedis.exists(cacheKey);
			}
		});
	}

	/**
	 * 是否存在通配货
	 * @param wildCard
	 * @return
	 */
	public Boolean execExistsWildCard(final String wildCard){
		return execJedisOperate(new JedisCallback<Boolean>() {
			@Override
			public Boolean invoke(ShardedJedis jedis) {
				for (Jedis shard : jedis.getAllShards()) {
					Set<String> keys = shard.keys(wildCard);
					if (!CollectionUtils.isEmpty(keys)) return true;
				}
				return false;
			}
		});
	}

	/**
	* @Title: execExpireToCache 
	* @Description: 设置过期时间
	* @param @param cacheKey
	* @param @return
	* @return Boolean    返回类型
	 */
	public Long execExpireToCache(final String cacheKey,final int seconds){
		return execJedisOperate(new JedisCallback<Long>() {
			@Override
			public Long invoke(ShardedJedis jedis) {
				return jedis.expire(cacheKey, seconds);
			}
		});
	}
	
	/**
	* @Title: execSetnxToCache 
	* @Description: 
	* @param @param cacheKey
	* @param @param value
	* @param @return
	* @return Long    返回类型
	 */
	public Long execSetnxToCache(final String cacheKey,final String value){
		return execJedisOperate(new JedisCallback<Long>(){
			@Override
			public Long invoke(ShardedJedis jedis) {
				return jedis.setnx(cacheKey, value);
			}});
	}
	
	/**
	 * @Title: execSetObjectToCache
	 * @Description: 存入缓存值object
	 * @param cacheKey
	 * @param obj
	 */
	public void execSetExpireObjectToCache(final String cacheKey,final int expireSeconds,final Object obj){
		execJedisOperate(new JedisCallback<Void>() {
			@Override
			public Void invoke(ShardedJedis jedis) {
				ObjectOutputStream oos = null;
				ByteArrayOutputStream baos = null;
				try {
					//序列化
					baos = new ByteArrayOutputStream();
					oos = new ObjectOutputStream(baos);
					oos.writeObject(obj);
					byte[] bytes = baos.toByteArray();
					jedis.setex(cacheKey.getBytes(KEY_ENCODING),expireSeconds,bytes);				
				} catch (Exception e) {
					log.error("",e);
				}
				return null;
			}
		});
	}
	
	/**
	 * @Title: execSetObjectToCache
	 * @Description: 存入缓存值object
	 * @param cacheKey
	 * @param obj
	 */
	public void execSetEternalObjectToCache(final String cacheKey,final Object obj){
		execJedisOperate(new JedisCallback<Void>() {
			@Override
			public Void invoke(ShardedJedis jedis) {
				ObjectOutputStream oos = null;
				ByteArrayOutputStream baos = null;
				try {
					//序列化
					baos = new ByteArrayOutputStream();
					oos = new ObjectOutputStream(baos);
					oos.writeObject(obj);
					byte[] bytes = baos.toByteArray();
					jedis.set(cacheKey.getBytes(KEY_ENCODING),bytes);					
				} catch (Exception e) {
					log.error("",e);
				}
				return null;
			}
		});
	}
	
	/**
	 * @Title: execSetObjectToCache
	 * @Description: 读取缓存值object
	 * @param cacheKey
	 * @return
	 */
	public Object execGetObjectFromCache(final String cacheKey){
		return execJedisOperate(new JedisCallback<Object>(){
			@Override
			public Object invoke(ShardedJedis jedis) {
				ByteArrayInputStream bais = null;
				try {
					byte[] bytes = jedis.get(cacheKey.getBytes(KEY_ENCODING));
					if (null != bytes) {
						//反序列化
						bais = new ByteArrayInputStream(bytes);
						ObjectInputStream ois = new ObjectInputStream(bais);
						return ois.readObject();
					}					
				} catch (Exception e) {
					log.error("",e);
				}
				return null;
			}
		});
	}
	
	/**
	* @Title: execIncrToCache 
	* @Description: 得到 key 的剩余有效时间
	* @param @param cacheKey
	* @param @return
	* @return Long    返回类型
	 */
	public long getRemainAliveTime(final String cacheKey) {
		long remainAliveTime = execJedisOperate(new JedisCallback<Long>() {
			@Override
			public Long invoke(ShardedJedis jedis) {
				return jedis.ttl(cacheKey);
			}
		});
		return remainAliveTime;
	}

	public Long execHSetToCache(final String key, final String field,final  String value) {

		return  execJedisOperate(new JedisCallback<Long>() {
			@Override
			public Long invoke(ShardedJedis jedis) {
				return jedis.hset(key, field, value);
			}
		});
	}

	public Long execHDelToCache(final String userAccount, final String[] fields) {

		return execJedisOperate(new JedisCallback<Long>() {
			@Override
			public Long invoke(ShardedJedis jedis) {
				return jedis.hdel(userAccount, fields);
			}
		});
	}

	public Long execHDelToCache(final String userAccount, final List<String> fields) {

		if (CollectionUtils.isEmpty(fields)) return 0l;
		final String[] fieldsArray = new String[fields.size()];
		fields.toArray(fieldsArray);

		return execJedisOperate(new JedisCallback<Long>() {
			@Override
			public Long invoke(ShardedJedis jedis) {
				return jedis.hdel(userAccount, fieldsArray);
			}
		});
	}

	public Map<String,String> execHgetFromCache(final String key) {

		return execJedisOperate(new JedisCallback<Map<String,String>>() {
			@Override
			public Map<String,String> invoke(ShardedJedis jedis) {
				return jedis.hgetAll(key);
			}
		});
	}
}
