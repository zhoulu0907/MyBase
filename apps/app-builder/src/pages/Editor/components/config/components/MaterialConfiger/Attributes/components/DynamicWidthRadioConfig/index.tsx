import { Form, Radio } from '@arco-design/web-react';
import { useI18n } from '@/hooks/useI18n';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';

interface Props {
  handleLayoutChange: (key: string, value: string) => void;
  item: any;
  configs: any;
}

const DynamicWidthRadioConfig = ({ handleLayoutChange, item, configs }: Props) => {
  const { t } = useI18n();
  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <Radio.Group
        type="button"
        direction="horizontal"
        size="mini"
        value={configs[item.key]}
        onChange={(value) => {
          handleLayoutChange(item.key, value);
        }}
      >
        {item.range.map((option: any) => (
          <Radio key={option.key} value={option.value} className={styles.widthRadio}>
            {option.text && option.text.startsWith('editor.') ? t(option.text) : option.text}
          </Radio>
        ))}
      </Radio.Group>
    </Form.Item>
  );
};

export default DynamicWidthRadioConfig;

registerConfigRenderer(CONFIG_TYPES.WIDTH_RADIO, ({ handleLayoutChange, item, configs }) => (
  <DynamicWidthRadioConfig handleLayoutChange={handleLayoutChange} item={item} configs={configs} />
));