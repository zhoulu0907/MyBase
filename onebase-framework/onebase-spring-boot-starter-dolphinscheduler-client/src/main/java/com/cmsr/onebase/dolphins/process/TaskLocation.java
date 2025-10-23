package com.cmsr.onebase.dolphins.process;

import com.cmsr.onebase.dolphins.remote.RequestHttpEntity;
import com.cmsr.onebase.dolphins.util.JacksonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class TaskLocation {

  private Long taskCode;

  private int x;

  private int y;

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
