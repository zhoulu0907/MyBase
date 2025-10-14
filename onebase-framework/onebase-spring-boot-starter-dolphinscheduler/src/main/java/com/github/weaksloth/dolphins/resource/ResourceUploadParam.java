package com.github.weaksloth.dolphins.resource;

import com.github.weaksloth.dolphins.core.DolphinClientConstant;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;

@Data
@Accessors(chain = true)
public class ResourceUploadParam {

  private String type = DolphinClientConstant.Resource.TYPE_FILE;

  private String currentDir = DolphinClientConstant.Resource.DEFAULT_CURRENT_DIR;

  private String description;

  private File file;

  private String name;
}
