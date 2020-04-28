package com.changgou.content.controller;

import com.changgou.file.FastDFSFile;
import com.changgou.util.FastDFSClientUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author hongchen
 * @date 2020/4/5 17:09
 */
@RestController
@CrossOrigin
public class FileController {
    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {
        //封装文件对象
        String name = file.getOriginalFilename();
        byte[] content = file.getBytes();
        String ext = FilenameUtils.getExtension(name);
        String md5="测试";
        String author="tom";
        FastDFSFile fastDFSFile=new FastDFSFile(name,content,ext,md5,author);

        //工具类调用文件上传方法
        String[] uploadResult = FastDFSClientUtil.uploadFile(fastDFSFile);

        //获取文件上传后的完整url
        String url = FastDFSClientUtil.getTrackerUrl();

        //拼接完整附件地址
        url+="/"+uploadResult[0]+"/"+uploadResult[1];
        return url;
    }

}
