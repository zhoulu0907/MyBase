import { Form, Radio } from '@arco-design/web-react';
import { useI18n } from '@/hooks/useI18n';
import { useEffect } from 'react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const DynamicButtonRadioConfig = ({ handlePropsChange, item, configs }: Props) => {
  const { t } = useI18n();
  const currentValue = configs[item.key];
  const defaultOption = Array.isArray(item.range) ? item.range.find((opt: any) => opt && opt.default === true) : undefined;
  const fallbackValue = currentValue ?? defaultOption?.value;

  useEffect(() => {
    if ((currentValue === undefined || currentValue === null) && defaultOption) {
      handlePropsChange(item.key, defaultOption.value);
    }
  }, [currentValue, defaultOption, item.key]);

  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <Radio.Group
        type="button"
        size="default"
        value={fallbackValue}
        onChange={(value) => {
          handlePropsChange(item.key, value);
        }}
        style={{ width: '100%', display: 'flex' }}
      >
        {item.range.map((option: any) => (
          <Radio
            key={option.key}
            value={option.value}
            style={{ flex: 1, textAlign: 'center', whiteSpace: 'nowrap' }}
          >
            {option.text && option.text.startsWith('formEditor.') ? t(option.text) : option.text}
          </Radio>
        ))}
      </Radio.Group>
    </Form.Item>
  );
};

registerConfigRenderer(CONFIG_TYPES.RADIO_INPUT, ({ handlePropsChange, item, configs }) => (
  <DynamicButtonRadioConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
registerConfigRenderer(CONFIG_TYPES.DATE_TYPE, ({ handlePropsChange, item, configs }) => (
  <DynamicButtonRadioConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
registerConfigRenderer(CONFIG_TYPES.FORM_LAYOUT, ({ handlePropsChange, item, configs }) => (
  <DynamicButtonRadioConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
registerConfigRenderer(CONFIG_TYPES.COLLAPSED, ({ handlePropsChange, item, configs }) => (
  <DynamicButtonRadioConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
registerConfigRenderer(CONFIG_TYPES.TEXT_ALIGN, ({ handlePropsChange, item, configs }) => (
  <DynamicButtonRadioConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
registerConfigRenderer(CONFIG_TYPES.DATA_SELECT_MODE, ({ handlePropsChange, item, configs }) => (
  <DynamicButtonRadioConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
registerConfigRenderer(CONFIG_TYPES.STATUS_RADIO, ({ handlePropsChange, item, configs }) => (
  <DynamicButtonRadioConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

export default DynamicButtonRadioConfig;
