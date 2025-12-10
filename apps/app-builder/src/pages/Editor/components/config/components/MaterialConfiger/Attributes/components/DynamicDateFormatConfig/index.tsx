import { Form, Select } from '@arco-design/web-react';
import { DATE_OPTIONS, DATE_VALUES, getPopupContainer } from '@onebase/ui-kit';
import { useEffect, useState } from 'react';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import styles from '../../index.module.less';

export interface DynamicDateFormatConfigProps {
  handlePropsChange: (key: string, value: any) => void;
  handleConfigsChange: (config: any) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicDateFormatConfig: React.FC<DynamicDateFormatConfigProps> = ({
  handlePropsChange,
  handleConfigsChange,
  item,
  configs,
  id
}) => {
  const options = [
    { label: '年', value: DATE_VALUES[DATE_OPTIONS.YEAR] },
    { label: '年-月', value: DATE_VALUES[DATE_OPTIONS.MONTH] },
    { label: '年-月-日', value: DATE_VALUES[DATE_OPTIONS.DATE] }
    // { label: '年-月-日 时:分:秒', value: DATE_VALUES[DATE_OPTIONS.FULL] },
  ];

  const dateFormatKey = 'dateType';

  const [dateFormat, setDateFormat] = useState<string>('');

  useEffect(() => {
    setDateFormat(configs[dateFormatKey]);
  }, [configs[dateFormatKey]]);

  return (
    <Form.Item layout="vertical" label={item.name || '日期格式'} className={styles.formItem}>
      <Select
        getPopupContainer={getPopupContainer}
        value={dateFormat}
        onChange={(value) => {
          if (configs.defaultValueConfig?.customValue) {
            handleConfigsChange({
              ...configs,
              [dateFormatKey]: value,
              defaultValueConfig: { ...configs.defaultValueConfig, customValue: '' }
            });
          } else {
            handlePropsChange(dateFormatKey, value);
          }
        }}
        options={item.range || options}
      ></Select>
    </Form.Item>
  );
};
export default DynamicDateFormatConfig;

registerConfigRenderer(CONFIG_TYPES.DATE_FORMAT, ({ id, handlePropsChange, handleConfigsChange, item, configs }) => (
  <DynamicDateFormatConfig
    id={id}
    handlePropsChange={handlePropsChange}
    handleConfigsChange={handleConfigsChange}
    item={item}
    configs={configs}
  />
));
