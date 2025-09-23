package com.cmsr.onebase.module.metadata.api.datamethod.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;

import jakarta.validation.Valid;
@Service
public class DataMethodApiImpl implements DataMethodApi {
    //todo 本接口主要是通过传入的条件，和条件对应的值，拼接出查询的 sql 语句，并排序，然后将查询到的数据组装成对象返回，业务实体字段转换成为Jdbc类型主要使用JdbcTypeConvertor，然后条件枚举都定义在onebase-spring-boot-starter-express中
    @Override
    public List<EntityFieldDataRespDTO> getDataByCondition(@Valid EntityFieldDataReqDTO reqDTO) {
        // TODO Auto-generated method stub




        throw new UnsupportedOperationException("Unimplemented method 'getDataByCondition'");
    }
}
