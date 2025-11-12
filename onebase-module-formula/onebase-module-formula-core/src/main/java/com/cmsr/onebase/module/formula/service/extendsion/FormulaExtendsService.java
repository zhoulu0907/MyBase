package com.cmsr.onebase.module.formula.service.extendsion;

import java.util.Map;

public interface FormulaExtendsService {

    void buildParametersWithSystemInfo(String formula, Map<String, Object> parameters);

}
