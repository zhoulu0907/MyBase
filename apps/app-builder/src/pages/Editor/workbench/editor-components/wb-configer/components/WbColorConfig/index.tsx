import { Form, ColorPicker } from '@arco-design/web-react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { WORKBENCH_CONFIG_TYPES } from '@onebase/ui-kit';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const WbColorConfig = ({ handlePropsChange, item, configs }: Props) => {
  return (
    <Form.Item
      className={styles.formItem}
      label={item.name}
      layout="horizontal"
      labelCol={{ span: 7 }}
      wrapperCol={{ span: 17 }}
      labelAlign="left"
    >
      <div className={styles.colorPicker}>
        <ColorPicker
          showText={!!configs[item.key]}
          value={configs[item.key]}
          onChange={(value) => {
            handlePropsChange(item.key, value);
          }}
        />
      </div>
    </Form.Item>
  );
};

export default WbColorConfig;

registerConfigRenderer(WORKBENCH_CONFIG_TYPES.WB_COLOR, ({ handlePropsChange, item, configs }) => (
  <WbColorConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
