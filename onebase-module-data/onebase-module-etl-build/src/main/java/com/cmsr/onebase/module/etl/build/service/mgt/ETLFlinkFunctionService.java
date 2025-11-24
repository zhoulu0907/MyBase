package com.cmsr.onebase.module.etl.build.service.mgt;

import com.cmsr.onebase.module.etl.build.vo.mgt.FlinkFunctionVO;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/18 16:33
 */
public interface ETLFlinkFunctionService {

    List<String> listFlinkFunctionTypes();

    List<FlinkFunctionVO> listFlinkFunctions(String type, String key);
}
