package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLFlinkFunctionDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.ETLFlinkFuntionMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/18 16:29
 */
@Slf4j
@Repository
public class ETLFlinkFunctionRepository extends ServiceImpl<ETLFlinkFuntionMapper, ETLFlinkFunctionDO> {

    public List<String> listFunctionTypes() {
        QueryWrapper queryWrapper = query().select(ETLFlinkFunctionDO::getFunctionType)
                .groupBy(ETLFlinkFunctionDO::getFunctionType)
                .orderBy(ETLFlinkFunctionDO::getFunctionType, true);
        return objListAs(queryWrapper, String.class);
    }

    public List<ETLFlinkFunctionDO> findFunctionsByKey(String type, String key) {
        QueryWrapper queryWrapper = query()
                .or(ETLFlinkFunctionDO::getFunctionType).eq(type, StringUtils::isNotBlank)
                .or(ETLFlinkFunctionDO::getFunctionName).like(key, StringUtils::isNotBlank)
                .or(ETLFlinkFunctionDO::getFunctionDesc).like(key, StringUtils::isNotBlank)
                .orderBy(ETLFlinkFunctionDO::getFunctionName, true);

        return list(queryWrapper);
    }
}
