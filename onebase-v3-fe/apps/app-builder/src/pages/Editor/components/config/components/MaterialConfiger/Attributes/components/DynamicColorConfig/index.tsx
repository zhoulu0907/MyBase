import { ColorPicker, Form } from '@arco-design/web-react';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicColorConfig = ({ handlePropsChange, item, configs, id }: Props) => {
  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <ColorPicker
        key={`${id}-${item.key}`}
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

registerConfigRenderer(CONFIG_TYPES.COLOR, ({ handlePropsChange, item, configs, id }) => (
  <DynamicColorConfig handlePropsChange={handlePropsChange} item={item} configs={configs} id={id} />
));
