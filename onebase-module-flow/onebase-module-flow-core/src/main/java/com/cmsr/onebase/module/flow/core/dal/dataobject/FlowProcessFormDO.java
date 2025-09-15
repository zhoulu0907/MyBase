package com.cmsr.onebase.module.flow.core.dal.dataobject;

import java.sql.*;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "flow_process_form")
public class FlowProcessFormDO extends TenantBaseDO {

  @Column(name = "process_id", length = 19, nullable = false)
  private Long processId;

  @Column(name = "page_id", length = 19, nullable = false)
  private Long pageId;


}