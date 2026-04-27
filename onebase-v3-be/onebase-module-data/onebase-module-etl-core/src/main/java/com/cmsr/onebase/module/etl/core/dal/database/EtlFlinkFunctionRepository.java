package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.module.etl.core.dal.dataobject.EtlFlinkFunctionDO;
import com.cmsr.onebase.module.etl.core.dal.mapper.EtlFlinkFuntionMapper;
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
public class EtlFlinkFunctionRepository extends ServiceImpl<EtlFlinkFuntionMapper, EtlFlinkFunctionDO> {

    public List<String> listFunctionTypes() {
        QueryWrapper queryWrapper = query().select(EtlFlinkFunctionDO::getFunctionType)
                .groupBy(EtlFlinkFunctionDO::getFunctionType)
                .orderBy(EtlFlinkFunctionDO::getFunctionType, true);
        return objListAs(queryWrapper, String.class);
    }

    public List<EtlFlinkFunctionDO> findFunctionsByKey(String type, String key) {
        QueryWrapper queryWrapper = query()
                .or(EtlFlinkFunctionDO::getFunctionType).eq(type, StringUtils::isNotBlank)
                .or(EtlFlinkFunctionDO::getFunctionName).like(key, StringUtils::isNotBlank)
                .or(EtlFlinkFunctionDO::getFunctionDesc).like(key, StringUtils::isNotBlank)
                .orderBy(EtlFlinkFunctionDO::getFunctionName, true);

        return list(queryWrapper);
    }
}
