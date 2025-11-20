package com.cmsr.onebase.module.etl.core.dal.database;

import com.cmsr.onebase.framework.aynline.DataRepository;
import com.cmsr.onebase.module.etl.core.dal.dataobject.ETLFlinkFunctionDO;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.param.ConfigStore;
import org.anyline.data.param.init.DefaultConfigStore;
import org.anyline.entity.Compare;
import org.anyline.entity.DataSet;
import org.anyline.entity.Order;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/18 16:29
 */
@Slf4j
@Repository
public class ETLFlinkFunctionRepository extends DataRepository<ETLFlinkFunctionDO> {

    public ETLFlinkFunctionRepository() {
        super(ETLFlinkFunctionDO.class);
    }

    public List<String> listFunctionTypes() {
        ConfigStore cs = new DefaultConfigStore();
        cs.columns("function_type").distinct(true);
        cs.order("function_type", Order.TYPE.ASC);
        DataSet dataSet = anylineService.querys("etl_flink_function", cs);
        return dataSet.stream().map(row -> row.getString("function_type")).toList();
    }

    public List<ETLFlinkFunctionDO> findFunctionsByKey(String type, String key) {
        ConfigStore cs = new DefaultConfigStore();
        if (type != null) {
            cs.eq("function_type", type);
        }
        if (key != null) {
            cs.like("function_name", key).or(Compare.LIKE, "function_desc", key);
        }
        cs.order("function_name", Order.TYPE.DESC);
        return this.findAllByConfig(cs);
    }


}
