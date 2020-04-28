package com.changgou.util;

import com.changgou.file.FastDFSFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * 操作fastDFS的工具类
 *
 * @author hongchen
 * @date 2020/4/5 15:58
 */
public class FastDFSClientUtil {
    /**
     * 初始化FastDFS配置文件
     */
    static {
        String filename = "fdfs_client.conf";
        String config_name = new ClassPathResource(filename).getPath();
        try {
            ClientGlobal.init(config_name);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件按上传
     * @param fastDFSFile
     * @return
     */
    public static String[] uploadFile(FastDFSFile fastDFSFile) {
        byte[] file_buff = fastDFSFile.getContent();//文件信息
        String file_ext_name = fastDFSFile.getExt();//文件扩展名称
        NameValuePair[] meta_list = new NameValuePair[1];//文件备注信息
        meta_list[0] = new NameValuePair(fastDFSFile.getAuthor());
        //1.创建跟踪服务器的客户端
        TrackerClient trackerClient = new TrackerClient();

        try {
            //2.由跟踪服务器的客户端获取到服务器端
            TrackerServer trackerServer = trackerClient.getConnection();

            //3.创建存储服务器客户端
            StorageClient storageClient = new StorageClient(trackerServer, null);

            //4.文件上传操作
            String[] uploadResult = storageClient.upload_file(file_buff, file_ext_name, meta_list);
            //返回的数组：组名+文件所在路径
            return uploadResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取跟踪服务器地址
     * @return
     */
    public static String getTrackerUrl(){
        try {
        //1.创建跟踪服务器的客户端
        TrackerClient trackerClient=new TrackerClient();

        //2.由跟踪服务器的客户端获取到服务器端
            TrackerServer trackerServer = trackerClient.getConnection();

        //3.获取跟踪服务器地址和端口
            String hostAddress = trackerServer.getInetSocketAddress().getAddress().getHostAddress();
            int port = ClientGlobal.getG_tracker_http_port();
            String url="http://"+hostAddress+":"+port;
            return url;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 文件下载
     * @param group_name
     * @param remote_filename
     * @return
     */
    public static byte[] downloadFile(String group_name,String remote_filename){
        //1.创建跟踪服务器的客户端
        TrackerClient trackerClient = new TrackerClient();

        try {
            //2.由跟踪服务器的客户端获取到服务器端
            TrackerServer trackerServer = trackerClient.getConnection();

            //3.创建存储服务器客户端
            StorageClient storageClient = new StorageClient(trackerServer, null);

            //4.文件下载操作
            byte[] bytes = storageClient.download_file(group_name, remote_filename);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件删除
     * @param group_name
     * @param remote_filename
     */
    public static void deleteFile(String group_name,String remote_filename){
        //1.创建跟踪服务器的客户端
        TrackerClient trackerClient = new TrackerClient();

        try {
            //2.由跟踪服务器的客户端获取到服务器端
            TrackerServer trackerServer = trackerClient.getConnection();

            //3.创建存储服务器客户端
            StorageClient storageClient = new StorageClient(trackerServer, null);

            //4.文件删除操作
           storageClient.delete_file(group_name, remote_filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取附件信息
     * @param group_name
     * @param remote_filename
     * @return
     */
    public static FileInfo getFileInfo(String group_name,String remote_filename){
        //1.创建跟踪服务器的客户端
        TrackerClient trackerClient = new TrackerClient();

        try {
            //2.由跟踪服务器的客户端获取到服务器端
            TrackerServer trackerServer = trackerClient.getConnection();

            //3.创建存储服务器客户端
            StorageClient storageClient = new StorageClient(trackerServer, null);

            //4.获取文件信息
            FileInfo file_info = storageClient.get_file_info(group_name, remote_filename);
            return file_info;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取存储服务器信息
     * @param groupName
     * @return
     */
    public static StorageServer getStorageServerInfo(String groupName){
        //1.创建跟踪服务器的客户端
        TrackerClient trackerClient = new TrackerClient();

        try {
            //2.由跟踪服务器的客户端获取到服务器端
            TrackerServer trackerServer = trackerClient.getConnection();

            //3.获取存储服务器信息
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer, groupName);

            return storeStorage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
