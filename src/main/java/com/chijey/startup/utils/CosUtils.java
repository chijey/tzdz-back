package com.chijey.startup.utils;


import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;

import java.io.File;

public class CosUtils {
    public static COSCredentials credentials = new BasicCOSCredentials("AKIDd3KBm7fc1A2TVHvoa8PPN7cqmUxdCGFy","bw2ScWFjxuZAWZQ6lMioaSItPUcV8w6g");
    public static ClientConfig clientConfig = new ClientConfig(new Region("ap-chongqing"));
    public static COSClient cosClient = new COSClient(credentials,clientConfig);
    public static String BUCKET_NAME = "tzdz-1304527316";

    public static PutObjectResult uploadFile(String key, File file){
        return  cosClient.putObject(BUCKET_NAME,key,file);
    }

}
