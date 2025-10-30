package com.cmsr.onebase.module.formula.service.user;

import java.util.Map;

public interface FormulaUserService {

    void enrichParametersWithUserInfo(String formula, Map<String, Object> parameters);

}
