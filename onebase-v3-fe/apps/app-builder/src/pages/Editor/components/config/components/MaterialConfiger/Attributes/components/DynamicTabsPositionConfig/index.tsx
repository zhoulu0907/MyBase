import { Form, Select } from '@arco-design/web-react';
import { getPopupContainer, CONFIG_TYPES } from '@onebase/ui-kit';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const Option = Select.Option;

const DynamicTabsPositionConfig = ({ handlePropsChange, item, configs }: Props) => {
  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <Select
        defaultValue={configs[item.key]}
        getPopupContainer={getPopupContainer}
        onChange={(value) => handlePropsChange(item.key, value)}
      >
        {item.range.map((option: any) => (
          <Option key={option.key} value={option.value}>
            {option.label}
          </Option>
        ))}
      </Select>
    </Form.Item>
  );
};

export default DynamicTabsPositionConfig;

registerConfigRenderer(CONFIG_TYPES.TABS_POSITION, ({ handlePropsChange, item, configs }) => (
  <DynamicTabsPositionConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));