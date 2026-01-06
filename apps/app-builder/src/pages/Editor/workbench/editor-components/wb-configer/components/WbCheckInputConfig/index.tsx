import { Checkbox, Form, Input } from '@arco-design/web-react';
import { WORKBENCH_CONFIG_TYPES, type IWbCheckInputConfigType } from '@onebase/ui-kit';
import { useEffect, useRef, useState } from 'react';
import { registerConfigRenderer } from '../../registry';
import styles from '../../index.module.less';

interface Props {
  handlePropsChange: (key: string, value: unknown) => void;
  item: IWbCheckInputConfigType;
  configs: Record<string, unknown>;
}

const WbCheckInputConfig = ({ handlePropsChange, item, configs }: Props) => {
  const [localTextValue, setLocalTextValue] = useState((configs[item.key] as { text?: string })?.text || '');
  const isInternalUpdateRef = useRef(false);

  const checkboxValue = (configs[item.key] as { display?: boolean })?.display ?? true;

  const textValue = (configs[item.key] as { text?: string })?.text || '';
  useEffect(() => {
    if (!isInternalUpdateRef.current) {
      setLocalTextValue(textValue);
    }
    isInternalUpdateRef.current = false;
  }, [textValue]);

  const handleTextChange = (value: string) => {
    setLocalTextValue(value);
    isInternalUpdateRef.current = true;
    const currentConfig = (configs[item.key] as { text?: string; display?: boolean }) || {};
    handlePropsChange(item.key, { ...currentConfig, text: value });
  };

  const handleCheckboxChange = (checked: boolean) => {
    const currentConfig = (configs[item.key] as { text?: string; display?: boolean }) || {};
    handlePropsChange(item.key, { ...currentConfig, display: checked });
  };

  return (
    <Form.Item
      className={styles.formItem}
      label={
        <>
          {item.name}
          <Checkbox checked={checkboxValue} style={{ float: 'right' }} onChange={handleCheckboxChange}>
            {item.checkboxLabel || '显示标题'}
          </Checkbox>
        </>
      }
    >
      <Input placeholder={`请输入${item.name}`} value={localTextValue} onChange={handleTextChange} />
    </Form.Item>
  );
};

export default WbCheckInputConfig;

registerConfigRenderer(WORKBENCH_CONFIG_TYPES.WB_CHECK_INPUT, ({ handlePropsChange, item, configs }) => (
  <WbCheckInputConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
