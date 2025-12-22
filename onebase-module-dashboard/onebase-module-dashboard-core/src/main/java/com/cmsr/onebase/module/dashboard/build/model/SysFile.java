package com.cmsr.onebase.module.dashboard.build.model;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author fc
 * @since 2022-12-22
 */
@Table("dashboard_file")
public class SysFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "uuid")
    private String id;

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




    @Column(onInsertValue = "now()")
    private String createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }
    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }


	public String getVirtualKey() {
		return virtualKey;
	}

	public void setVirtualKey(String virtualKey) {
		this.virtualKey = virtualKey;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}





}
