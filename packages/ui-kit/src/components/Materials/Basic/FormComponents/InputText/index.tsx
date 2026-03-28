// ===== 导入 begin =====
import { Form, Input } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect } from 'react';

import { securityEncodeText } from '@/utils';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DEFAULT_VALUE_TYPES, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XInputTextConfig } from './schema';
import { useFormulaWatchContext } from '../../../../../contexts';

import '../index.css';
import { useFormFieldWatch } from '../useFormField';
// ===== 导入 end =====

// ===== 组件定义 begin =====
const XInputText = memo((props: XInputTextConfig & { runtime?: boolean; detailMode?: boolean; cpState?: any; tooltipPosition: any; id?: string; }) => {
  // ===== 外部 props begin =====
  const {
    label,
    dataField,
    placeholder,
    tooltip,
    tooltipPosition,
    status,
    defaultValueConfig,
    verify,
    align,
    layout,
    runtime = true,
    detailMode,
    security,
    id: cpId
  } = props;
  // ===== 外部 props end =====

  // ===== 内部状态 & 回显begin =====

  // =====  内部状态 & 回显 end =====

  // ===== 表单上下文与字段名与值读取 begin =====
  const { form, fieldValue } = useFormFieldWatch(dataField);
  const targetFieldName = dataField.length > 0 ? dataField[dataField.length - 1] : '';
  // ===== 表单上下文与字段名与值读取 end =====

  // ===== 公式监听 begin =====
  const formulaWatchContext = useFormulaWatchContext();
  const isFormulaType = defaultValueConfig?.type === DEFAULT_VALUE_TYPES.FORMULA;
  const formattedFormula = defaultValueConfig?.formattedFormula || '';
  const relatedFieldsStr = JSON.stringify(defaultValueConfig?.relatedFields || []);

  useEffect(() => {
    if (!isFormulaType || !formulaWatchContext || !targetFieldName || !cpId) {
      return;
    }

    formulaWatchContext.registerFormulaComponent({
      cpId,
      targetFieldName,
      defaultValueConfig,
      formattedFormula
    });

    return () => {
      formulaWatchContext.unregisterFormulaComponent(cpId);
    };
  }, [isFormulaType, formulaWatchContext, cpId, targetFieldName, formattedFormula, relatedFieldsStr]);
  // ===== 公式监听 end =====

  // ===== 外部事件：选择数据 begin =====
  // ===== 外部事件：选择数据 end =====

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={
          dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.INPUT_TEXT}_${nanoid()}`
        }
        layout={layout}
        tooltip={tooltip && {
          content: tooltip,
          position: tooltipPosition
        }}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={[
          { required: verify?.required, message: `${label.text}是必填项` },
          {
            validator: (value, callback) => {
              if (verify.lengthLimit) {
                if (verify.minLength && value && value.length < verify.minLength) {
                  callback(`字数不能小于${verify.minLength}`);
                }
                if (verify.maxLength && value && value.length > verify.maxLength) {
                  callback(`字数不能大于${verify.maxLength}`);
                }
              }
            }
          }
        ]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : undefined}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{String(securityEncodeText(security, fieldValue))}</div>
        ) : (
          <Input
            placeholder={placeholder}
            maxLength={verify.lengthLimit ? verify.maxLength : undefined}
            style={{
              width: '100%',
              textAlign: align,
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          />
        )}
      </Form.Item>
    </div>
  );
});

export default XInputText;
