import { Form, Checkbox, TimePicker, Message } from '@arco-design/web-react';
import styles from '../../index.module.less';

export interface DynamicTimeRangeConfigProps {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicTimeRangeConfig: React.FC<DynamicTimeRangeConfigProps> = ({ handlePropsChange, item, configs, id }) => {
  const dateRangeKey = item.key || 'dateRange';

  return (
    <>
      <Form.Item layout="vertical" label={item.name || '可选范围'} className={styles.formItem}>
        {typeof configs[dateRangeKey]['earliestLimit'] === 'boolean' && (
          <div>
            <Checkbox
              checked={configs[dateRangeKey]['earliestLimit']}
              onChange={(value) => {
                handlePropsChange(dateRangeKey, { ...configs[dateRangeKey], earliestLimit: value });
              }}
            >
              最早可选时间
            </Checkbox>
            <TimePicker
              value={configs[dateRangeKey]['earliestValue']}
              format="HH:mm:ss"
              style={{ marginTop: '8px', marginBottom: '8px', width: '100%' }}
              placeholder="请选择"
              onChange={(value) => {
                if (value && configs[dateRangeKey].latestValue) {
                  const latestList = configs[dateRangeKey].latestValue.split(':');
                  const latestSecond =
                    Number(latestList[0]) * 3600 + Number(latestList[1]) * 60 + Number(latestList[1]) * 1;
                  const earliestList = value.split(':');
                  const earliestSecond =
                    Number(earliestList[0]) * 3600 + Number(earliestList[1]) * 60 + Number(earliestList[1]) * 1;
                  if (earliestSecond > latestSecond) {
                    Message.error('最早可选时间不得晚于最晚可选时间');
                    return;
                  }
                }
                handlePropsChange(dateRangeKey, { ...configs[dateRangeKey], earliestValue: value });
              }}
            ></TimePicker>
          </div>
        )}

        {typeof configs[dateRangeKey]['latestLimit'] === 'boolean' && (
          <div>
            <Checkbox
              checked={configs[dateRangeKey]['latestLimit']}
              onChange={(value) => {
                handlePropsChange(dateRangeKey, { ...configs[dateRangeKey], latestLimit: value });
              }}
            >
              最晚可选时间
            </Checkbox>
            <TimePicker
              value={configs[dateRangeKey]['latestValue']}
              placeholder="请选择"
              format="HH:mm:ss"
              style={{ marginTop: '8px', marginBottom: '8px', width: '100%' }}
              onChange={(value) => {
                if (value && configs[dateRangeKey].earliestValue) {
                  const earliestList = configs[dateRangeKey].earliestValue.split(':');
                  const earliestSecond =
                    Number(earliestList[0]) * 3600 + Number(earliestList[1]) * 60 + Number(earliestList[1]) * 1;
                  const latestList = value.split(':');
                  const latestSecond =
                    Number(latestList[0]) * 3600 + Number(latestList[1]) * 60 + Number(latestList[1]) * 1;
                  if (earliestSecond > latestSecond) {
                    Message.error('最早可选时间不得晚于最晚可选时间');
                    return;
                  }
                }
                handlePropsChange(dateRangeKey, { ...configs[dateRangeKey], latestValue: value });
              }}
            ></TimePicker>
          </div>
        )}
      </Form.Item>
    </>
  );
};
export default DynamicTimeRangeConfig;
