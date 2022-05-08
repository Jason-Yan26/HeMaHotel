//package com.example.hemahotel.controller;
//
//import com.aliyun.tea.*;
//import com.aliyun.dysmsapi20170525.*;
//import com.aliyun.dysmsapi20170525.models.*;
//import com.aliyun.teaopenapi.*;
//import com.aliyun.teaopenapi.models.*;
//
//public class Sample {
//
//    /**
//     * 使用AK&SK初始化账号Client
//     * @param accessKeyId
//     * @param accessKeySecret
//     * @return Client
//     * @throws Exception
//     */
//    public static com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
//        Config config = new Config()
//                // 您的AccessKey ID
//                .setAccessKeyId(accessKeyId)
//                // 您的AccessKey Secret
//                .setAccessKeySecret(accessKeySecret);
//        // 访问的域名
//        config.endpoint = "dysmsapi.aliyuncs.com";
//        return new com.aliyun.dysmsapi20170525.Client(config);
//    }
//
//    public static void main(String[] args_) throws Exception {
//        java.util.List<String> args = java.util.Arrays.asList(args_);
//        com.aliyun.dysmsapi20170525.Client client = Sample.createClient("LTAI5tRFYj7CaT8kS64BoasV", "k1knbztYbsbT9rdwRAdkN7AUVBEyGb");
//        SendSmsRequest sendSmsRequest = new SendSmsRequest()
//                .setSignName("阿里云短信测试")
//                .setTemplateCode("SMS_154950909")
//                .setPhoneNumbers("18852078152")
//                .setTemplateParam("{\"code\":\"1234\"}");
//        // 复制代码运行请自行打印 API 的返回值
//        client.sendSms(sendSmsRequest);
//    }
//}
