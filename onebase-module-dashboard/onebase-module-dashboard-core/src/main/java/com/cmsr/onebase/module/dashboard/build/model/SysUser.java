package com.cmsr.onebase.module.dashboard.build.model;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author fc
 * @since 2023-04-30
 */
@Table("dashboard_user")
@Data
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Generator, value = "uuid")
    private String id;

    private String username;

    private String password;

    private String nickname;

    private Integer depId;

    private String posId;


}
