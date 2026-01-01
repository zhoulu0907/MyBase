import { Checkbox, Form, Input } from '@arco-design/web-react';
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

const DynamicLabelInputConfig = ({ handlePropsChange, item, configs, isInSubTable }: Props) => {
  const [localValue, setLocalValue] = useState(configs[item.key]?.['text'] || '');
  const isInternalUpdateRef = useRef(false);

  // 当外部 configs 变化时同步到本地状态（非内部更新导致的变化）
  useEffect(() => {
    if (!isInternalUpdateRef.current) {
      setLocalValue(configs[item.key]?.['text'] || '');
    }
    isInternalUpdateRef.current = false;
  }, [configs[item.key]?.['text']]);

  const handleChange = (value: string) => {
    setLocalValue(value);
    isInternalUpdateRef.current = true;
    handlePropsChange(item.key, { ...configs[item.key], text: value });
  };

  return (
    <Form.Item
      className={styles.formItem}
      label={
        <>
          {item.name}
          {!isInSubTable && typeof configs[item.key]?.['display'] === 'boolean' && (
            <Checkbox
              checked={configs[item.key]?.['display']}
              style={{ float: 'right' }}
              onChange={(value) => {
                handlePropsChange(item.key, { ...configs[item.key], display: value });
              }}
            >
              显示标题
            </Checkbox>
          )}
        </>
      }
    >
      <Input placeholder={`请输入${item.name}`} value={localValue} onChange={handleChange} />
    </Form.Item>
  );
};

export default DynamicLabelInputConfig;

registerConfigRenderer(CONFIG_TYPES.LABEL_INPUT, ({ isInSubTable, handlePropsChange, item, configs }) => (
  <DynamicLabelInputConfig
    isInSubTable={isInSubTable}
    handlePropsChange={handlePropsChange}
    item={item}
    configs={configs}
  />
));
