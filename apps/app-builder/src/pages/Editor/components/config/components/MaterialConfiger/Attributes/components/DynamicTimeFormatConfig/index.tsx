import { Form, Select, Checkbox } from '@arco-design/web-react';
import { useEffect, useState } from 'react';
import { TIME_OPTIONS, TIME_VALUES, getPopupContainer } from '@onebase/ui-kit';
import styles from '../../index.module.less';

export interface DynamicTimeFormatConfigProps {
  handlePropsChange: (key: string, value: any) => void;
  handleConfigsChange: (config: any) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicTimeFormatConfig: React.FC<DynamicTimeFormatConfigProps> = ({
  handlePropsChange,
  handleConfigsChange,
  item,
  configs,
  id
}) => {
  const dateFormatKey = 'dateType';
  const timeFormatKey = 'timeType';
  const options = [
    { label: '时', value: TIME_VALUES[TIME_OPTIONS.HOUR] },
    { label: '时:分', value: TIME_VALUES[TIME_OPTIONS.MINUTE] },
    { label: '时:分:秒', value: TIME_VALUES[TIME_OPTIONS.SECOND] }
  ];

  const [dateFormat, setDateFormat] = useState<string>('');
  const [timeFormat, setTimeFormat] = useState<boolean>(true);

  useEffect(() => {
    setDateFormat(configs[dateFormatKey]);
  }, [configs[dateFormatKey]]);

  useEffect(() => {
    setTimeFormat(configs[timeFormatKey]);
  }, [configs[timeFormatKey]]);

  return (
    <Form.Item layout="vertical" label={item.name || '时间格式'} className={styles.formItem}>
      <Select
        getPopupContainer={getPopupContainer}
        value={dateFormat}
        style={{ marginBottom: '8px' }}
        onChange={(value) => handlePropsChange(dateFormatKey, value)}
        options={item.range || options}
      ></Select>
      <Checkbox checked={timeFormat} onChange={(value) => handlePropsChange(timeFormatKey, value)}>
        24小时制
      </Checkbox>
    </Form.Item>
  );
};
export default DynamicTimeFormatConfig;
