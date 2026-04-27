import { useState, useCallback, useMemo } from 'react';
import type { FieldFormValues, FieldValidation } from '../types';

// 表单校验管理hook
export const useFieldValidation = (): FieldValidation => {
  const [errors, setErrors] = useState<Record<string, string>>({});

  // 验证单个字段
  const validateField = useCallback((field: FieldFormValues): string[] => {
    const fieldErrors: string[] = [];

    // 验证字段名称
    if (!field.fieldName) {
      fieldErrors.push('字段名称不能为空');
    } else if (!/^[a-z][a-z0-9_]{0,39}$/.test(field.fieldName)) {
      fieldErrors.push('字段名称由小写字母、数字、下划线组成，须以字母开头，不超过40个字符');
    }

    // 验证展示名称
    if (!field.displayName) {
      fieldErrors.push('展示名称不能为空');
    }

    // 验证数据类型
    if (!field.fieldType) {
      fieldErrors.push('数据类型不能为空');
    }

    // 验证约束配置
    if (field.constraints) {
      const { lengthEnabled, minLength, maxLength } = field.constraints;
      if (lengthEnabled === 1 && maxLength < minLength) {
        fieldErrors.push('最大长度应不小于最小长度');
      }
    }

    return fieldErrors;
  }, []);

  // 验证所有字段
  const validateAllFields = useCallback(
    (fields: FieldFormValues[]): Record<string, string> => {
      const allErrors: Record<string, string> = {};

      fields.forEach((field, index) => {
        if (!field.id) return;

        const fieldErrors = validateField(field);
        if (fieldErrors.length > 0) {
          fieldErrors.forEach((error, errorIndex) => {
            const fieldKey = `fields.${index}.${errorIndex === 0 ? 'fieldName' : 'displayName'}`;
            allErrors[fieldKey] = error;
          });
        }
      });

      setErrors(allErrors);
      return allErrors;
    },
    [validateField]
  );

  // 清除错误
  const clearErrors = useCallback(() => {
    setErrors({});
  }, []);

  // 设置字段错误
  const setFieldError = useCallback((fieldId: string, field: string, error: string) => {
    setErrors((prev) => ({
      ...prev,
      [`${fieldId}.${field}`]: error
    }));
  }, []);

  // 设置所有错误
  const setAllErrors = useCallback((newErrors: Record<string, string>) => {
    setErrors(newErrors);
  }, []);

  // 清除单个字段错误
  const clearFieldError = useCallback((fieldKey: string) => {
    setErrors((prev) => {
      const { [fieldKey]: _, ...rest } = prev;
      return rest;
    });
  }, []);

  return useMemo(
    () => ({
      validateField,
      validateAllFields,
      clearErrors,
      setFieldError,
      setAllErrors,
      clearFieldError,
      errors
    }),
    [validateField, validateAllFields, clearErrors, setFieldError, setAllErrors, clearFieldError, errors]
  );
};
