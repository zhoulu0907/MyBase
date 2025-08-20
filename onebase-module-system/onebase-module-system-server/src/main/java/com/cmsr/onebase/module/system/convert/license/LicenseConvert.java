package com.cmsr.onebase.module.system.convert.license;

import com.cmsr.onebase.module.system.controller.admin.license.vo.LicenseExportRespVO;
import com.cmsr.onebase.module.system.controller.admin.license.vo.LicenseSaveReqVO;
import com.cmsr.onebase.module.system.controller.admin.license.vo.LicenseRespVO;
import com.cmsr.onebase.module.system.controller.admin.license.vo.LicensePageRespVO;
import com.cmsr.onebase.module.system.dal.dataobject.license.LicenseDO;
import org.mapstruct.Mapper;
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
    List<LicensePageRespVO> convertList(List<LicenseDO> list);
}
