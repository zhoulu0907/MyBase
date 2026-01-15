package com.cmsr.onebase.module.dashboard.build.model.vo;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DashboardProjectVO implements Serializable {
    private static final long serialVersionUID = 1L;


	@ApiModelProperty(value = "主键")
	private String id;

	@ApiModelProperty(value = "项目名称")
	private String projectName;

	@ApiModelProperty(value = "项目状态[-1未发布,1发布]")
	private Integer state;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@ApiModelProperty(value = "创建时间")
	private Date createTime;

	@ApiModelProperty(value = "创建人id")
	private String createUserId;

	@ApiModelProperty(value = "删除状态[1删除,-1未删除]")
	private Integer isDelete;

	@ApiModelProperty(value = "首页图片")
	private String indexImage;

	@ApiModelProperty(value = "项目介绍")
	private String remarks;

	private String content;

	public String dateToStringConvert(Date date) {
		if(date!=null) {
			return DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
		}
		return "";
	}
}
