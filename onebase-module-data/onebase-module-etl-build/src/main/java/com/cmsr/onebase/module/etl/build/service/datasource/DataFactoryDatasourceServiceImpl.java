package com.cmsr.onebase.module.etl.build.service.datasource;

import com.cmsr.onebase.module.etl.build.service.datasource.vo.DatabaseTypeVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.anyline.metadata.type.DatabaseType;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DataFactoryDatasourceServiceImpl implements DataFactoryDatasourceService {

    @Override
    public List<DatabaseTypeVO> getSupportedDatabaseTypes() {
        List<DatabaseTypeVO> supportedDbs = Lists.newArrayList();
        for (DatabaseType db : DatabaseType.values()) {
            // 跳过非常规类型
            if (db == DatabaseType.NONE || db == DatabaseType.COMMON) {
                continue;
            }
            if (StringUtils.isBlank(db.driver())) {
                continue;
            }
            String driverName = db.driver();
            try {
                ClassUtils.getClass(driverName);
                DatabaseTypeVO typeVO = new DatabaseTypeVO();
                typeVO.setDatasourceType(db.name());
                typeVO.setDisplayName(db.title());

                supportedDbs.add(typeVO);
            } catch (ClassNotFoundException ex) {
                // do nothing, just pass
            }
        }

        return supportedDbs;
    }

//    public void test() {
//        BeanUtils.toBean()
//    }
}
