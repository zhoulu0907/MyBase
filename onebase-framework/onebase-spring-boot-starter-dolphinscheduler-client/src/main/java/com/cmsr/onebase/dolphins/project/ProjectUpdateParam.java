package com.cmsr.onebase.dolphins.project;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProjectUpdateParam {

  private Long projectCode;

  private String projectName;

  private String description;

  private String userName;
}
