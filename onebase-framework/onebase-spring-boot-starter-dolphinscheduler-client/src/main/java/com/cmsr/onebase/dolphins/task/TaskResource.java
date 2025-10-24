package com.cmsr.onebase.dolphins.task;

import com.cmsr.onebase.dolphins.remote.RequestHttpEntity;
import com.cmsr.onebase.dolphins.util.JacksonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * used by shell,python,spark,flink.... task
 *
 * <p>used when define task
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResource {

  //  private Long id;

  /** 资源名称，如 onebase-ds/default/resources/JavaProject.Flink-1.0.0-SNAPSHOT.jar */
  private String resourceName;

  /**
   * must rewrite,then {@link RequestHttpEntity#bodyToMap()} can transfer object to json string
   *
   * @return object json string
   */
  @Override
  public String toString() {
    return JacksonUtils.toJSONString(this);
  }
}
