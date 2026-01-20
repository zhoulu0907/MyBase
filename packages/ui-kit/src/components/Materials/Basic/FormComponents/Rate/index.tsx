// ===== 导入 begin =====
import { Form, Rate, Typography } from '@arco-design/web-react';
import {
  IconStar,
  IconBulb,
  IconSun,
  IconThumbUp,
  IconFire,
  IconHeart,
  IconBug,
  IconExclamationCircle,
  IconPushpin,
  IconSubscribe,
  IconClose,
  IconNotification,
  IconSafe,
  IconMinus
} from '@arco-design/web-react/icon';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { DEFAULT_VALUE_TYPES, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { type XRateConfig } from './schema';
import { useFormFieldWatch } from '../useFormField';
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
    { lable: <IconStar />, value: 'IconStar' },
    { lable: <IconBulb />, value: 'IconBulb' },
    { lable: <IconSun />, value: 'IconSun' },
    { lable: <IconThumbUp />, value: 'IconThumbUp' },
    { lable: <IconFire />, value: 'IconFire' },
    { lable: <IconHeart />, value: 'IconHeart' },
    { lable: <IconBug />, value: 'IconBug' },
    { lable: <IconExclamationCircle />, value: 'IconExclamationCircle' },
    { lable: <IconPushpin />, value: 'IconPushpin' },
    { lable: <IconSubscribe />, value: 'IconSubscribe' },
    { lable: <IconClose />, value: 'IconClose' },
    { lable: <IconNotification />, value: 'IconNotification' },
    { lable: <IconSafe />, value: 'IconSafe' },
    { lable: <IconMinus />, value: 'IconMinus' }
  ];
  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.RATE}_${nanoid()}`;
  // =====  内部状态 & 回显 end =====

  // ===== 表单上下文与字段名与值读取 begin =====
  const { fieldValue } = useFormFieldWatch(dataField);
  // ===== 表单上下文与字段名与值读取 end =====

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
        initialValue={
          defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? Number(defaultValueConfig?.customValue || 0) : 0
        }
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{String(fieldValue)}</div>
        ) : (
          <div style={{ display: 'flex', alignItems: 'center' }} className='custom-rate'>
            <Rate
              count={rateConfig.max || 5}
              style={{ stroke: rateConfig.iconColor }}
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
            />
            {rateConfig.showResult && (
              <Typography.Text style={{ marginRight: '16px' }}>
                {rateConfig.showCustomTooltips
                  ? rateConfig.tooltips[Math.ceil(fieldValue) - 1]
                  : Array.from({ length: rateConfig.max || 5 }, (_, index) => `${index + 1}`)[
                      Math.ceil(fieldValue) - 1
                    ]}
              </Typography.Text>
            )}
          </div>
        )}
      </Form.Item>
    </div>
  );
});

export default XRate;
