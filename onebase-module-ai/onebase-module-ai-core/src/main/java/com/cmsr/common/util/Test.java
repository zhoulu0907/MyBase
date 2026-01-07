package com.cmsr.common.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Test {

    public void testUpload() throws FileNotFoundException {

        String endpoint = "oss-cn-shanghai.aliyuncs.com";

        String accessKeyId = "LTAI5tNqcnnGfoJrzzxq5U3b";

        String accessKeySecret = "LQutAYMXzRVu9TA6ShWbsfSBgkxgVl";

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        InputStream inputStream = new FileInputStream("C:\\Users\\Administrator\\Desktop\\image\\pf.jpg");

        ossClient.putObject("gulimall-gsp","bug.jpg",inputStream);

        ossClient.shutdown();

        System.out.println("上传成功...");
    }

    public static void main(String[] args) throws FileNotFoundException {

        String endpoint = "http://oss-cn-shanghai.aliyuncs.com";

        String accessKeyId = "LTAI5tNqcnnGfoJrzzxq5U3b";

        String accessKeySecret = "LQutAYMXzRVu9TA6ShWbsfSBgkxgVl";

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        InputStream inputStream = new FileInputStream("C:\\Users\\Administrator\\Desktop\\image\\pf.jpg");

        ossClient.putObject("gulimall-gsp","pf.jpg",inputStream);

        ossClient.shutdown();

        System.out.println("上传成功...");
    }
}
