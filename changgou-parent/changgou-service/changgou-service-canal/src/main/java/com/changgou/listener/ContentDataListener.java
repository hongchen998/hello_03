package com.changgou.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * @author hongchen
 * @date 2020/4/10 1:09
 */
@CanalEventListener
public class ContentDataListener {
    @Autowired(required = false)
    private ContentFeign contentFeign;
    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    @ListenPoint(destination = "example", schema = {"changgou_content"}, table = {"tb_content"},
            eventType = {CanalEntry.EventType.INSERT, CanalEntry.EventType.UPDATE})
    public void onEventContent(CanalEntry.EntryType entryType, CanalEntry.RowData rowData) {
        String categoryId = getCategoryId(rowData, "category_id");
        Result<List<Content>> result = contentFeign.findContentListByCategoryId(Long.parseLong(categoryId));
        List<Content> list = result.getData();
        stringRedisTemplate.boundValueOps("content_" + categoryId).set(JSON.toJSONString(list));
    }

    private String getCategoryId(CanalEntry.RowData rowData, String category_id) {
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            if (category_id.equals(column.getName())) {
                return column.getValue();
            }
        }
        return null;
    }
}
