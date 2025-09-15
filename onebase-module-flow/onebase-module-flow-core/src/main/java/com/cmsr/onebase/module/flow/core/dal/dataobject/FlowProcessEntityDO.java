package com.cmsr.onebase.module.flow.core.dal.dataobject;

import java.sql.*;

import com.cmsr.onebase.framework.tenant.core.db.TenantBaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "flow_process_entity")
public class FlowProcessEntityDO extends TenantBaseDO {


  @Column(name = "process_id", length = 19, nullable = false)
  private Long processId;

  @Column(name = "entity_id", length = 19, nullable = false)
  private Long entityId;


}