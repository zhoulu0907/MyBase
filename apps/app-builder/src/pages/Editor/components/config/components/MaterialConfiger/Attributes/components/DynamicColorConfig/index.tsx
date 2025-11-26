import { Form, ColorPicker } from '@arco-design/web-react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const DynamicColorConfig = ({ handlePropsChange, item, configs }: Props) => {
  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <ColorPicker
        showText={!!configs[item.key]}
        value={configs[item.key]}
        onChange={(value) => {
          handlePropsChange(item.key, value);
        }}
      />
    </Form.Item>
  );
};

export default DynamicColorConfig;

registerConfigRenderer(CONFIG_TYPES.COLOR, ({ handlePropsChange, item, configs }) => (
  <DynamicColorConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));