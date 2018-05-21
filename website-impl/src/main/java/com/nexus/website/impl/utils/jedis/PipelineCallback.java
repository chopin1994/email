package com.nexus.website.impl.utils.jedis;

import redis.clients.jedis.ShardedJedisPipeline;

/**
 * redis Pipeline 操作抽象类
 */
public interface PipelineCallback {
	/**
	 * 管道操作
	* @Title: invokePipeline 
	* @Description: 
	* @param @param pipeline    
	* @return void   
	* @throws
	 */
	public void invokePipeline(ShardedJedisPipeline pipeline);

}
