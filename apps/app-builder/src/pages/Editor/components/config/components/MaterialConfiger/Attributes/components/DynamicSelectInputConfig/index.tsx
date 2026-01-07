import { Form, Select } from '@arco-design/web-react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const DynamicSelectInputConfig = ({ handlePropsChange, item, configs }: Props) => {
  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <Select
        placeholder={`请选择${item.name}`}
        value={configs[item.key]}
        onChange={(value) => {
          handlePropsChange(item.key, value);
        }}
        options={item.options}
        allowClear
      />
    </Form.Item>
  );
};

export default DynamicSelectInputConfig;

registerConfigRenderer(CONFIG_TYPES.SELECT_INPUT, ({ handlePropsChange, item, configs }) => (
  <DynamicSelectInputConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
