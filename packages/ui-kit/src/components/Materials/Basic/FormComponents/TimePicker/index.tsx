import { Form, TimePicker } from '@arco-design/web-react';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES, TIME_FORMAT, DEFAULT_VALUE_TYPES } from '../../../constants';
import '../index.css';
import type { XInputTimePickerConfig } from './schema';
import { getPopupContainer } from '@/utils';

const XTimePicker = memo((props: XInputTimePickerConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const {
    label,
    tooltip,
    status,
    defaultValueConfig,
    timeRange,
    dateType,
    timeType,
    verify,
    layout,
    runtime = true
  } = props;

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
  }

  // 禁用判断,返回的是禁用的数据 禁用的部分小时选项 0-23
  const handelDisabledHours = (): number[] => {
    let disabledHours: number[] = [];
    if (timeRange.earliestLimit && timeRange.earliestValue) {
      const time = timeRange.earliestValue.split(':');
      const minHour = Number(time[0]);
      const minDisabledHours = Array.from({ length: minHour }, (_, i) => {
        return i;
      })
      disabledHours = [...disabledHours, ...minDisabledHours]

    }
    if (timeRange.latestLimit && timeRange.latestValue) {
      const time = timeRange.latestValue.split(':');
      const maxHour = Number(time[0]);
      const maxDisabledHours = Array.from({ length: 24 - maxHour }, (_, i) => {
        return maxHour + i + 1;
      })
      disabledHours = [...disabledHours, ...maxDisabledHours]
    }
    return disabledHours;
  }
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
      if (selectedHour === minHour) {
        const minDisabledMinutes = Array.from({ length: minMinute }, (_, i) => {
          return i;
        })
        disabledMinutes = [...disabledMinutes, ...minDisabledMinutes]
      }
    }
    if (timeRange.latestLimit && timeRange.latestValue) {
      const time = timeRange.latestValue.split(':');
      const maxHour = Number(time[0]);
      const maxMinute = Number(time[1]);
      if (selectedHour === maxHour) {
        const maxDisabledMinutes = Array.from({ length: 60 - maxMinute }, (_, i) => {
          return maxMinute + i + 1;
        })
        disabledMinutes = [...disabledMinutes, ...maxDisabledMinutes]
      }
    }
    return disabledMinutes;
  }
  // 禁用的部分秒数选项	0-59
  const handelDisabledSeconds = (selectedHour: number, selectedMinute: number): number[] => {
    if (!selectedHour && selectedHour !== 0 || !selectedMinute && selectedMinute !== 0) {
      return [];
    }
    let disabledSeconds: number[] = [];
    if (timeRange.earliestLimit && timeRange.earliestValue) {
      const time = timeRange.earliestValue.split(':');
      const minHour = Number(time[0]);
      const minMinute = Number(time[1]);
      const minSecond = Number(time[2]);
      if (selectedHour === minHour && selectedMinute === minMinute) {
        const minDisabledminSeconds = Array.from({ length: minSecond }, (_, i) => {
          return i;
        })
        disabledSeconds = [...disabledSeconds, ...minDisabledminSeconds]
      }
    }
    if (timeRange.latestLimit && timeRange.latestValue) {
      const time = timeRange.latestValue.split(':');
      const maxHour = Number(time[0]);
      const maxMinute = Number(time[1]);
      const maxSecond = Number(time[2]);
      if (selectedHour === maxHour && selectedMinute === maxMinute) {
        const maxDisabledSeconds = Array.from({ length: 60 - maxSecond }, (_, i) => {
          return maxSecond + i + 1;
        })
        disabledSeconds = [...disabledSeconds, ...maxDisabledSeconds]
      }
    }
    console.log('selectedHour', selectedHour)
    console.log('selectedMinute', selectedMinute)
    return disabledSeconds;
  }

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label.display &&
          label.text && <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
        }
        layout={layout}
        tooltip={tooltip}
        wrapperCol={{ style: { flex: 1 } }}
        rules={[{ required: verify?.required }]}
        hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
        style={{
          margin: 0,
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
        initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : ''}
      >
        {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? (
          <div>
            {defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue || '--' : '--'}
          </div>
        ) : (
          // timeType 24小时制
          <TimePicker
            use12Hours={!timeType}
            format={timeType ? TIME_FORMAT[dateType] : `${TIME_FORMAT[dateType]} A`}
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
