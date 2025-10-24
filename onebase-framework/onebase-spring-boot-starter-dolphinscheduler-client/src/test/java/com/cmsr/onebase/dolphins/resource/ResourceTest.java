package com.cmsr.onebase.dolphins.resource;

import com.cmsr.onebase.dolphins.BaseTest;
import com.cmsr.onebase.dolphins.core.DolphinClientConstant;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResourceTest extends BaseTest {

  private final String fileName = "dophinsdk-create2";
  private final String suffix = "sh";
  private final String fullName =
      "file:/home/"
          + tenantCode
          + "/ds/upload/"
          + tenantCode
          + "/resources/"
          + fileName
          + "."
          + suffix;

  @Test
  public void testPage() {
    List<ResourceQueryRes> list =
        getClient()
            .opsForResource()
            .page(null, null, DolphinClientConstant.Resource.DEFAULT_PID_FILE, "");
    list.forEach(System.out::println);
  }

  @Test
  public void testOnlineCreate() {
    ResourceCreateParam resourceCreateParam = new ResourceCreateParam();
    resourceCreateParam
        .setSuffix(suffix)
        .setFileName(fileName)
        .setContent("created by dolphin scheduler java sdk");
    Assertions.assertTrue(getClient().opsForResource().onlineCreate(resourceCreateParam));
  }

  @Test
  public void testOnlineUpdate() {
    ResourceUpdateParam resourceUpdateParam = new ResourceUpdateParam();
    resourceUpdateParam
        .setTenantCode(tenantCode)
        .setFullName(fullName)
        .setContent("update by dolphin scheduler java sdk");
    Assertions.assertTrue(getClient().opsForResource().onlineUpdate(resourceUpdateParam));
  }

  @Test
  public void testUploadFile() {
    ResourceUploadParam resourceUploadParam = new ResourceUploadParam();
    resourceUploadParam
        .setName("test_upload.txt")
        .setDescription("upload by dolphin scheduler java sdk")
        .setFile(new File("/home/chen/Documents/test_upload.txt"));
    Assertions.assertTrue(getClient().opsForResource().upload(resourceUploadParam));
  }

  @Test
  public void delete() {
    Assertions.assertTrue(getClient().opsForResource().delete(tenantCode, fullName));
  }
}
