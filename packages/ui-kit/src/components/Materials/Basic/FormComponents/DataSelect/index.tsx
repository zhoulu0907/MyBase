// ===== 导入 begin =====
import { Form, Input, Select } from '@arco-design/web-react';
import { memo, useEffect, useState } from 'react';

import { FORM_COMPONENT_TYPES } from '@/components/Materials/componentTypes';
import { IconClose } from '@arco-design/web-react/icon';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import PreviewDataSelectModal from './previewDataSelectModal';
import { XDataSelectConfig } from './schema';

import { dataMethodPageV2, debugFormula, menuSignal, PageMethodV2Params } from '@onebase/app';
import { useFormField } from '../useFormField';

import { useFormEditorSignal } from '@/index';
import { isRuntimeEnv } from '@onebase/common';
import './index.css';

// ===== 过滤条件转换函数 begin =====
/**
 * 将前端 filterCondition 转换为后端 API 需要的 filters 格式（异步版本，支持公式计算）
 */
async function transformFilterConditionToFiltersAsync(filterCondition: any[], form?: any): Promise<any> {
  if (!filterCondition || filterCondition.length === 0) {
    return undefined;
  }

  // 如果只有一个顶级组
  if (filterCondition.length === 1) {
    const children = await transformConditionsAsync(filterCondition[0].conditions, form);
    if (!children || children.length === 0) return undefined;

    return {
      nodeType: 'GROUP',
      combinator: 'AND',
      children
    };
  }

  // 多个顶级组，用 OR 连接
  const childrenPromises = filterCondition.map(async (group) => {
    const groupChildren = await transformConditionsAsync(group.conditions, form);
    return {
      nodeType: 'GROUP',
      combinator: 'AND',
      children: groupChildren || []
    };
  });

  const children = await Promise.all(childrenPromises);
  const filteredChildren = children.filter((g: any) => g.children && g.children.length > 0);

  if (filteredChildren.length === 0) return undefined;

  return {
    nodeType: 'GROUP',
    combinator: 'OR',
    children: filteredChildren
  };
}

/**
 * 转换条件列表（异步版本）
 */
async function transformConditionsAsync(conditions: any[], form?: any): Promise<any[]> {
  if (!conditions) return [];

  const promises = conditions
    .filter(cond => cond && cond.fieldKey && cond.op)
    .map(async (cond) => {
      const fieldName = cond.fieldKey?.split('.')[1] || cond.fieldKey;
      const fieldValue = await resolveFieldValueAsync(cond, form);

      return {
        nodeType: 'CONDITION',
        fieldName,
        operator: cond.op,
        fieldValue
      };
    });

  return Promise.all(promises);
}

/**
 * 解析字段值（处理静态值、变量、公式）- 异步版本
 */
async function resolveFieldValueAsync(cond: any, form?: any): Promise<any[]> {
  const { operatorType, value } = cond;

  if (!value) return [];

  switch (operatorType) {
    case 'value':
      // 静态值：转为数组
      return Array.isArray(value) ? value : [value];

    case 'variables':
      // 变量：从当前表单上下文获取值
      return resolveVariableValue(value, form);

    case 'formula':
      // 公式：计算公式结果
      return await resolveFormulaValue(value, form);

    default:
      // 默认当作静态值处理
      return Array.isArray(value) ? value : [value];
  }
}

/**
 * 解析公式值
 * @param formulaData 公式数据对象 { formulaData, formula, parameters, relatedFields }
 * @param form 当前表单实例
 */
async function resolveFormulaValue(formulaData: any, form?: any): Promise<any[]> {
  console.log('[DataSelect] resolveFormulaValue called');
  console.log('[DataSelect] formulaData:', JSON.stringify(formulaData, null, 2));
  console.log('[DataSelect] form exists:', !!form);

  if (!formulaData || !formulaData.formula) {
    console.log('[DataSelect] No formulaData or formula, returning []');
    return [];
  }

  try {
    // 构建参数对象：从表单获取各参数的实际值
    const parameters: Record<string, any> = {};

    console.log('[DataSelect] formulaData.parameters:', formulaData.parameters);
    console.log('[DataSelect] formulaData.relatedFields:', formulaData.relatedFields);

    // 构建 displayName -> formFieldName 的映射
    const fieldNameMap: Record<string, string> = {};
    if (formulaData.relatedFields && Array.isArray(formulaData.relatedFields)) {
      formulaData.relatedFields.forEach((field: any) => {
        // relatedFields 中有 fieldName（显示名）和 formFieldName（实际表单字段名）
        if (field.fieldName && field.formFieldName) {
          fieldNameMap[field.fieldName] = field.formFieldName;
        }
      });
    }
    console.log('[DataSelect] fieldNameMap:', fieldNameMap);

    if (formulaData.parameters) {
      Object.entries(formulaData.parameters).forEach(([paramName, paramValue]) => {
        console.log('[DataSelect] Processing param:', paramName, 'raw value:', paramValue);

        if (form) {
          // 跳过节点ID映射（值是 nodeId）
          if (paramName.startsWith('$') && !paramName.includes('.')) {
            console.log('[DataSelect] Skipping node ID param:', paramName);
            return;
          }

          // 使用 relatedFields 中的映射获取正确的表单字段名
          let formFieldName = fieldNameMap[paramName] || paramName;

          // 子表/节点字段：去掉 $ 前缀，取字段名部分
          if (paramName.startsWith('$') && paramName.includes('.')) {
            const parts = paramName.split('.');
            formFieldName = parts[parts.length - 1]; // 取最后一段作为字段名
          }

          console.log('[DataSelect] Using formFieldName:', formFieldName);
          const fieldValue = form.getFieldValue(formFieldName);
          console.log('[DataSelect] form.getFieldValue("' + formFieldName + '"):', fieldValue);

          // 处理数据选择组件的特殊值格式 { id, name }
          if (fieldValue && typeof fieldValue === 'object' && fieldValue.id !== undefined) {
            parameters[paramName] = fieldValue.id;
            console.log('[DataSelect] Extracted id from object:', fieldValue.id);
          } else {
            parameters[paramName] = fieldValue ?? '';
            console.log('[DataSelect] Set param', paramName, 'to:', fieldValue ?? '(empty)');
          }
        } else {
          console.log('[DataSelect] No form available');
          parameters[paramName] = '';
        }
      });
    }

    console.log('[DataSelect] Final Formula params:', JSON.stringify(parameters, null, 2));
    console.log('[DataSelect] Formula to calculate:', formulaData.formula);

    // 调用公式计算 API
    const response = await debugFormula({
      formula: formulaData.formula,
      parameters
    });

    const result = response?.result;
    console.log('[DataSelect] Formula API response:', response);
    console.log('[DataSelect] Formula result:', result);

    // 处理不同格式的结果
    if (result == null) return [];
    if (typeof result === 'object' && result.id !== undefined) {
      return [result.id];
    }
    if (Array.isArray(result)) return result;
    return [result];
  } catch (error) {
    console.error('[DataSelect] Formula evaluation failed:', error);
    return [];
  }
}

/**
 * 解析变量值
 * @param variablePath 变量路径，格式：currentForm.fieldName 或 tableName.fieldName
 * @param form 当前表单实例
 */
function resolveVariableValue(variablePath: string, form?: any): any[] {
  if (!variablePath || !form) return [];

  // 解析变量路径
  const parts = variablePath.split('.');
  const fieldKey = parts[parts.length - 1];  // 取最后一段作为字段名

  // 从表单获取字段值
  const formValue = form.getFieldValue(fieldKey);

  if (formValue == null) return [];

  // 处理对象格式的值（如数据选择组件 { id, name }）
  if (typeof formValue === 'object' && formValue.id !== undefined) {
    return [formValue.id];
  }

  // 数组值
  if (Array.isArray(formValue)) {
    return formValue;
  }

  return [formValue];
}
// ===== 过滤条件转换函数 end =====
// ===== 导入 end =====

// ===== 组件定义 begin =====
const XDataSelect = memo((props: XDataSelectConfig & { runtime?: boolean; detailMode?: boolean; tooltipPosition: any; }) => {
  // ===== 外部 props begin =====
  const {
    label,
    dataField,
    tooltip,
    tooltipPosition,
    status,
    defaultValue,
    verify,
    layout,
    labelColSpan = 0,
    runtime,
    displayFields = [],
    detailMode,
    fillRuleSetting = []
  } = props;
  const { pageComponentSchemas: fromPageComponentSchemas } = useFormEditorSignal;
  // ===== 外部 props end =====

  // ===== 表单上下文与字段名与值读取 begin =====
  const {
    form,
    fieldName,
    fieldValue: formFieldValue
  } = useFormField(dataField, props.id, FORM_COMPONENT_TYPES.DATA_SELECT);
  // ===== 表单上下文与字段名与值读取 end =====

  // ===== 外部事件：选择数据 begin =====
  // ===== 外部事件：选择数据 end =====

  // ===== 内部状态 & 回显begin =====
  const [uiState, setUiState] = useState<{ previewVisible: boolean }>({ previewVisible: false });
  const [dataState, setDataState] = useState<any>('');
  const [options, setOptions] = useState<any[]>([]);
  const [dataList, setDataList] = useState<any[]>([]);

  useEffect(() => {
    if (!runtime) {
      setDataState('');
      return;
    }
    const normalize = (data: any) => {
      if (!data) return '';
      if (typeof data === 'object') {
        // 新格式：{ id, name, value } - 保留 value 用于显示和访问
        if (typeof data.id !== 'undefined' || typeof data.name !== 'undefined') {
          return { id: data.id ?? '', name: data.name ?? '', value: data.value ?? null };
        }
        // 旧格式兼容：{ selectID, displayValue }
        if (typeof data.selectID !== 'undefined' || typeof data.displayValue !== 'undefined') {
          return { id: data.selectID ?? '', name: data.displayValue ?? '', value: null };
        }
      } else if (typeof data === 'string') {
        return data;
      }
      return '';
    };
    const normalizedValue = normalize(formFieldValue);
    setDataState(normalizedValue);
  }, [formFieldValue, runtime]);
  // =====  内部状态 & 回显 end =====

  // ===== 内部事件 =====
  const internalEvents = {
    openPreview: () => setUiState((prev) => ({ ...prev, previewVisible: true })),
    closePreview: () => setUiState((prev) => ({ ...prev, previewVisible: false })),
    clear: (e: React.MouseEvent) => {
      e.stopPropagation();
      setDataState('');
      if (runtime) {
        form.setFieldValue(fieldName, '');
      }
    },
    selectData: (data: any) => {
      const lastKey = (displayFields || []).length ? displayFields[displayFields.length - 1]?.value : undefined;
      const name = lastKey ? data?.[lastKey] : '';
      const nextValue = data ? { id: data.id, name } : '';
      setDataState(nextValue);
      if (runtime) {
        form.setFieldValue(fieldName, nextValue);
        internalEvents.fillDatabyRule(data);
      }
    },
    selectDropdown: (value: any, option: any) => {
      const data = dataList.find((item) => item.id === value);
      const name = option?.labelTitle ?? option.children ?? '';
      const nextValue = value ? { id: value, name } : '';
      setDataState(nextValue);
      if (runtime) {
        form.setFieldValue(fieldName, nextValue);
        internalEvents.fillDatabyRule(data);
      }
    },
    fillDatabyRule: (data: any) => {
      if (!Array.isArray(fillRuleSetting) || fillRuleSetting.length === 0) return;
      fillRuleSetting.forEach((item) => {
        const value = data?.[item.fieldName];
        const dataField = fromPageComponentSchemas.value[item.selectComponentID].config.dataField;
        if (dataField.length > 0) {
          const fieldName =
            fromPageComponentSchemas.value[item.selectComponentID].config.dataField[dataField.length - 1];
          form.setFieldValue(fieldName, value);
        }
      });
    }
  };
  // ===== 内部事件 =====

  // ===== 方法：帮助方法 begin =====
  const helpers = {
    getDisplayText: (v: any) => {
      return v && typeof v === 'object'
        ? ((v.name && typeof v.name === 'object' ? v.name?.name : v.name) ?? '')
        : typeof v === 'string'
          ? v
          : '';
    },

    getSelectedId: (v: any) => (v && typeof v === 'object' ? (v.id ?? null) : null),
    isDropdownMode: () => props.selectMethod === 'dropdown'
  };

  const normalizeLabel = (value: any): string => {
    if (value == null) return '';
    if (typeof value === 'string') return value;
    if (Array.isArray(value)) {
      if (value.length === 0) return '';
      return value.map((item: any) => item?.name ?? '').filter(Boolean).join(', ') || '';
    }
    if (typeof value === 'object') {
      return value?.name ?? value?.label ?? JSON.stringify(value);
    }
    return String(value);
  };

  const fetchOptions = async () => {
    if (!runtime || !isRuntimeEnv()) {
      return;
    }
    const tableName = props?.selectedDataSource?.tableName;
    if (!tableName) return;
    const { curMenu } = menuSignal;

    // 处理过滤条件（异步，支持公式计算）
    const filterCondition = props?.filterCondition;
    const filters = await transformFilterConditionToFiltersAsync(filterCondition, form);

    const req: PageMethodV2Params = {
      pageNo: 1,
      pageSize: 100,
      filters
    };

    const res = await dataMethodPageV2(tableName, curMenu.value?.id, req);
    const lastKey = (displayFields || []).length ? displayFields[displayFields.length - 1]?.value : undefined;
    const list = Array.isArray(res?.list) ? res.list : [];
    const opts = list.map((item: any) => ({
      label: normalizeLabel(lastKey ? item?.[lastKey] : ''),
      value: item?.id ?? ''
    }));
    setOptions(opts);
    setDataList(list);
  };

  // ===== 方法：帮助方法 end =====

  useEffect(() => {
    if (isInteractive && helpers.isDropdownMode()) {
      fetchOptions();
    }
  }, [
    runtime,
    props.selectMethod,
    props?.dynamicTableConfig?.metaData,
    props?.selectedDataSource?.entityUuid,
    displayFields,
    props.filterCondition
  ]);

  const renderInteractiveContent = () => (
    <div className="dataSelectTrigger" onClick={() => internalEvents.openPreview()}>
      <Input
        readOnly
        value={helpers.getDisplayText(dataState)}
        suffix={
          !!helpers.getDisplayText(dataState) ? (
            <span className="dataSelectClearIcon" onClick={(e) => internalEvents.clear(e as any)} title="清除">
              <IconClose style={{ fontSize: 12 }} />
            </span>
          ) : undefined
        }
      />
    </div>
  );

  const renderReadonlyContent = () => {
    const fieldValue = helpers.getDisplayText(dataState);
    return <div className="dataSelectReadonly">{fieldValue || '--'}</div>;
  };

  const renderDropdownContent = () => {
    const selectedId = helpers.getSelectedId(dataState);
    const selectedLabel = helpers.getDisplayText(dataState);
    const exists = (options || []).some((o: any) => String(o?.value ?? '') === String(selectedId ?? ''));
    const finalOptions =
      selectedId != null && selectedLabel
        ? exists
          ? options
          : [{ label: selectedLabel, value: selectedId }, ...(options || [])]
        : options || [];
    return (
      <div>
        <Input value={selectedId} hidden />
        <Select
          showSearch
          allowClear
          value={selectedId}
          options={finalOptions}
          style={{ minWidth: '120px', width: '100%' }}
          onChange={(v, option) => internalEvents.selectDropdown(v, option)}
          onFocus={() => fetchOptions()}
        />
      </div>
    );
  };

  const renderRuntime = (interactive: boolean) => (
    <>
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldName}
        layout={layout}
        tooltip={ tooltip && {
          content: tooltip,
          position: tooltipPosition
        }}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={[{ required: verify?.required, message: `${label.text}是必填项` }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        {interactive
          ? helpers.isDropdownMode()
            ? renderDropdownContent()
            : renderInteractiveContent()
          : renderReadonlyContent()}
      </Form.Item>
      {interactive &&
        (helpers.isDropdownMode() ? null : (
          <PreviewDataSelectModal
            visible={uiState.previewVisible}
            onCancel={internalEvents.closePreview}
            tableConfig={props.dynamicTableConfig ?? null}
            defaultSelectedId={helpers.getSelectedId(dataState)}
            onSelect={internalEvents.selectData}
          />
        ))}
    </>
  );

  const renderBuilder = () => (
    <Form.Item
      label={
        label.display && label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
      }
      field={fieldName}
      layout={layout}
      tooltip={ tooltip && {
        content: tooltip,
        position: tooltipPosition
      }}
      labelCol={layout === 'horizontal' ? { span: 10 } : {}}
      rules={[{ required: verify?.required }]}
      hidden={false}
      style={{
        margin: 0,
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      <Input readOnly placeholder={defaultValue} value={helpers.getDisplayText(dataState)} />
    </Form.Item>
  );

  const isInteractive = runtime && status !== STATUS_VALUES[STATUS_OPTIONS.READONLY] && !detailMode;
  return <div className="formWrapper">{runtime ? renderRuntime(isInteractive!) : renderBuilder()}</div>;
});
// ===== 组件定义 end =====

export default XDataSelect;
