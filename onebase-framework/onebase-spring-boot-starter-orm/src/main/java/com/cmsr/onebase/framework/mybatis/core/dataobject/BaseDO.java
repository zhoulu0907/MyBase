package com.cmsr.onebase.framework.mybatis.core.dataobject;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * 基础实体对象
 *
 */
@Data
public class BaseDO implements Serializable {

    public static final String ID = "id";
    public static final String CREATE_TIME = "create_time";
    public static final String UPDATE_TIME = "update_time";
    public static final String CREATOR = "creator";
    public static final String UPDATER = "updater";
    public static final String DELETED = "deleted";
    private static final String LOCK_VERSION = "lock_version";

    /**
     * 创建时间
     * 设置为注入雪花ID
     */
    @Id
    @GeneratedValue(generator = "SnowflakeIdGenerator")
    @Column(name = ID)
    private Long id;
    /**
     * 创建时间
     */
    @Column(name = CREATE_TIME)
    private LocalDateTime createTime;
    /**
     * 最后更新时间
     */
    @Column(name = UPDATE_TIME)
    private LocalDateTime updateTime;
    /**
     * 创建者，目前使用 SysUser 的 id 编号
     *
     */
    @Column(name = CREATOR)
    private Long creator;
    /**
     * 更新者，目前使用 SysUser 的 id 编号
     *
     */
    @Column(name = UPDATER)
    private Long updater;
  
    /**
     * 是否删除
     */
    @Column(name = DELETED)
    private Boolean deleted;

       /**
     * 乐观锁版本号
     */
    @Column(name = LOCK_VERSION)
    private Long lockVersion;
    
    /**
     * 把 creator、createTime、updateTime、updater 都清空，避免前端直接传递 creator 之类的字段，直接就被更新了
     */
    public void clean(){
        this.creator = null;
        this.createTime = null;
        this.updater = null;
        this.updateTime = null;
    }

}
