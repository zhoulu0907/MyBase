import { Form, Input } from '@arco-design/web-react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const DynamicTablePageSizeConfig = ({ handlePropsChange, item, configs }: Props) => {
  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <Input
        type="number"
        size="large"
        value={configs[item.key]}
        onChange={(value) => {
          if (!value) return;
          handlePropsChange(item.key, value);
        }}
      />
    </Form.Item>
  );
};

export default DynamicTablePageSizeConfig;

registerConfigRenderer(CONFIG_TYPES.TABLE_PAGE_SIZE, ({ handlePropsChange, item, configs }) => (
  <DynamicTablePageSizeConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));