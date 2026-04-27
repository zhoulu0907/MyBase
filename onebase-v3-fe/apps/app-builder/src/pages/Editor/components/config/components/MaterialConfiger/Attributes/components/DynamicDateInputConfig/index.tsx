import { Form, DatePicker } from '@arco-design/web-react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
}

const DynamicDateInputConfig = ({ handlePropsChange, item }: Props) => {
  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <DatePicker
        showTime={{ defaultValue: '00:00:00' }}
        format="YYYY-MM-DD HH:mm:ss"
        onChange={(value) => {
          handlePropsChange(item.key, value);
        }}
        style={{ width: '100%' }}
      />
    </Form.Item>
  );
};

export default DynamicDateInputConfig;

registerConfigRenderer(CONFIG_TYPES.DATE_INPUT, ({ handlePropsChange, item }) => (
  <DynamicDateInputConfig handlePropsChange={handlePropsChange} item={item} />
));