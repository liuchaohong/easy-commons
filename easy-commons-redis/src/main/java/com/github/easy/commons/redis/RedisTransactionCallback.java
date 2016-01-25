package com.github.easy.commons.redis;

import redis.clients.jedis.Transaction;

/**
 * Redis批处理方法的回调接口
 * 需要手动 Transaction.exec()
 * 
 * @see RedisTemplate
 * @param <T>
 */
public interface RedisTransactionCallback <T> {

	public T doInTransaction(Transaction tran);
	
}
