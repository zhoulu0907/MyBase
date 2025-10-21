package com.cmsr.onebase.module.bpm.convert;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.bmp.api.dto.BpmDefinitionExtDto;
import com.cmsr.onebase.module.bpm.build.vo.design.BpmDesignVO;
import org.dromara.warm.flow.core.dto.DefJson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * 流程设计转换类
 * 使用MapStruct进行类型安全的转换，Spring Boot托管
 * 主要负责流程设计的整体转换，具体转换逻辑委托给专门的转换器
 *
 * @author liyang
 * @date 2025-10-21
 */
@Mapper(componentModel = "spring", uses = {BpmNodeConvert.class, BpmSkipConvert.class})
public interface BpmDesignConvert {

    /**
     * DefJson转换为BpmDesignVO
     *
     * @param defJson WarmFlow的DefJson
     * @return BpmDesignVO
     */
    @Mapping(target = "nodeList", source = "nodeList", qualifiedByName = "nodeJsonListToBpmNodeVOList")
    @Mapping(target = "businessId", source = "formPath")
    BpmDesignVO toFlowDesignVO(DefJson defJson);

    /**
     * BpmDesignVO转换为DefJson
     *
     * @param flowDesignVO BpmDesignVO
     * @return DefJson
     */
    @Mapping(target = "formPath", source = "businessId")
    @Mapping(target = "formCustom", constant = "Y")
    @Mapping(target = "modelValue", constant = "CLASSICS")
    @Mapping(target = "ext", source = ".", qualifiedByName = "bpmDesignVOToExt")
    DefJson toDefJson(BpmDesignVO flowDesignVO);

    /**
     * BpmDesignVO转换为ext字段JSON字符串
     *
     * @param bpmDesignVO BpmDesignVO对象
     * @return ext字段的JSON字符串
     */
    @Named("bpmDesignVOToExt")
    default String bpmDesignVOToExt(BpmDesignVO bpmDesignVO) {
        if (bpmDesignVO == null) {
            return null;
        }

        BpmDefinitionExtDto extDto = new BpmDefinitionExtDto();
        extDto.setVersionAlias(bpmDesignVO.getVersionAlias());

        return JsonUtils.toJsonString(extDto);
    }
}