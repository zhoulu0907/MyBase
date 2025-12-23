package com.cmsr.onebase.module.dashboard.build.common.domain;

import lombok.Data;

/**
 * boostrap table post 参数
 * @author fc
 *
 */
@Data
public class Tablepar {

	private int page;//页码

	private int limit;//数量

	private String orderByColumn;//排序字段

	private String isAsc;//排序字符 asc desc

	private String searchText;//列表table里面的搜索

	private Integer appId; //应用id

}
