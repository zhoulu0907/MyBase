import { Form, Select, Input, Button, Switch } from '@arco-design/web-react';
import { useEffect, useState } from 'react';
import { DEFAULT_VALUE_TYPES, DEFAULT_VALUE_TYPES_LABELS,DATE_EXTREME_TYPE,DATE_DYNAMIC_TYPE } from '@onebase/ui-kit';
import styles from '../../index.module.less';

export interface DynamicDateRangeConfigProps {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicDateRangeConfig: React.FC<DynamicDateRangeConfigProps> = ({
  handlePropsChange,
  item,
  configs,
  id
}) => {
  const dateRangeKey = 'dateRange';

  const [dateRange, setDateRange] = useState({
    weekLimit: false,
      week: [],
      earliestLimit: false,
      earliestType: DATE_EXTREME_TYPE.DYNAMIC,
      earliestStaticValue: '',
      earliestDynamicValue: DATE_DYNAMIC_TYPE.TODAY,
      earliestVariableValue: [],
      latestLimit: false,
      latestType:  DATE_EXTREME_TYPE.DYNAMIC,
      latestStaticValue: '',
      latestDynamicValue: DATE_DYNAMIC_TYPE.TODAY,
      latestVariableValue: []
  });

  useEffect(() => {
    setDateRange((prev) => ({ ...prev, ...configs[dateRangeKey] }));
  }, [configs[dateRangeKey]]);

  const handleChange = (key: string, value: boolean | string) => {
    const newConfig = { ...configs[dateRangeKey], [key]: value };
    handlePropsChange(dateRangeKey, newConfig);
  };

  return (
    <>
      <Form.Item layout="vertical" label={item.name || '可选范围'} className={styles.formItem}>
       
      </Form.Item>
    </>
  );
};
export default DynamicDateRangeConfig;
