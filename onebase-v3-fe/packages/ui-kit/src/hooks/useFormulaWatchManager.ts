import { useEffect, useRef, useCallback, useMemo } from 'react';
import { debugFormula } from '@onebase/app';

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
  const isComputingRef = useRef(false);
  
  const formulaComponentsRef = useRef<Map<string, FormulaComponentInfo>>(new Map());
  const fieldDependencyRef = useRef<Map<string, Set<string>>>(new Map());
  const timersRef = useRef<Map<string, ReturnType<typeof setTimeout>>>(new Map());
  
  const setForm = useCallback((form: any) => {
    formRef.current = form;
  }, []);
  
  const computeFormulaForComponent = useCallback(async (cpId: string, triggerSource?: string) => {
    console.log(`[computeFormulaForComponent] 触发来源: ${triggerSource || '未知'}, cpId: ${cpId}`);
    const info = formulaComponentsRef.current.get(cpId);
    const form = formRef.current;
    if (!info || !form) return;
    
    const { targetFieldName, defaultValueConfig, formattedFormula } = info;
    
    const parameters: Record<string, any> = {};
    
    if (defaultValueConfig?.relatedFields) {
      defaultValueConfig.relatedFields.forEach((field: RelatedField) => {
        const value = form.getFieldValue(field.formFieldName);
        parameters[field.fieldName] = value ?? '';
      });
    }
    
    console.log(`[公式计算] ${formattedFormula}`);
    console.log(`  关联字段值:`, parameters);
    
    try {
      const response = await debugFormula({
        formula: formattedFormula,
        parameters
      });
      
      const computedValue = response?.result ?? '';
      console.log(`  计算结果:`, computedValue);
      
      const currentValue = form.getFieldValue(targetFieldName);
      if (currentValue !== computedValue) {
        isComputingRef.current = true;
        form.setFieldValue(targetFieldName, computedValue);
        setTimeout(() => {
          isComputingRef.current = false;
        }, 0);
      }
    } catch (error) {
      console.error(`[公式计算失败]`, error);
    }
  }, []);
  
  const computeForField = useCallback((changedFieldName: string) => {
    const dependentCpIds = fieldDependencyRef.current.get(changedFieldName);
    if (dependentCpIds) {
      dependentCpIds.forEach((cpId) => {
        computeFormulaForComponent(cpId, `字段变化: ${changedFieldName}`);
      });
    }
  }, [computeFormulaForComponent]);
  
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
    
    computeFormulaForComponent(info.cpId, '组件注册');
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
  
  const handleValuesChange = useCallback((changedValues: any, allValues: any) => {
    if (isComputingRef.current) {
      console.log('[handleValuesChange] 正在计算中，跳过');
      return;
    }
    
    console.log('[handleValuesChange] changedValues:', changedValues);
    console.log('[handleValuesChange] fieldDependencyRef keys:', Array.from(fieldDependencyRef.current.keys()));
    
    Object.keys(changedValues).forEach((fieldName) => {
      console.log(`[handleValuesChange] 检查字段: ${fieldName}, 是否在依赖中: ${fieldDependencyRef.current.has(fieldName)}`);
      
      if (!fieldDependencyRef.current.has(fieldName)) {
        return;
      }
      
      if (timersRef.current.has(fieldName)) {
        clearTimeout(timersRef.current.get(fieldName)!);
      }
      
      const timer = setTimeout(() => {
        computeForField(fieldName);
        timersRef.current.delete(fieldName);
      }, 300);
      
      timersRef.current.set(fieldName, timer);
    });
  }, [computeForField]);
  
  useEffect(() => {
    return () => {
      timersRef.current.forEach((timer) => {
        clearTimeout(timer);
      });
      timersRef.current.clear();
    };
  }, []);
  
  return useMemo(() => ({
    registerFormulaComponent,
    unregisterFormulaComponent,
    handleValuesChange,
    setForm
  }), [registerFormulaComponent, unregisterFormulaComponent, handleValuesChange, setForm]);
}
