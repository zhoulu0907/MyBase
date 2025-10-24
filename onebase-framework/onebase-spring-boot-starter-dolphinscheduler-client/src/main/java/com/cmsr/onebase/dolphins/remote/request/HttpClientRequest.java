package com.cmsr.onebase.dolphins.remote.request;

import com.cmsr.onebase.dolphins.remote.RequestHttpEntity;
import com.cmsr.onebase.dolphins.remote.response.HttpClientResponse;
import java.io.Closeable;
import java.net.URI;

public interface HttpClientRequest extends Closeable {

  HttpClientResponse execute(URI uri, String httpMethod, RequestHttpEntity requestHttpEntity)
      throws Exception;
}
