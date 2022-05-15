package com.example.hemahotel.alipay;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import org.springframework.stereotype.Component;

/**使用修改说明*/
//请将 AlipayConfig.java 中的 appId、alipayPublicKey、notifyUrl 换成自己的配置；
//同时修改 merchantPrivateKey 的路径或者直接粘贴 merchantPrivateKey 的字符串；
//另外, 请将 AlipayService.java 中的重定向url(return url) 修改为自己项目的url；

@Component
public class AlipayConfig {

    // 1. 设置参数（全局只需设置一次）
    static {
        Factory.setOptions(getOptions());
    }

    private static Config getOptions() {
        Config config = new Config();

        config.protocol = "https";

        //真实环境
        config.gatewayHost = "openapi.alipay.com";
        //沙箱环境
        config.gatewayHost = "openapi.alipaydev.com";

        config.signType = "RSA2";

        //支付宝开放平台中的APPID
        config.appId = "2021000117690160";

        //注:填支付宝公钥,不是生成的应用公钥
        config.alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArSlzV7QZKVTMLWdQdfydC4Uaw6YIIUOvts4CSnGwXw3SC+G2id1WX1iK760BbFsKmdxtV31az2UdGjdOKPjE0VxX04a1cAo3a8j7GZhni925yY2uXvKSoRnwVGNRLAd0Naw+m+STU0CAhovxlFuvab0gpnaglJzpg62Z+ROlbKws2yMH1ILz4AV5EubrOQat46K4xkAOE7aX4/jxoOQtfdhx2moW9KUN8rneYogRg6GWSKvySTHCjZUITYqEuTgDepggd3E4tQOyqM/Ftamdhf8dtZ6CJNqBUrM9L29Nw8Amx4Jq5xL/n8WfQHjoxsS1WIEUUZMP8aJTlEeZtbYQgQIDAQAB";

        //注:填应用私钥,为避免私钥随源码泄露，推荐从文件中读取私钥字符串而不是写入源码中
        config.merchantPrivateKey = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDrLS7NCKJL89EMct37z8h7xhycAprK0XkR51zYtqZ27rRaxFrULKgBDNwFm4ZcCxNvDJfgdGseK8e7g58flq83m9eMIuaGtv6dFv69h5SAqX6h7rZgWtyHG6mivl+oMdldqxwWnrS9xnJPZqhh2fFO1An3yNLTmQtYPzQM9rAZtZA7jmXoc5stm8WYv4bW6egFRLgTDFrMBeNnPqedCLNnt0H48oUW1mGA62rmEFq2+1A1alTgUfATFZKFRYf0Nq9skigb+uwo3DTbikqp+JudDS10KTipg6AUlrxnPWGnkxVBLhFlUNP6e15OtlUH4djGYFqQ8MSOpU5BJM1jbml9AgMBAAECggEBAJZ+GV7XUZQs9+9xaiADvRWHlzM2b3uuRD1ywnVjnG6rHfqfgEgaM4BIjFuCRJ6N7+e2NVJK7WknNu8FsUPzlziMn4hrpP08CP7ZgjYilqMm04QoT8ilHE3RI+Ki1G+7Ro+sOZM4CA0Q+oFY/hNv9f0trxtDBbamTx4vg81v5wzrarGCg8gQUNHURt0Pl1cEI4Dpz3jGghHGhzimvmv2jipDCz1KaDtkKoztHHIPfovhBENuZxMGqo3quEkUJKRBnUfpzr7oEAVkzjDtRKhZDw8ghlAiuxUDgXVKUDuY/uMdBMRSPuRrEnum/u6jIdlLJJFoBbrUtRlW6HypbGA89nkCgYEA/H7lZGWTDDxr2v/VTHBl7t0kW4MbFrMVcSj9uB2+ym5fLKmEDP37ALU3KMEkm6UO0EB5DbV6m5QtdR9B778luGt5/HKKtrEkNrmdBgiiFoGMlXOKxI4yp4F9+7OafE9mfumbtaXTUzmoAntsdM4JFnOy1Htyc/TT4QBLrz3lSAsCgYEA7nDAqHib9zOq/40YJ2/bs0sVYj9zskoixpvhfsJ3Uyu1flaDvT9MkD5U8DGRurpE/HTSi86YZdcndqfzFALgJVnCEnvMSrnY3ErN3tBOSGfYN7Kf7F/KSLGxTQO3cJgJkvO/tJ8v0JSGL2m+51EGTDCvjj8JSNxBtZp+4//moZcCgYEA/EG+5f3A2nGnA6WisPp+LBUNlMu4DYafPm3IheBZ055PQayd++jmkv94+yNzcniGAzLWfga4VqTEgYXX9zEN+2CM/9Rjl0AEwRPRt8x6O2f0Tv+fPN4f50+s0QHPrrAY42R6pxDjvvsUSl6KrWuGksLGgBhQ8V5BLEKCWrLI1Z8CgYAwnFoavqQFbMoy2WAQQjTHQ8pzQj5jHcwjM4pwXlMXaLTSse6fuXTxf1OvuFtedzUzRtnZ2N+m+4s3HJxo+wEZfEgdnpGStnroxnNcNpmPF2S0Tj1eYlUKJvffnJ9cXBKvm/P8bevAmVcV+XgLDrC6/b38wQsyiHz4+itebkBltwKBgQDhtE+en9Zh2Fk4Zt0qVhERujhIWowByrjzSbWg+LTA4vsJJYXle3cw9xIELpsGeaDJz7cLsPPAdAzHBbz64NvAA3qzVWtr893i+iQbxz+UWUafwbsbqHg6rRkmwR+RsljonLEL3hoqmHpduyOtyS6wTtwzvn5shGBRaBEZi7zFJg==";

        //可设置异步通知接收服务地址（可选）
        config.notifyUrl = "http://81.69.175.215:8080/api/alipay/callback";

        return config;
    }
}

