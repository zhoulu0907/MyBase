import { Form, Radio } from '@arco-design/web-react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const DynamicRadioDataConfig = ({ handlePropsChange, item, configs }: Props) => {
  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <Radio.Group
        options={item.options}
        value={configs[item.key]}
        onChange={(value) => {
          handlePropsChange(item.key, value);
        }}
        type='button'
      />
    </Form.Item>
  );
};

export default DynamicRadioDataConfig;

registerConfigRenderer(CONFIG_TYPES.RADIO_DATA, ({ handlePropsChange, item, configs }) => (
  <DynamicRadioDataConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
