import { Form, TimePicker } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES, TIME_FORMAT, TIME_12_FORMAT, DEFAULT_VALUE_TYPES } from '../../../constants';
import '../index.css';
import { nanoid } from 'nanoid';
import { FORM_COMPONENT_TYPES } from '../../../componentTypes';
import type { XInputTimePickerConfig } from './schema';
import { getPopupContainer } from '@/utils';
import dayjs from 'dayjs';

const XTimePicker = memo((props: XInputTimePickerConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    tooltip,
    status,
    dataField,
    defaultValueConfig,
    timeRange,
    dateType,
    use24Hours,
    verify,
    layout,
    runtime = true,
    detailMode
  } = props;

  const { form } = Form.useFormContext();
  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.TIME_PICKER}_${nanoid()}`
  const fieldValue = Form.useWatch(fieldId, form);

  // 判断当前 AM PM
  let hourType = '';
  const onSelect = (value: string) => {
    if (value.indexOf('AM') > -1) {
      hourType = 'AM';
    } else if (value.indexOf('PM') > -1) {
      hourType = 'PM';
    } else {
      hourType = '';
    }
  };

  // 禁用判断,返回的是禁用的数据 禁用的部分小时选项 0-23
  const handelDisabledHours = (): number[] => {
    let disabledHours: number[] = [];

    if (timeRange.earliestLimit && timeRange.earliestValue) {
      const time = timeRange.earliestValue.split(':');
      const minHour = Number(time[0]);
      // 12小时制
      if (!use24Hours) {
        if (hourType === 'PM') {
          if (minHour > 12) {
            // 去除本身 额外-1
            const minDisabledHours = Array.from({ length: minHour - 12 - 1 }, (_, i) => {
              return i + 1;
            });
            disabledHours = [...disabledHours, ...minDisabledHours];
          }
        } else {
          // 默认AM
          if (minHour > 0) {
            disabledHours.push(12);
            // 去除本身 额外-1
            const minDisabledHours = Array.from({ length: minHour >= 12 ? 11 : minHour - 1 }, (_, i) => {
              return i + 1;
            });
            disabledHours = [...disabledHours, ...minDisabledHours];
          }
        }
      } else {
        const minDisabledHours = Array.from({ length: minHour }, (_, i) => {
          return i;
        });
        disabledHours = [...disabledHours, ...minDisabledHours];
      }
    }

    if (timeRange.latestLimit && timeRange.latestValue) {
      const time = timeRange.latestValue.split(':');
      const maxHour = Number(time[0]);
      // 12小时制
      if (!use24Hours) {
        if (hourType === 'PM') {
          if (maxHour < 12) {
            // 全部禁用
            disabledHours.push(12);
            const maxDisabledHours = Array.from({ length: 11 }, (_, i) => {
              return i + 1;
            });
            disabledHours = [...disabledHours, ...maxDisabledHours];
          } else if (maxHour === 12) {
            // 禁用1-11
            const maxDisabledHours = Array.from({ length: 11 }, (_, i) => {
              return i + 1;
            });
            disabledHours = [...disabledHours, ...maxDisabledHours];
          } else {
            // 去除本身 额外-1
            const maxDisabledHours = Array.from({ length: 24 - maxHour - 1 }, (_, i) => {
              return maxHour - 12 + i + 1;
            });
            disabledHours = [...disabledHours, ...maxDisabledHours];
          }
        } else {
          // 默认AM
          if (maxHour >= 12) {
            // 全部可用
          } else if (maxHour === 0) {
            // 禁用1-11
            const maxDisabledHours = Array.from({ length: 11 }, (_, i) => {
              return i + 1;
            });
            disabledHours = [...disabledHours, ...maxDisabledHours];
          } else {
            // 去除本身 额外-1
            const maxDisabledHours = Array.from({ length: 12 - 1 - maxHour }, (_, i) => {
              return maxHour + i + 1;
            });
            disabledHours = [...disabledHours, ...maxDisabledHours];
          }
        }
      } else {
        const maxDisabledHours = Array.from({ length: 24 - maxHour }, (_, i) => {
          return maxHour + i + 1;
        });
        disabledHours = [...disabledHours, ...maxDisabledHours];
      }
    }

    // 去重
    return Array.from(new Set(disabledHours));
  };
  // 禁用的部分分钟选项	0-59
  const handelDisabledMinutes = (selectedHour: number): number[] => {
    if (!selectedHour && selectedHour !== 0) {
      return [];
    }
    let disabledMinutes: number[] = [];
    if (timeRange.earliestLimit && timeRange.earliestValue) {
      const time = timeRange.earliestValue.split(':');
      const minHour = Number(time[0]);
      const minMinute = Number(time[1]);
      // 12小时制
      if (!use24Hours && hourType === 'PM') {
        const select24Hour = selectedHour + 12;
        if (select24Hour === minHour) {
          const minDisabledMinutes = Array.from({ length: minMinute }, (_, i) => {
            return i;
          });
          disabledMinutes = [...disabledMinutes, ...minDisabledMinutes];
        }
      } else if (selectedHour === minHour) {
        const minDisabledMinutes = Array.from({ length: minMinute }, (_, i) => {
          return i;
        });
        disabledMinutes = [...disabledMinutes, ...minDisabledMinutes];
      }
    }
    if (timeRange.latestLimit && timeRange.latestValue) {
      const time = timeRange.latestValue.split(':');
      const maxHour = Number(time[0]);
      const maxMinute = Number(time[1]);
      // 12小时制
      if (!use24Hours && hourType === 'PM') {
        const select24Hour = selectedHour + 12;
        if (select24Hour === maxHour) {
          const maxDisabledMinutes = Array.from({ length: 60 - maxMinute }, (_, i) => {
            return maxMinute + i + 1;
          });
          disabledMinutes = [...disabledMinutes, ...maxDisabledMinutes];
        }
      } else if (selectedHour === maxHour) {
        const maxDisabledMinutes = Array.from({ length: 60 - maxMinute }, (_, i) => {
          return maxMinute + i + 1;
        });
        disabledMinutes = [...disabledMinutes, ...maxDisabledMinutes];
      }
    }
    // 去重
    return Array.from(new Set(disabledMinutes));
  };
  // 禁用的部分秒数选项	0-59
  const handelDisabledSeconds = (selectedHour: number, selectedMinute: number): number[] => {
    if ((!selectedHour && selectedHour !== 0) || (!selectedMinute && selectedMinute !== 0)) {
      return [];
    }
    let disabledSeconds: number[] = [];
    if (timeRange.earliestLimit && timeRange.earliestValue) {
      const time = timeRange.earliestValue.split(':');
      const minHour = Number(time[0]);
      const minMinute = Number(time[1]);
      const minSecond = Number(time[2]);
      // 12小时制
      if (!use24Hours && hourType === 'PM') {
        const select24Hour = selectedHour + 12;
        if (select24Hour === minHour && selectedMinute === minMinute) {
          const minDisabledminSeconds = Array.from({ length: minSecond }, (_, i) => {
            return i;
          });
          disabledSeconds = [...disabledSeconds, ...minDisabledminSeconds];
        }
      } else if (selectedHour === minHour && selectedMinute === minMinute) {
        const minDisabledminSeconds = Array.from({ length: minSecond }, (_, i) => {
          return i;
        });
        disabledSeconds = [...disabledSeconds, ...minDisabledminSeconds];
      }
    }
    if (timeRange.latestLimit && timeRange.latestValue) {
      const time = timeRange.latestValue.split(':');
      const maxHour = Number(time[0]);
      const maxMinute = Number(time[1]);
      const maxSecond = Number(time[2]);
      // 12小时制
      if (!use24Hours && hourType === 'PM') {
        const select24Hour = selectedHour + 12;
        if (select24Hour === maxHour && selectedMinute === maxMinute) {
          const maxDisabledSeconds = Array.from({ length: 60 - maxSecond }, (_, i) => {
            return maxSecond + i + 1;
          });
          disabledSeconds = [...disabledSeconds, ...maxDisabledSeconds];
        }
      } else if (selectedHour === maxHour && selectedMinute === maxMinute) {
        const maxDisabledSeconds = Array.from({ length: 60 - maxSecond }, (_, i) => {
          return maxSecond + i + 1;
        });
        disabledSeconds = [...disabledSeconds, ...maxDisabledSeconds];
      }
    }
    // 去重
    return Array.from(new Set(disabledSeconds));
  };

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        field={fieldId ? fieldId : `${FORM_COMPONENT_TYPES.TIME_PICKER}_${nanoid()}`}
        layout={layout}
        tooltip={tooltip}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[{ required: verify?.required, message: `${label.text}是必填项` }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : ''}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
          <div>
            {dayjs(fieldValue).format(use24Hours ? TIME_FORMAT[dateType] : TIME_12_FORMAT[dateType])}
          </div>
        ) : (
          // use24Hours 24小时制
          <TimePicker
            use12Hours={!use24Hours}
            format={use24Hours ? TIME_FORMAT[dateType] : TIME_12_FORMAT[dateType]}
            getPopupContainer={getPopupContainer}
            disabledHours={handelDisabledHours}
            disabledMinutes={handelDisabledMinutes}
            disabledSeconds={handelDisabledSeconds}
            onSelect={onSelect}
            style={{
              width: '100%',
              pointerEvents: runtime ? 'unset' : 'none'
            }}
          />
        )}
      </Form.Item>
    </div>
  );
});

export default XTimePicker;
