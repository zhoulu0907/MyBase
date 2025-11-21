import { Form, Radio } from '@arco-design/web-react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const DynamicTablePagePositionRadioConfig = ({ handlePropsChange, item, configs }: Props) => {
  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <Radio.Group
        type="button"
        size="large"
        value={configs[item.key]}
        onChange={(value) => {
          handlePropsChange(item.key, value);
        }}
        className={styles.pagePositionRadioGroup}
      >
        {item.range.map((option: any) => (
          <Radio key={option.key} value={option.value} className={styles.pagePositionRadio}>
            {option.text}
          </Radio>
        ))}
      </Radio.Group>
    </Form.Item>
  );
};

export default DynamicTablePagePositionRadioConfig;

registerConfigRenderer(CONFIG_TYPES.TABLE_PAGE_POSITION_RADIO, ({ handlePropsChange, item, configs }) => (
  <DynamicTablePagePositionRadioConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));