import { Form, Radio } from '@arco-design/web-react';
import { useI18n } from '@/hooks/useI18n';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const DynamicColumnCountRadioConfig = ({ handlePropsChange, item, configs }: Props) => {
  const { t } = useI18n();
  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <Radio.Group
        type="button"
        size="default"
        value={configs[item.key]}
        onChange={(value) => {
          handlePropsChange(item.key, value);
        }}
        className={styles.columnCountRadioGroup}
      >
        {item.range.map((option: any) => (
          <Radio key={option.key} value={option.value} className={styles.columnCountRadio}>
            {option.text && option.text.startsWith('formEditor.') ? t(option.text) : option.text}
          </Radio>
        ))}
      </Radio.Group>
    </Form.Item>
  );
};

export default DynamicColumnCountRadioConfig;

registerConfigRenderer(CONFIG_TYPES.COLUMN_COUNT_RADIO, ({ handlePropsChange, item, configs }) => (
  <DynamicColumnCountRadioConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));