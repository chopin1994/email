package com.nexus.website.impl.utils.jedis;

import redis.clients.jedis.ShardedJedis;

public interface JedisCallback<T> {
	
	public T invoke(ShardedJedis jedis);

}
