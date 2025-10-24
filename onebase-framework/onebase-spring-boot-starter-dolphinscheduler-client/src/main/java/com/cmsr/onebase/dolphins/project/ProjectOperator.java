package com.cmsr.onebase.dolphins.project;

import com.cmsr.onebase.dolphins.common.PageInfo;
import com.cmsr.onebase.dolphins.core.AbstractOperator;
import com.cmsr.onebase.dolphins.core.DolphinClientConstant;
import com.cmsr.onebase.dolphins.core.DolphinException;
import com.cmsr.onebase.dolphins.remote.DolphinsRestTemplate;
import com.cmsr.onebase.dolphins.remote.HttpRestResult;
import com.cmsr.onebase.dolphins.remote.Query;
import com.cmsr.onebase.dolphins.util.JacksonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/** operator for operate project */
@Slf4j
public class ProjectOperator extends AbstractOperator {

  public ProjectOperator(
      String dolphinAddress, String token, DolphinsRestTemplate dolphinsRestTemplate) {
    super(dolphinAddress, token, dolphinsRestTemplate);
  }

  /**
   * create project, api:/dolphinscheduler/projects
   *
   * @param projectCreateParam create project param
   * @return project info
   */
  public ProjectInfoResp create(ProjectCreateParam projectCreateParam) {
    String url = dolphinAddress + "/projects";
    try {
      HttpRestResult<ProjectInfoResp> result =
          dolphinsRestTemplate.postForm(
              url, getHeader(), projectCreateParam, ProjectInfoResp.class);
      if (result.getSuccess()) {
        return result.getData();
      } else {
        log.error("create project response:{}", result);
        throw new DolphinException("create dolphin scheduler project fail");
      }
    } catch (Exception e) {
      throw new DolphinException("create dolphin scheduler project fail", e);
    }
  }

  /**
   * update project, api：/dolphinscheduler/projects/{code}
   *
   * @param projectUpdateParam update project param
   * @return true for success,otherwise false
   */
  public ProjectInfoResp update(ProjectUpdateParam projectUpdateParam) {
    String url = dolphinAddress + "/projects/" + projectUpdateParam.getProjectCode();
    try {
      HttpRestResult<ProjectInfoResp> result =
          dolphinsRestTemplate.putForm(url, getHeader(), projectUpdateParam, ProjectInfoResp.class);
      if (result.getSuccess()) {
        return result.getData();
      } else {
        log.error("update project response:{}", result);
        throw new DolphinException("update dolphin scheduler project fail");
      }
    } catch (Exception e) {
      throw new DolphinException("update dolphin scheduler project fail", e);
    }
  }

  /**
   * delete dolphin scheduler project
   *
   * @param projectCode dolphin scheduler project code
   * @return true for success,otherwise false
   */
  public Boolean delete(Long projectCode) {
    String url = dolphinAddress + "/projects/" + projectCode;
    try {
      HttpRestResult<String> result =
          dolphinsRestTemplate.delete(url, getHeader(), null, String.class);
      log.info("delete project response:{}", result);
      return result.getSuccess();
    } catch (Exception e) {
      throw new DolphinException("delete dolphin scheduler project fail", e);
    }
  }

  /**
   * page query project list ，api:/dolphinscheduler/projects
   *
   * @param page page number
   * @param size page size
   * @param projectName project's name query criteria
   * @return {@link List<ProjectInfoResp>}
   */
  public List<ProjectInfoResp> page(Integer page, Integer size, String projectName) {

    page = Optional.ofNullable(page).orElse(DolphinClientConstant.Page.DEFAULT_PAGE);
    size = Optional.ofNullable(size).orElse(DolphinClientConstant.Page.DEFAULT_SIZE);

    String url = dolphinAddress + "/projects";
    Query query =
        new Query()
            .addParam("pageNo", String.valueOf(page))
            .addParam("pageSize", String.valueOf(size))
            .addParam("searchVal", projectName)
            .build();
    try {
      HttpRestResult<JsonNode> stringHttpRestResult =
          dolphinsRestTemplate.get(url, getHeader(), query, JsonNode.class);
      return JacksonUtils.parseObject(
              stringHttpRestResult.getData().toString(),
              new TypeReference<PageInfo<ProjectInfoResp>>() {})
          .getTotalList();
    } catch (Exception e) {
      throw new DolphinException("list dolphin scheduler project fail", e);
    }
  }
}
