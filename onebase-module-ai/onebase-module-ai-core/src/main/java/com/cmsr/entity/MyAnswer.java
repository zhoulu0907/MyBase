package com.cmsr.entity;

import lombok.Data;

@Data
public class MyAnswer {
    //意图:1.数据集列表；2.详情；3.图表构建；4.对话式生成大屏;5.图片生成大屏
    private String intention;
    //意图推断理由
    private String reason;
    //可靠度评分从0~10分
    private String reliability;
    //普通对话回答
    private String answer;
}
