package com.cmsr.onebase.module.etl.build.service.mgt;

import com.cmsr.onebase.framework.common.util.object.BeanUtils;
import com.cmsr.onebase.module.etl.build.vo.mgt.FlinkFunctionVO;
import com.cmsr.onebase.module.etl.core.dal.database.ETLFlinkFunctionRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLFlinkFunctionDO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/18 16:40
 */
@Setter
@Slf4j
@Service
public class ETLFlinkFunctionServiceImpl implements ETLFlinkFunctionService {

    @Autowired
    private ETLFlinkFunctionRepository flinkFunctionRepository;

    @Override
    public List<String> listFlinkFunctionTypes() {
        return flinkFunctionRepository.listFunctionTypes();
    }

    @Override
    public List<FlinkFunctionVO> listFlinkFunctions(String type, String key) {
        List<ETLFlinkFunctionDO> dos = flinkFunctionRepository.findFunctionsByKey(type, key);
        return BeanUtils.toBean(dos, FlinkFunctionVO.class);
    }

}
