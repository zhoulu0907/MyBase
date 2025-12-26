package com.cmsr.onebase.module.dashboard.build.model;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author mty
 * @since 2022-12-22
 */
@Data
@Table("dashboard_file")
public class DashboardFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(comment = "主键ID")
    private Long id;

    private String fileName;

    private Integer fileSize;

    private String fileSuffix;

    /**
     * 虚拟路径
     */
    private String virtualKey;

    /**
     * 相对路径
     */
    private String relativePath;

    /**
     * 绝对路径
     */
    private String absolutePath;

    private LocalDateTime createTime;

    /**
     * 文件标识
     */
    private Long fileId;

}
