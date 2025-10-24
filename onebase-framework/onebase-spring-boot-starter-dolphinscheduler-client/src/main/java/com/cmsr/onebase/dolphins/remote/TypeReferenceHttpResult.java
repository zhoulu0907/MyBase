package com.cmsr.onebase.dolphins.remote;

import com.cmsr.onebase.dolphins.util.JacksonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import java.lang.reflect.Type;

public class TypeReferenceHttpResult<T> extends TypeReference<HttpRestResult<T>> {

  protected final Type type;

  public TypeReferenceHttpResult(Class<?>... clazz) {
    type =
        JacksonUtils.getObjectMapper()
            .getTypeFactory()
            .constructParametricType(HttpRestResult.class, clazz);
  }

  @Override
  public Type getType() {
    return type;
  }
}
