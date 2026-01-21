// ===== 导入 begin =====
import { Form, Rate, Typography } from '@arco-design/web-react';
import {
  IconStarFill,
  IconFaceSmileFill,
  IconBulb,
  IconSunFill,
  IconThumbUpFill,
  IconFire,
  IconHeartFill,
  IconBug,
  IconExclamationCircleFill,
  IconPushpin,
  IconSubscribe,
  IconClose,
  IconNotification,
  IconMinus
} from '@arco-design/web-react/icon';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DEFAULT_VALUE_TYPES, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XRateConfig } from './schema';
import { useFormField } from '../useFormField';
import '../index.css';
// ===== 导入 end =====

// ===== 组件定义 begin =====
const XRate = memo((props: XRateConfig & { runtime?: boolean; detailMode?: boolean }) => {
  // ===== 外部 props begin =====
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValueConfig,
    rateConfig,
    verify,
    align,
    layout,
    runtime = true,
    detailMode
  } = props;
  // ===== 外部 props end =====

  // ===== 内部状态 & 回显begin =====
  // 图标下拉内容
  const dropList = [
    { lable: <IconStarFill />, value: 'IconStarFill' },
    { lable: <IconFaceSmileFill />, value: 'IconFaceSmileFill' },
    { lable: <IconBulb />, value: 'IconBulb' },
    { lable: <IconSunFill />, value: 'IconSunFill' },
    { lable: <IconThumbUpFill />, value: 'IconThumbUpFill' },
    { lable: <IconFire />, value: 'IconFire' },
    { lable: <IconHeartFill />, value: 'IconHeartFill' },
    { lable: <IconBug />, value: 'IconBug' },
    { lable: <IconExclamationCircleFill />, value: 'IconExclamationCircleFill' },
    { lable: <IconPushpin />, value: 'IconPushpin' },
    { lable: <IconSubscribe />, value: 'IconSubscribe' },
    { lable: <IconClose />, value: 'IconClose' },
    { lable: <IconNotification />, value: 'IconNotification' },
    { lable: <IconMinus />, value: 'IconMinus' }
  ];
  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.RATE}_${nanoid()}`;
  // =====  内部状态 & 回显 end =====

  // ===== 表单上下文与字段名与值读取 begin =====
  const { form, fieldValue } = useFormField(dataField, nanoid(), FORM_COMPONENT_TYPES.RATEs);
  const [tooltipValue, setTooltipValue] = useState('');
  // ===== 表单上下文与字段名与值读取 end =====

  useEffect(() => {
    const newTooltipValue = rateConfig.showCustomTooltips ? rateConfig.tooltips?.[Math.ceil(fieldValue) - 1] || '' : fieldValue
    setTooltipValue(newTooltipValue);
  }, [fieldValue]);

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldId}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={[{ required: verify?.required, message: `${label.text}是必填项` }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : undefined}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{String(fieldValue)}</div>
        ) : (
          <div style={{ display: 'flex', alignItems: 'center' }} className="custom-rate">
            <Rate
              count={rateConfig.max || 5}
              style={{ stroke: rateConfig.iconColor, fill: rateConfig.iconColor }}
              allowHalf={rateConfig.allowHalf}
              tooltips={
                rateConfig.showResult
                  ? rateConfig.showCustomTooltips
                    ? rateConfig.tooltips
                    : Array.from({ length: rateConfig.max || 5 }, (_, index) => `${index + 1}`)
                  : undefined
              }
              character={(index) => {
                if (!rateConfig.showIcon) {
                  return <span>{index + 1}</span>;
                }
                return <span>{dropList.find((ele) => ele.value === rateConfig.iconName)?.lable}</span>;
              }}
              onChange={(value)=>{
                const newTooltipValue = rateConfig.showCustomTooltips ? rateConfig.tooltips?.[Math.ceil(value) - 1] || '' : `${value}`
                setTooltipValue(newTooltipValue);
                form.setFieldValue(fieldId, value);
              }}
            />
            {rateConfig.showResult && (
              <Typography.Text style={{ marginLeft: '16px' }}>
                {tooltipValue}
              </Typography.Text>
            )}
          </div>
        )}
      </Form.Item>
    </div>
  );
});

export default XRate;
