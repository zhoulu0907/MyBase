import { DatePicker, Form } from '@arco-design/web-react';
import { nanoid } from 'nanoid';
import { memo, useEffect, useState } from 'react';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import {
  DATE_OPTIONS,
  DATE_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES
} from '../../../constants';
import type { XInputDatePickerConfig } from './schema';
import { getPopupContainer, securityEncodeText } from '@/utils';
import dayjs from 'dayjs';
import { handelDisabledDate } from '../date';
import '../index.css';

const { YearPicker, MonthPicker } = DatePicker;
const XDatePicker = memo((props: XInputDatePickerConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    dataField,
    tooltip,
    status,
    defaultValueConfig,
    verify,
    dateType,
    dateRange,
    layout,
    runtime = true,
    detailMode,
    security
  } = props;

  const { form } = Form.useFormContext();
  const fieldId =
    dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DATE_PICKER}_${nanoid()}`;
  const fieldValue = Form.useWatch(fieldId, form);

  // 根据日期类型渲染对应的日期选择器
  const renderDatePicker = () => {
    const styles = {
      width: '100%',
      pointerEvents: (runtime ? 'auto' : 'none') as React.CSSProperties['pointerEvents']
    };
    switch (dateType) {
      case DATE_VALUES[DATE_OPTIONS.YEAR]:
        return (
          <YearPicker
            style={styles}
            disabledDate={(current) => {
              return handelDisabledDate(current, dateRange, form)
            }}
            format="YYYY"
            getPopupContainer={getPopupContainer}
          />
        );
      case DATE_VALUES[DATE_OPTIONS.MONTH]:
        return (
          <MonthPicker
            style={styles}
            disabledDate={(current) => {
              return handelDisabledDate(current, dateRange, form)
            }}
            format="YYYY-MM"
            getPopupContainer={getPopupContainer}
          />
        );
      case DATE_VALUES[DATE_OPTIONS.DATE]:
        return (
          <DatePicker
            style={styles}
            disabledDate={(current) => {
              return handelDisabledDate(current, dateRange, form)
            }}
            format="YYYY-MM-DD"
            getPopupContainer={getPopupContainer}
          />
        );
      case DATE_VALUES[DATE_OPTIONS.FULL]:
        return (
          <DatePicker
            showTime
            style={styles}
            disabledDate={(current) => {
              return handelDisabledDate(current, dateRange, form)
            }}
            format="YYYY-MM-DD HH:mm:ss"
            getPopupContainer={getPopupContainer}
          />
        );
      default:
        // 默认显示日期选择器
        return <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />;
    }
  };

  const renderTime = () => {
    switch (dateType) {
      case DATE_VALUES[DATE_OPTIONS.YEAR]:
        return <>{securityEncodeText(security, dayjs(fieldValue).format('YYYY'))}</>;
      case DATE_VALUES[DATE_OPTIONS.MONTH]:
        return <>{securityEncodeText(security, dayjs(fieldValue).format('YYYY-MM'))}</>;
      case DATE_VALUES[DATE_OPTIONS.DATE]:
        return <>{securityEncodeText(security, dayjs(fieldValue).format('YYYY-MM-DD'))}</>;
      case DATE_VALUES[DATE_OPTIONS.FULL]:
        return <>{securityEncodeText(security, dayjs(fieldValue).format('YYYY-MM-DD HH:mm:ss'))}</>;
      default:
        // 默认显示日期选择器
        return <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />;
    }
  };

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldId ? fieldId : `${FORM_COMPONENT_TYPES.DATE_PICKER}_${nanoid()}`}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={[{ required: verify?.required, message: `${label.text}是必填项` }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultValueConfig.customValue}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>{fieldValue ? renderTime() : '--'}</div>
        ) : (
          renderDatePicker()
        )}
      </Form.Item>
    </div>
  );
});

export default XDatePicker;
