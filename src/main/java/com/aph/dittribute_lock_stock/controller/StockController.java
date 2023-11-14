package com.aph.dittribute_lock_stock.controller;

import com.aph.dittribute_lock_stock.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("/stock/deductStock/{goodsId}/{count}")
    public String deductStock(@PathVariable Long goodsId,@PathVariable Integer count) {
        return stockService.deductStockRedisRedisson(goodsId,count);
    }
}
