import { Form, Select } from '@arco-design/web-react';
import { useEffect, useState } from 'react';
import { TIME_OPTIONS, TIME_VALUES } from '@onebase/ui-kit';
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
  const options = [
    { label: '时', value: TIME_VALUES[TIME_OPTIONS.HOUR] },
    { label: '时:分', value: TIME_VALUES[TIME_OPTIONS.MINUTE] },
    { label: '时:分:秒', value: TIME_VALUES[TIME_OPTIONS.SECOND] }
  ];

  const [dateFormat, setDateFormat] = useState<string>('');

  useEffect(() => {
    setDateFormat(configs[dateFormatKey]);
  }, [configs[dateFormatKey]]);

  return (
    <Form.Item layout="vertical" label={item.name || '时间格式'} className={styles.formItem}>
      <Select
        value={dateFormat}
        onChange={(value) => handlePropsChange(dateFormatKey, value)}
        options={item.range || options}
      ></Select>
    </Form.Item>
  );
};
export default DynamicTimeFormatConfig;
