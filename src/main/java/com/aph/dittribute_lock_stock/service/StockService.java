package com.aph.dittribute_lock_stock.service;

import com.aph.dittribute_lock_stock.dao.StockDao;
import com.aph.dittribute_lock_stock.entity.GoodsStockEntity;
import com.aph.dittribute_lock_stock.lock.AbstractLock;
import com.aph.dittribute_lock_stock.lock.RedisLock;
import io.netty.util.internal.StringUtil;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Service
public class StockService {

    @Autowired
    private StockDao stockDao;

    @Autowired
    private StringRedisTemplate template;

    //注入redisson
    @Autowired
    private RedissonClient redissonClient;

    //@Transactional
    public synchronized String deductStock(Long goodsId, Integer count){

        //1.查询商品库存的库存数量
        Integer stock = stockDao.selectStockByGoodsId(goodsId);
        //2.判断数量是否足够
        if(stock < count) {
            return "库存不足";
        }

        //3.如果足够，减去库存
        stockDao.updateStockByGoodsId(goodsId, stock - count);
        return  "库存扣减成功";

    }


    public String deductStockOneUpdate(Long goodsId, Integer count){


        //使用一条语句更新
        Integer result = stockDao.updateStockByGoodsIdAndCount(goodsId, count);
        if(result > 0) {
            return  "库存扣减成功";
        }
        return  "库存不足";

    }


    @Transactional
    public  String deductStockForUpdate(Long goodsId, Integer count){

        //1.查询商品库存的库存数量
        Integer stock = stockDao.selectStockByGoodsIdForUpdate(goodsId);
        //2.判断数量是否足够
        if(stock < count) {
            return "库存不足";
        }

        //3.如果足够，减去库存
        stockDao.updateStockByGoodsId(goodsId, stock - count);
        return  "库存扣减成功";

    }


    public  String deductStockCAS(Long goodsId, Integer count){
        Integer result = 0;
        while(result == 0){
            //1.查询商品库存的库存数量 + version
            List<GoodsStockEntity> goodsStockEntities = stockDao.selectStockAndVersionByGoodsId(goodsId);

            if(CollectionUtils.isEmpty(goodsStockEntities)) {
                return "商品不存在";
            }

            GoodsStockEntity goodsStockEntity = goodsStockEntities.get(0);

            //2.判断数量是否足够
            if(goodsStockEntity.getStock() < count) {
                return "库存不足";
            }

            //3.如果足够，减去库存
            result = stockDao.updateStockAndVersionByGoodsIdAndVersion(goodsId,
                    goodsStockEntity.getStock() - count, goodsStockEntity.getVersion());
        }

        return  "库存扣减成功";

    }


    public String deductStockRedis(Long goodsId, Integer count) {
        //1、查询商品库存的库存数量
        String stock = template.opsForValue().get("stock" + goodsId);
        if (StringUtil.isNullOrEmpty(stock)) {
            return "商品不存在";
        }
        Integer lastStock = Integer.parseInt(stock);
        //2、判断库存数量是否足够
        if (lastStock < count) {
            return "库存不足";
        }
        //3、如果库存数量足够，那么就去扣减库存
        template.opsForValue().set("stock" + goodsId, String.valueOf(lastStock - count));
        return "库存扣减成功";
    }


    public  String deductStockRedisLock(Long goodsId, Integer count){
        AbstractLock lock = null;

        try {
            lock = new RedisLock(template, "lock" + goodsId);
            lock.lock();
            lock(lock);
            //1、查询商品库存的库存数量
            String stock = template.opsForValue().get("stock" + goodsId);
            if (StringUtil.isNullOrEmpty(stock)) {
                return "商品不存在";
            }
            Integer lastStock = Integer.parseInt(stock);
            //2、判断库存数量是否足够
            if (lastStock < count) {
                return "库存不足";
            }
            //3、如果库存数量足够，那么就去扣减库存
            template.opsForValue().set("stock" + goodsId, String.valueOf(lastStock - count));
            return "库存扣减成功";
        } finally {
            if(lock != null) {
                lock.unlock();
            }
        }

    }

    public String deductStockRedisRedisson(Long goodsId, Integer count) {
        RLock lock = null;
        try {
            lock = redissonClient.getLock("lock" + goodsId);
            lock.lock();
            //1、查询商品库存的库存数量
            String stock = template.opsForValue().get("stock" + goodsId);
            if (StringUtil.isNullOrEmpty(stock)) {
                return "商品不存在";
            }
            Integer lastStock = Integer.parseInt(stock);
            //2、判断库存数量是否足够
            if (lastStock < count) {
                return "库存不足";
            }
            //3、如果库存数量足够，那么就去扣减库存
            template.opsForValue().set("stock" + goodsId, String.valueOf(lastStock - count));
            return "库存扣减成功";
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(lock != null) {
                lock.unlock();
            }
        }

    }


    private void lock(Lock lock) {
        lock.lock();
        lock.unlock();
    }
}
