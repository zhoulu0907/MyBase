// ===== 导入 begin =====
import { Form, Input, Select } from '@arco-design/web-react';
import { memo, useEffect, useState } from 'react';

import { FORM_COMPONENT_TYPES } from '@/components/Materials/componentTypes';
import { IconClose } from '@arco-design/web-react/icon';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import PreviewDataSelectModal from './previewDataSelectModal';
import { XDataSelectConfig } from './schema';

import { dataMethodPageV2, menuSignal, PageMethodV2Params } from '@onebase/app';
import { useFormField } from '../useFormField';

import { useFormEditorSignal } from '@/index';
import './index.css';
// ===== 导入 end =====

// ===== 组件定义 begin =====
const XDataSelect = memo((props: XDataSelectConfig & { runtime?: boolean; detailMode?: boolean }) => {
  // ===== 外部 props begin =====
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValue,
    verify,
    layout,
    labelColSpan = 0,
    runtime,
    displayFields,
    detailMode,
    fillRuleSetting
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
      console.log('data: ', data, 'typeof data: ', typeof data);
      if (!data) return '';
      if (typeof data === 'object') {
        if (typeof data.id !== 'undefined' || typeof data.name !== 'undefined') {
          return { id: data.id ?? '', name: data.name ?? '' };
        }
        if (typeof data.selectID !== 'undefined' || typeof data.displayValue !== 'undefined') {
          return { id: data.selectID ?? '', name: data.displayValue ?? '' };
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
      const data = dataList.find(item => item.id === value);
      const name = option?.labelTitle ?? option.children ?? '';
      const nextValue = value ? { id: value, name } : '';
      setDataState(nextValue);
      if (runtime) {
        form.setFieldValue(fieldName, nextValue);
        internalEvents.fillDatabyRule(data);
      }
    },
    fillDatabyRule: (data: any) => {
      if (fillRuleSetting.length > 0) {
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

  // ===== 方法：帮助方法 end =====

  useEffect(() => {
    const fetchOptions = async () => {
      if (!runtime) return;
      const tableName = props?.selectedDataSource?.tableName;
      if (!tableName) return;
      const { curMenu } = menuSignal;
      const req: PageMethodV2Params = {
        pageNo: 1,
        pageSize: 100
      };
      const res = await dataMethodPageV2(tableName, curMenu.value?.id, req);
      const lastKey = (displayFields || []).length ? displayFields[displayFields.length - 1]?.value : undefined;
      const list = Array.isArray(res?.list) ? res.list : [];
      const opts = list.map((item: any) => ({
        label: lastKey ? (item?.[lastKey] ?? '') : '',
        value: item?.id ?? item?.id
      }));
      setOptions(opts);
      setDataList(list);
    };
    if (helpers.isDropdownMode()) {
      fetchOptions();
    }
  }, [
    runtime,
    props.selectMethod,
    props?.dynamicTableConfig?.metaData,
    props?.selectedDataSource?.entityUuid,
    displayFields
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
        tooltip={tooltip}
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
            tableConfig={props.dynamicTableConfig}
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
      tooltip={tooltip}
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
