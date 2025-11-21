import { Form, InputNumber } from '@arco-design/web-react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const DynamicNumberInputConfig = ({ handlePropsChange, item, configs }: Props) => {
  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <InputNumber
        placeholder={`请输入${item.name}`}
        value={configs[item.key]}
        min={item.min}
        max={item.max}
        onChange={(value) => {
          if (value >= 0) {
            handlePropsChange(item.key, value);
          }
        }}
      />
    </Form.Item>
  );
};

export default DynamicNumberInputConfig;

registerConfigRenderer(CONFIG_TYPES.NUMBER_INPUT, ({ handlePropsChange, item, configs }) => (
  <DynamicNumberInputConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

registerConfigRenderer(CONFIG_TYPES.LABEL_COL_SPAN, ({ handlePropsChange, item, configs }) => (
  <DynamicNumberInputConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));