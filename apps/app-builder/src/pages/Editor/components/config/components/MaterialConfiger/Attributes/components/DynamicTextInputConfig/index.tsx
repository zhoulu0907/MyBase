import { Form, Input } from '@arco-design/web-react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const DynamicTextInputConfig = ({ handlePropsChange, item, configs }: Props) => {
  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <Input
        placeholder={`请输入${item.name}`}
        value={configs[item.key]}
        onChange={(value) => {
          handlePropsChange(item.key, value);
        }}
      />
    </Form.Item>
  );
};

export default DynamicTextInputConfig;

registerConfigRenderer(CONFIG_TYPES.TEXT_INPUT, ({ handlePropsChange, item, configs }) => (
  <DynamicTextInputConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

registerConfigRenderer(CONFIG_TYPES.PLACEHOLDER_INPUT, ({ handlePropsChange, item, configs }) => (
  <DynamicTextInputConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

registerConfigRenderer(CONFIG_TYPES.UPLOAD_COMPRESS, ({ handlePropsChange, item, configs }) => (
  <DynamicTextInputConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));