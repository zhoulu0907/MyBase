import { Form, Switch, Input } from '@arco-design/web-react';
import { useEffect, useState } from 'react';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import styles from '../../index.module.less';

export interface DynamicSwitchFillTextConfigProps {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicSwitchFillTextConfig: React.FC<DynamicSwitchFillTextConfigProps> = ({
  handlePropsChange,
  item,
  configs,
  id
}) => {
  const fillTextKey = 'fillText';

  const [fillText, setFillText] = useState({
    display: true,
    checkedText: '',
    uncheckedText: ''
  });

  useEffect(() => {
    setFillText((prev) => ({ ...prev, ...configs[fillTextKey] }));
  }, [configs[fillTextKey]]);

  const handleChange = (key: string, value: boolean | string) => {
    const newConfig = { ...configs[fillTextKey], [key]: value };
    handlePropsChange(fillTextKey, newConfig);
  };

  return (
    <>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '12px' }}>
        <div style={{ flex: 1, marginRight: 'auto' }}>填充文本</div>
        <div>
          <Switch size="small" checked={fillText.display} onChange={(value) => handleChange('display', value)}></Switch>
        </div>
      </div>
      {fillText.display && (
        <>
          <Form.Item layout="inline" label={'开启时'} className={styles.formItem}>
            <Input value={fillText.checkedText} onChange={(value) => handleChange('checkedText', value)} />
          </Form.Item>
          <Form.Item layout="inline" label={'开闭时'} className={styles.formItem}>
            <Input value={fillText.uncheckedText} onChange={(value) => handleChange('uncheckedText', value)} />
          </Form.Item>
        </>
      )}
    </>
  );
};
export default DynamicSwitchFillTextConfig;

registerConfigRenderer(CONFIG_TYPES.SWITCH_FILL_TEXT, ({ id, handlePropsChange, item, configs }) => (
  <DynamicSwitchFillTextConfig id={id} handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
