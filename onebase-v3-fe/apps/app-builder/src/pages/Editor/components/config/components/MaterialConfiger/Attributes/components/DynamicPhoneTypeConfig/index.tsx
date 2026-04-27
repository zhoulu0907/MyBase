import { Form, Radio } from '@arco-design/web-react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const DynamicPhoneTypeConfig = ({ handlePropsChange, item, configs }: Props) => {
  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <Radio.Group
        size="large"
        value={configs[item.key]}
        onChange={(value) => {
          handlePropsChange(item.key, value);
        }}
        className={styles.pagePositionRadioGroup}
        options={item.range || []}
      ></Radio.Group>
    </Form.Item>
  );
};

export default DynamicPhoneTypeConfig;

registerConfigRenderer(CONFIG_TYPES.PHONE_TYPE, ({ handlePropsChange, item, configs }) => (
  <DynamicPhoneTypeConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));