package com.xiaosong.test;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @program: innerVisitor
 * @description: 测试
 * @author: cwf
 * @create: 2020-04-15 14:47
 **/
public class fileTest {
    public static void test() throws IOException {
        File file=new File("D:\\access1.txt");
        FileUtils.moveFile(file,new File("D:\\英雄时刻\\access1.txt"));
    }

    public static void main(String[] args) throws IOException {
        test();
    }
}
