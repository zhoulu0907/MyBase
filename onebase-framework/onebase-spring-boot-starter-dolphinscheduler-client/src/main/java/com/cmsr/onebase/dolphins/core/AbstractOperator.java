package com.cmsr.onebase.dolphins.core;

import com.cmsr.onebase.dolphins.remote.DolphinsRestTemplate;
import com.cmsr.onebase.dolphins.remote.Header;

public abstract class AbstractOperator {

  protected final String dolphinAddress;

  private final String token;

  protected final DolphinsRestTemplate dolphinsRestTemplate;

  public AbstractOperator(
      String dolphinAddress, String token, DolphinsRestTemplate dolphinsRestTemplate) {
    this.dolphinAddress = dolphinAddress;
    this.token = token;
    this.dolphinsRestTemplate = dolphinsRestTemplate;
  }

  /**
   * get header for dolphin scheduler
   *
   * @return
   */
  protected Header getHeader() {
    Header header = Header.newInstance();
    header.addParam("token", this.token);
    return header;
  }
}
