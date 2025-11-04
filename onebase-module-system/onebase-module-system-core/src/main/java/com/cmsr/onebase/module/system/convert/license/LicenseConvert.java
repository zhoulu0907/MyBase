package com.cmsr.onebase.module.system.convert.license;

import com.cmsr.onebase.framework.common.consts.NumberConstant;
import com.cmsr.onebase.module.system.vo.license.LicenseExportRespVO;
import com.cmsr.onebase.module.system.vo.license.LicensePageRespVO;
import com.cmsr.onebase.module.system.vo.license.LicenseRespVO;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * License 对象转换器
 *
 * 用于VO与DO之间的对象转换。
 *
 * @author matianyu
 * @date 2025-07-25
 */
@Mapper
public interface LicenseConvert {
    LicenseConvert INSTANCE = Mappers.getMapper(LicenseConvert.class);

    /**
     * LicenseDO 转 LicenseRespVO
     *
     * @param bean LicenseDO
     * @return LicenseRespVO
     */
    @Mapping(source = "isTrial", target = "isTrial", qualifiedByName = "integerToBoolean")
    LicenseRespVO convert(LicenseDO bean);

    /**
     * LicenseDO 转 LicenseExportRespVO
     *
     * @param bean LicenseDO
     * @return LicenseExportRespVO
     */
    LicenseExportRespVO convertToExportVO(LicenseDO bean);

    /**
     * LicenseDO 列表转 LicensePageRespVO 列表
     *
     * @param list LicenseDO列表
     * @return LicensePageRespVO列表
     */
    @IterableMapping(qualifiedByName = "licenseDoToPageRespVo")
    List<LicensePageRespVO> convertList(List<LicenseDO> list);
    
    @Mapping(source = "isTrial", target = "isTrial", qualifiedByName = "integerToBoolean")
    @Named("licenseDoToPageRespVo")
    LicensePageRespVO convertToPageRespVO(LicenseDO bean);
    
    @Named("integerToBoolean")
    default Boolean integerToBoolean(Integer value) {
        return value != null && value != NumberConstant.ZERO;
    }
}