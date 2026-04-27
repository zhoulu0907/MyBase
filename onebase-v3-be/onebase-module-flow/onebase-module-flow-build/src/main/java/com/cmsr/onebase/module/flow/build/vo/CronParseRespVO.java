package com.cmsr.onebase.module.flow.build.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/10/11 17:56
 */
@Data
public class CronParseRespVO {

    private boolean valid;

    private List<String> next;

}
