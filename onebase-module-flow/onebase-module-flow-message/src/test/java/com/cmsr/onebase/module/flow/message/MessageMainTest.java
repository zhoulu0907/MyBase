package com.cmsr.onebase.module.flow.message;

/**
 * @Author：huangjie
 * @Date：2025/10/14 10:48
 */
public class MessageMainTest {

    public static void main(String[] args) {
        args = new String[]{"-endpoints", "10.0.104.38:8081", "-processId", "123456", "-msgTag", "norm"};
        MessageMain.main(args);
    }

}