package com.cmsr.onebase.framework.common.validation;

import com.cmsr.onebase.framework.common.tools.core.text.CharSequenceUtil;
import com.cmsr.onebase.framework.common.tools.core.util.PhoneUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelephoneValidator implements ConstraintValidator<Telephone, String> {

    @Override
    public void initialize(Telephone annotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 如果手机号为空，默认不校验，即校验通过
        if (CharSequenceUtil.isEmpty(value)) {
            return true;
        }
        // 校验手机
        return PhoneUtil.isTel(value) || PhoneUtil.isPhone(value);
    }

}
