package com.cmsr.onebase.module.metadata.api.entity;

/**
 * 已移除：build 模块不再实现 API 接口，避免 api/build 互相依赖。
 * 保留占位类以防止包路径变更导致的扫描异常，但不注入到 Spring 容器。
 */
public class MetadataEntityFieldApiImplRemoved {}
