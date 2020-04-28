package com.changou;

import com.changgou.util.FastDFSClientUtil;
import org.apache.commons.io.IOUtils;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.StorageServer;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author hongchen
 * @date 2020/4/5 16:52
 */
public class FastDFSTest {
    /**
     * 文件下载测试
     *
     * @throws IOException
     */
    @Test
    public void testDownload() throws IOException {
        //调用工具下载
        String group_name = "group1";
        String remote_name = "M00/00/00/wKjThF6JqGGAdU17AAGuCzlBnzQ961.jpg";
        byte[] bytes = FastDFSClientUtil.downloadFile(group_name, remote_name);
        //将字节转成图片写道磁盘
        IOUtils.write(bytes, new FileOutputStream("f:/123.jpg"));
    }

    /**
     * 文件删除测试
     */
    @Test
    public void testDelete() {
        String group_name = "group1";
        String remote_name = "M00/00/00/wKjThF6JqGGAdU17AAGuCzlBnzQ961.jpg";
        FastDFSClientUtil.deleteFile(group_name, remote_name);
    }

    /**
     * 获取附件信息
     */
    @Test
    public void getFileInfo() {
        String group_name = "group1";
        String remote_name = "M00/00/00/wKjThF6JqGGAdU17AAGuCzlBnzQ961.jpg";
        FileInfo fileInfo = FastDFSClientUtil.getFileInfo(group_name, remote_name);
        System.out.println("文件大小：" + fileInfo.getFileSize());
        System.out.println("文件上传日期：" + fileInfo.getCreateTimestamp());
        System.out.println("文件所在服务器的地址：" + fileInfo.getSourceIpAddr());
    }

    /**
     * 获取服务器信息
     */
    @Test
    public void getStorageServerInfo() {
        String groupName = "group1";
        StorageServer storageServerInfo = FastDFSClientUtil.getStorageServerInfo(groupName);
        System.out.println("存储服务器地址:" + storageServerInfo.getInetSocketAddress().getAddress().getHostAddress());
        System.out.println("存储服务器端口:" + storageServerInfo.getInetSocketAddress().getPort());
        System.out.println("存储服务器角标位置:" + storageServerInfo.getStorePathIndex());
    }
}

