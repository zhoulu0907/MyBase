package com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.datamethodImpl;

import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodRequestContext;
import com.cmsr.onebase.module.metadata.core.domain.query.MetadataDataMethodSubEntityContext;
import com.cmsr.onebase.module.metadata.core.enums.MetadataDataMethodOpEnum;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.MetadataDataMethodCoreServiceImpl;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.ProcessedSubEntityVo;
import com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo.SubEntityVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MetadataDataMethodSubEntityCrudImpl {

    @Autowired
    private MetadataDataMethodCoreServiceImpl metadataDataMethodCoreServiceImpl;


    /**
     * 子表插入数据行
     */
    public Map<String, Object>  doInsert(ProcessedSubEntityVo processedSubEntityVo){

        MetadataDataMethodRequestContext methodCoreContext = new MetadataDataMethodRequestContext();
        methodCoreContext.setEntityUuid(processedSubEntityVo.getSubEntityId());
        methodCoreContext.setData(processedSubEntityVo.getSubData());
        methodCoreContext.setMethodCode("");
        methodCoreContext.setTraceId(processedSubEntityVo.getTraceId());
        methodCoreContext.setMenuId(processedSubEntityVo.getMenuId());
        methodCoreContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.CREATE);

        return metadataDataMethodCoreServiceImpl.createData(methodCoreContext);
    }

    /**
     * 子表更新数据行
     */
    public Map<String, Object>  doUpdate(ProcessedSubEntityVo processedSubEntityVo){

        MetadataDataMethodRequestContext methodCoreContext = new MetadataDataMethodRequestContext();
        methodCoreContext.setEntityUuid(processedSubEntityVo.getSubEntityId());
        methodCoreContext.setId(processedSubEntityVo.getId());
        methodCoreContext.setData(processedSubEntityVo.getSubData());
        methodCoreContext.setMethodCode("");
        methodCoreContext.setMenuId(processedSubEntityVo.getMenuId());
        methodCoreContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.UPDATE);

        return metadataDataMethodCoreServiceImpl.updateData(methodCoreContext);
    }

    /**
     * 子表删除数据行
     */
    public void doDelete(ProcessedSubEntityVo processedSubEntityVo){

        MetadataDataMethodRequestContext methodCoreContext = new MetadataDataMethodRequestContext();
        methodCoreContext.setEntityUuid(processedSubEntityVo.getSubEntityId());
        methodCoreContext.setId(processedSubEntityVo.getId());
        methodCoreContext.setMethodCode("");
        methodCoreContext.setMenuId(processedSubEntityVo.getMenuId());
        methodCoreContext.setMetadataDataMethodOpEnum(MetadataDataMethodOpEnum.DELETE);

        metadataDataMethodCoreServiceImpl.deleteData(methodCoreContext);
    }
}
