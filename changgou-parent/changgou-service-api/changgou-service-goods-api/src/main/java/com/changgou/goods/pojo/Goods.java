package com.changgou.goods.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author hongchen
 * @date 2020/4/7 1:43
 */
public class Goods implements Serializable {
    private Spu spu;// 商品的基本信息
    private List<Sku> skuList;// 商品对应的库存信息

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }
}
