package com.changgou.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.DeleteListenPoint;
import com.xpand.starter.canal.annotation.InsertListenPoint;
import com.xpand.starter.canal.annotation.UpdateListenPoint;

import java.util.List;

/**
 * @author hongchen
 * @date 2020/4/9 23:59
 */
@CanalEventListener
public class CanalDataEventListener {
    /**
     * 监控新增的数据
     * @param entryType 监控的事件
     * @param rowData 监控的行数据
     */
    @InsertListenPoint
    public void onEventInsert(CanalEntry.EntryType entryType, CanalEntry.RowData rowData){
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            String columnName = column.getName();
            String columnValue = column.getValue();
            System.out.println("列名称："+columnName+"，列的值："+columnValue);
        }

    }
    /**
     * 监控更新的的数据
     * @param entryType 监控的事件
     * @param rowData 监控的行数据
     */
    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.EntryType entryType, CanalEntry.RowData rowData){
        System.out.println("--------更新前的数据---------");
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        for (CanalEntry.Column column : beforeColumnsList) {
            String columnName = column.getName();
            String columnValue = column.getValue();
            System.out.println("列名称："+columnName+"，列的值："+columnValue);
        }
        System.out.println("--------更新后的数据---------");
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        for (CanalEntry.Column column : afterColumnsList) {
            String columnName = column.getName();
            String columnValue = column.getValue();
            System.out.println("列名称："+columnName+"，列的值："+columnValue);
        }

    }
    /**
     * 监控删除的数据
     * @param entryType 监控的事件
     * @param rowData 监控的行数据
     */
    @DeleteListenPoint
    public void onEventDelete(CanalEntry.EntryType entryType, CanalEntry.RowData rowData){
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        for (CanalEntry.Column column : beforeColumnsList) {
            String columnName = column.getName();
            String columnValue = column.getValue();
            System.out.println("列名称："+columnName+"，列的值："+columnValue);
        }

    }
}
