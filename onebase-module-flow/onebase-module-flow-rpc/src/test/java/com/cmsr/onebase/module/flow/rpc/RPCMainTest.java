package com.cmsr.onebase.module.flow.rpc;

import com.cmsr.onebase.module.flow.rpc.RPCMain;

/**
 * @Author：huangjie
 * @Date：2025/10/14 10:48
 */
public class RPCMainTest {

    public static void main(String[] args) {
        args = new String[]{"-jobType", "fld", "-processId", "123456", "-redisAddress", "redis://10.0.104.38:6379"};
        RPCMain.main(args);
    }

}