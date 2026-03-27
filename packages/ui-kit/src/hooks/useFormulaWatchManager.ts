import { useEffect, useRef, useCallback } from 'react';
import { throttle } from 'lodash-es';

interface RelatedField {
  fieldId: string;
  fieldName: string;
  formFieldName: string;
}

interface FormulaComponentInfo {
  cpId: string;
  targetFieldName: string;
  defaultValueConfig: {
    relatedFields?: RelatedField[];
    formattedFormula?: string;
    [key: string]: any;
  };
  formattedFormula: string;
}

interface FormulaWatchManagerReturn {
  registerFormulaComponent: (info: FormulaComponentInfo) => void;
  unregisterFormulaComponent: (cpId: string) => void;
  handleValuesChange: (changedValues: any, allValues: any) => void;
  setForm: (form: any) => void;
}

export function useFormulaWatchManager(): FormulaWatchManagerReturn {
  const formRef = useRef<any>(null);
  
  const formulaComponentsRef = useRef<Map<string, FormulaComponentInfo>>(new Map());
  const fieldDependencyRef = useRef<Map<string, Set<string>>>(new Map());
  
  const setForm = useCallback((form: any) => {
    formRef.current = form;
  }, []);
  
  const computeFormulaForComponent = useCallback((cpId: string) => {
    const info = formulaComponentsRef.current.get(cpId);
    const form = formRef.current;
    if (!info || !form) return;
    
    const { targetFieldName, defaultValueConfig, formattedFormula } = info;
    
    let computedValue = '';
    const relatedValues: Record<string, any> = {};
    
    if (defaultValueConfig?.relatedFields) {
      defaultValueConfig.relatedFields.forEach((field: RelatedField) => {
        const value = form.getFieldValue(field.formFieldName);
        relatedValues[field.fieldName] = value;
        if (!computedValue) {
          computedValue = value ?? '';
        }
      });
    }
    
    console.log(`[公式计算] ${formattedFormula}`);
    console.log(`  关联字段值:`, relatedValues);
    console.log(`  计算结果:`, computedValue);
    
    const currentValue = form.getFieldValue(targetFieldName);
    if (currentValue !== computedValue) {
      form.setFieldValue(targetFieldName, computedValue);
    }
  }, []);
  
  const registerFormulaComponent = useCallback((info: FormulaComponentInfo) => {
    formulaComponentsRef.current.set(info.cpId, info);
    
    if (info.defaultValueConfig?.relatedFields) {
      info.defaultValueConfig.relatedFields.forEach((field: RelatedField) => {
        const formFieldName = field.formFieldName;
        if (!fieldDependencyRef.current.has(formFieldName)) {
          fieldDependencyRef.current.set(formFieldName, new Set());
        }
        fieldDependencyRef.current.get(formFieldName)!.add(info.cpId);
      });
    }
    
    computeFormulaForComponent(info.cpId);
  }, [computeFormulaForComponent]);
  
  const unregisterFormulaComponent = useCallback((cpId: string) => {
    const info = formulaComponentsRef.current.get(cpId);
    if (info?.defaultValueConfig?.relatedFields) {
      info.defaultValueConfig.relatedFields.forEach((field: RelatedField) => {
        const formFieldName = field.formFieldName;
        const deps = fieldDependencyRef.current.get(formFieldName);
        if (deps) {
          deps.delete(cpId);
          if (deps.size === 0) {
            fieldDependencyRef.current.delete(formFieldName);
          }
        }
      });
    }
    formulaComponentsRef.current.delete(cpId);
  }, []);
  
  const throttledCompute = useRef(
    throttle((changedFieldName: string) => {
      const dependentCpIds = fieldDependencyRef.current.get(changedFieldName);
      if (dependentCpIds) {
        dependentCpIds.forEach((cpId) => {
          computeFormulaForComponent(cpId);
        });
      }
    }, 300)
  ).current;
  
  const handleValuesChange = useCallback((changedValues: any, allValues: any) => {
    Object.keys(changedValues).forEach((fieldName) => {
      throttledCompute(fieldName);
    });
  }, [throttledCompute]);
  
  useEffect(() => {
    return () => {
      throttledCompute.cancel();
    };
  }, [throttledCompute]);
  
  return {
    registerFormulaComponent,
    unregisterFormulaComponent,
    handleValuesChange,
    setForm
  };
}
