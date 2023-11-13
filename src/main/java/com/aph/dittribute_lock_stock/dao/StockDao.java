package com.aph.dittribute_lock_stock.dao;

import com.aph.dittribute_lock_stock.entity.GoodsStockEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface StockDao {
    @Select("select stock from goods_stock where goods_id = #{goodsId}")
    Integer selectStockByGoodsId(@Param("goodsId") Long goodsId);


    @Update("update goods_stock set stock = #{stock} where goods_id = #{goodsId}")
    Integer updateStockByGoodsId(@Param("goodsId") Long goodsId, @Param("stock") Integer stock);

    @Update("update goods_stock set stock = stock - #{count} where goods_id = #{goodsId} and stock >= #{count}")
    Integer updateStockByGoodsIdAndCount (@Param("goodsId") Long goodsId, @Param("count") Integer count);

    @Select("select stock from goods_stock where goods_id = #{goodsId} for update")
    Integer selectStockByGoodsIdForUpdate(@Param("goodsId") Long goodsId);

    @Select("select id, stock, version from goods_stock where goods_id = #{goodsId}")
    List<GoodsStockEntity> selectStockAndVersionByGoodsId(@Param("goodsId") Long goodsId);

    @Update("update goods_stock set stock = #{stock}, version = #{version} + 1 where goods_id = #{goodsId} and version = #{version}")
    Integer updateStockAndVersionByGoodsIdAndVersion(@Param("goodsId") Long goodsId, @Param("stock") Integer stock, @Param("version") Integer version);
}
