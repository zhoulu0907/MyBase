package com.cmsr.onebase.module.formula.convert;

import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.formula.api.formula.dto.FormulaExecuteReqDTO;
import com.cmsr.onebase.module.formula.api.formula.dto.FormulaExecuteRespDTO;
import com.cmsr.onebase.module.system.api.dept.dto.DeptAndUsersReqDTO;
import org.mapstruct.factory.Mappers;

public interface FormulaConvert {

    FormulaConvert INSTANCE = Mappers.getMapper(FormulaConvert.class);

    CommonResult<FormulaExecuteRespDTO> toDeptAndUsersReqVO(FormulaExecuteReqDTO reqVO);

}
