import { Checkbox, Form, Input } from '@arco-design/web-react';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import { useEffect, useRef, useState } from 'react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  isDivider?: boolean;
}

const DynamicTooltipInputConfig = ({ handlePropsChange, item, configs, isDivider }: Props) => {
  const externalValue = !isDivider ? configs[item.key] : configs[item.key]?.['text'];
  const [localValue, setLocalValue] = useState(externalValue || '');
  const isInternalUpdateRef = useRef(false);

  // 当外部 configs 变化时同步到本地状态（非内部更新导致的变化）
  useEffect(() => {
    if (!isInternalUpdateRef.current) {
      setLocalValue(externalValue || '');
    }
    isInternalUpdateRef.current = false;
  }, [externalValue]);

  const handleChange = (value: string) => {
    setLocalValue(value);
    isInternalUpdateRef.current = true;
    handlePropsChange(item.key, !isDivider ? value : { ...configs[item.key], text: value });
  };

  return (
    <Form.Item
      className={styles.formItem}
      label={
        <>
          {item.name}
          {isDivider && typeof configs[item.key]?.['display'] === 'boolean' && (
            <Checkbox
              checked={configs[item.key]?.['display']}
              style={{ float: 'right' }}
              onChange={(value) => {
                handlePropsChange(item.key, { ...configs[item.key], display: value });
              }}
            >
              显示描述
            </Checkbox>
          )}
        </>
      }
    >
      <Input.TextArea placeholder={`请输入${item.name}`} value={localValue} maxLength={500} onChange={handleChange} />
    </Form.Item>
  );
};

export default DynamicTooltipInputConfig;

registerConfigRenderer(CONFIG_TYPES.TOOLTIP_INPUT, ({ handlePropsChange, item, configs }) => (
  <DynamicTooltipInputConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

registerConfigRenderer(CONFIG_TYPES.DIVIDER_TOOLTIP_INPUT, ({ handlePropsChange, item, configs }) => (
  <DynamicTooltipInputConfig handlePropsChange={handlePropsChange} item={item} configs={configs} isDivider={true} />
));
