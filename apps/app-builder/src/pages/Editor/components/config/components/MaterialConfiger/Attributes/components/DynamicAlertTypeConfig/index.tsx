import { Form, Select } from '@arco-design/web-react';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import { useEffect, useRef, useState } from 'react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  isInSubTable: boolean;
}



const DynamicAlertTypeConfig = ({ handlePropsChange, item, configs, isInSubTable }: Props) => {
  // 从 configs 中提取值：如果是对象则取 text，如果是字符串则直接使用
  const getValue = (val: any): string => {
    if (typeof val === 'string') return val;
    if (val && typeof val === 'object' && 'text' in val) return val.text;
    return '';
  };

  const [localValue, setLocalValue] = useState(getValue(configs[item.key]));
  const isInternalUpdateRef = useRef(false);

  // 当外部 configs 变化时同步到本地状态（非内部更新导致的变化）
  useEffect(() => {
    if (!isInternalUpdateRef.current) {
      setLocalValue(getValue(configs[item.key]));
    }
    isInternalUpdateRef.current = false;
  }, [configs[item.key]]);

  const handleChange = (value: string) => {
    setLocalValue(value);
    isInternalUpdateRef.current = true;
    // 直接传递字符串值，而不是对象
    handlePropsChange(item.key, value);
  };

  const options = item.range?.map((option: any) => ({
    label: option.text,
    value: option.value
  }));


  return (
    <Form.Item
      className={styles.formItem}
      label={
        <>
          {item.name}
        </>
      }

    >
      <Select options={options}

      placeholder={`请输入${item.name}`} value={localValue} onChange={handleChange} />

    </Form.Item>
  );
};

export default DynamicAlertTypeConfig;

registerConfigRenderer(CONFIG_TYPES.ALERT_TYPE, ({ isInSubTable, handlePropsChange, item, configs }) => (
  <DynamicAlertTypeConfig
    isInSubTable={isInSubTable}
    handlePropsChange={handlePropsChange}
    item={item}
    configs={configs}
  />
));
