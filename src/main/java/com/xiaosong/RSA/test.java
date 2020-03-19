package com.xiaosong.RSA;

import com.xiaosong.model.VKey;
import org.apache.commons.codec.binary.Base64;

/**
 * @program: newInnerVisitor
 * @description:
 * @author: cwf
 * @create: 2020-01-20 11:16
 **/
public class test {
    public static void main(String[] args) throws Exception {
        VKey vKey = RSAEncrypt.genKeyPair();
        System.out.println(vKey.getPrivateKey());
        System.out.println(vKey.getPublicKey());
        String plainText="123";
        //私钥加密
        byte[] encrypt = RSAEncrypt.encrypt(RSAEncrypt.loadPrivateKeyByStr("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC3A4AId1RabvMyJZGaoWzzM3Q3Sr7mYVZBplGpbmtldVXkNyUK9sMkKK/OTNoxNgnk70XBi6fiMCzKpRntW+nEN2FoYIrxxWvXp/57eLIwyAowf9QoQPGkzXN7ha5ems2unczpMHCZJKDd0IRYjnh6hoVTobmjMvv9vIaDV5iCD0Q79G+/x4Ct2/vlth1AEJhajhdk8Js0cn5vZJzsMUhCNfwzBYcn5JRBeq4bZUptCJPIWGFFg2g1S1s7kL/iGVkCtpTBmmsj39GdiDuzlaH9s+HY4qDogBF4HkrVNWuBmRV50s3GBit/Txkj/kSW+kn4q1FcMa+ZkIgq2tCaSXULAgMBAAECggEAdRcuMTx3oaxeuHTE5ZPmKut+rWmLHcak8zfAA5D78VvS6kJF1u/uBhJp+Km+58FVLz3O+Xeqn9KGtdhP8EbgF4P/NFKgUvMmfkGjVcfw6bjym/kM+fwjHne8LQloeGq+sbuQQJmXUMPYkFI6epYcivfKEanlJX8XLk4SGbJPhtW7uCEQmLsFMLg/aJDdD91Hjta3cTK5KBjIzqNZUEWTqIWHGFnD4OHoDkvpirwZ7NGCJD0P+a+ecQzdlqfNUikQDinc8ZqhuE26zlht5XDO7QW6gM2BrIbiJb47ap8lsjACVBPICsBjHl82FqVpdGGiK36pt/Qu0LldltmZulVhEQKBgQDrS908zl/AGXcD5TdYCsGQIwPZkV32WWXGMOy5QbYWark8uZDvTEUamVoYsml+PBc/JSSE7Qi4pUpmn9ifUV9x6Vrg0M7BuxAjYXHA08PZ8zOltPZU+x7fSr+bbXuy4X68CG5NMiWOBALNfj/4XpmdD/XYJLXbretkFf6QN1O0PQKBgQDHHfMfsBFGkhcJ2WT7vMLnd3+nK3chfJAKqI4WwiPvwZLmliIoEFahxKCm2vxttdxreSpiQdfEpYLf81LNSaglmUqjOixKSAdObZhddY55HHvExre8/9OrHAQWAe69GT+TuYVyQ2fKMWumbHDkhvKaiXvX2+1x4uzqDee2xVw65wKBgHb/+lc7ZI+qEVgmABSF9BkqrFLHw0bJNcDDk6WqByZVOXPtwdBUuYpXToq5CMNwLq2f4lVb8BI6fmNiqlMA+42H4I9c64RtUB8ktDw6nY3IAzHmUMLv8lAp5Lf96haxFsCQXwCpwPaPLr+w/zdk2LnnjYWQI22EC02mFgXoBWxdAoGBAISrtPnxI6Gk54rdegat4CPAIKMvN+JtD8C6TGpnkvBGR24yrLGLKV4CGJT0pmsnWKWXhyEPBAh3y3sSsqIsKMhcmjmmIiqgUZX1OidTk8moq+MSEQzyXIPPV/2d7ge7UfxJCdx3gylawoKSEKqELBPB9W1/kXdnnznYaAME/44TAoGBAJR1/NI8WfQsdhImurXTek7VlUjRrQ4yKLWTS9ChwKJvuPHdjoLithQX7AeLdKbZAUxptA7kXvHE0HA5aOuS8S7xtz3MgJvUsYY4p725Ak7UlyYKlEMKErU+1aJiQGP5fmVH6kTSB9iiTGlcr+m0LgBkBk8JtKOWhSFnCZyo0S7G"), plainText.getBytes());
        String cipher= Base64.encodeBase64String(encrypt);
        RSAEncrypt.loadPublicKeyByStr("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtwOACHdUWm7zMiWRmqFs8zN0N0q+5mFWQaZRqW5rZXVV5DclCvbDJCivzkzaMTYJ5O9FwYun4jAsyqUZ7VvpxDdhaGCK8cVr16f+e3iyMMgKMH/UKEDxpM1ze4WuXprNrp3M6TBwmSSg3dCEWI54eoaFU6G5ozL7/byGg1eYgg9EO/Rvv8eArdv75bYdQBCYWo4XZPCbNHJ+b2Sc7DFIQjX8MwWHJ+SUQXquG2VKbQiTyFhhRYNoNUtbO5C/4hlZAraUwZprI9/RnYg7s5Wh/bPh2OKg6IAReB5K1TVrgZkVedLNxgYrf08ZI/5ElvpJ+KtRXDGvmZCIKtrQmkl1CwIDAQAB");
        byte[] res= RSAEncrypt.decrypt(RSAEncrypt.loadPublicKeyByStr("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtwOACHdUWm7zMiWRmqFs8zN0N0q+5mFWQaZRqW5rZXVV5DclCvbDJCivzkzaMTYJ5O9FwYun4jAsyqUZ7VvpxDdhaGCK8cVr16f+e3iyMMgKMH/UKEDxpM1ze4WuXprNrp3M6TBwmSSg3dCEWI54eoaFU6G5ozL7/byGg1eYgg9EO/Rvv8eArdv75bYdQBCYWo4XZPCbNHJ+b2Sc7DFIQjX8MwWHJ+SUQXquG2VKbQiTyFhhRYNoNUtbO5C/4hlZAraUwZprI9/RnYg7s5Wh/bPh2OKg6IAReB5K1TVrgZkVedLNxgYrf08ZI/5ElvpJ+KtRXDGvmZCIKtrQmkl1CwIDAQAB"), Base64.decodeBase64(cipher));
        byte[] res1= RSAEncrypt.decrypt(RSAEncrypt.loadPublicKeyByStr("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtwOACHdUWm7zMiWRmqFs8zN0N0q+5mFWQaZRqW5rZXVV5DclCvbDJCivzkzaMTYJ5O9FwYun4jAsyqUZ7VvpxDdhaGCK8cVr16f+e3iyMMgKMH/UKEDxpM1ze4WuXprNrp3M6TBwmSSg3dCEWI54eoaFU6G5ozL7/byGg1eYgg9EO/Rvv8eArdv75bYdQBCYWo4XZPCbNHJ+b2Sc7DFIQjX8MwWHJ+SUQXquG2VKbQiTyFhhRYNoNUtbO5C/4hlZAraUwZprI9/RnYg7s5Wh/bPh2OKg6IAReB5K1TVrgZkVedLNxgYrf08ZI/5ElvpJ+KtRXDGvmZCIKtrQmkl1CwIDAQAB"), Base64.decodeBase64("emRo6E/wXvXGFaUOH4tpIwb/C/g8c3JjKJGwIrB4KEiOFsTOwTnnPQ45dolBrHBKJGNHxMzLgTU34tJpcO+ECLCVtcUqswVmUJUR/DEfrXAqbckvRU5OAzQqeYJhzrvU/sjnsOTboz4oL1Ww8oqto782GD31itMW3F6bf5O7BCUwJNwzs5WHpiLVoDWflUOTE2gHFApM4RPssrpQ4DafhDHfyiGzAxZ4R7IGSgdYqKUMVrWtMtzOhemNJvbdQa6SDA7Zhi0BUgbz6S+kAU8mZkzHC2QZMka2gH7quI4wRADPxphfQDnwl0n4EnqvOV/6qW7rZcg6Mg3pL1Jd+ve2eA=="));
        String restr=new String(res);
        String restr1=new String(res1);
        System.out.println("原文："+plainText);
        System.out.println("加密："+cipher);
        System.out.println("解密："+restr);
        System.out.println("解密："+restr1);
    }
}
