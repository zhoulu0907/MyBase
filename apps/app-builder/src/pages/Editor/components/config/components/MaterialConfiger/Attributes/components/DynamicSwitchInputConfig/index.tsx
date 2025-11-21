import { Form, Switch } from '@arco-design/web-react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const DynamicSwitchInputConfig = ({ handlePropsChange, item, configs }: Props) => {
  return (
    <Form.Item
      label={<div style={{ textAlign: 'left' }}><span>{item.name}</span></div>}
      labelCol={{ span: 21 }}
      wrapperCol={{ span: 1 }}
      layout="horizontal"
      className={styles.formItem}
    >
      <Switch
        size="small"
        checked={configs[item.key]}
        onChange={(value) => {
          handlePropsChange(item.key, value);
        }}
      />
    </Form.Item>
  );
};

export default DynamicSwitchInputConfig;

registerConfigRenderer(CONFIG_TYPES.SWITCH_INPUT, ({ handlePropsChange, item, configs }) => (
  <DynamicSwitchInputConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));