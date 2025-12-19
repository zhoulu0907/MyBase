package com.cmsr.common;

public class AiConstant {

    public enum AiEnum {
        AI_DATATREE(1,"数据集列表"),
        AI_DATASET_DETAIL(2,"数据集详情"),
        AI_CHART_BUILD(3,"图表构建"),
        AI_CHAT_BUILD_SCREEN(4,"对话式生成大屏"),
        AI_PIC_BUILD(5,"图片生成大屏"),
        AI_SIMPLE_CHAT(6,"普通对话");
        private int code;
        private String msg;

        AiEnum(int code,String msg) {
            this.code = code;
            this.msg = msg;
        }
        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
    }
}
