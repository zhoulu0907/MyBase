import { useState } from 'react';
import { Form, InputNumber, Select } from '@arco-design/web-react';
import { CONFIG_TYPES, getPopupContainer } from '@onebase/ui-kit';
import { registerConfigRenderer } from '../../registry';
import styles from '../../index.module.less';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const Option = Select.Option;

const DynamicColumnGapConfig = ({ handlePropsChange, item, configs }: Props) => {
  const hideInputNumberValue = item.range.map(v => v.value).includes(configs[item.key]);
  const [value, setValue] = useState<string>(hideInputNumberValue ? '' : configs[item.key]);

  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <Select
        value={configs[item.key]}
        getPopupContainer={getPopupContainer}
        onChange={(value) => {
          setValue('');
          handlePropsChange(item.key, value);
        }}
        dropdownRender={(menu) => (
          <div>
            {menu}
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                padding: '7px 12px'
              }}
            >
              <InputNumber
                size='mini'
                min={0}
                max={100}
                value={Number.parseFloat(value)}
                placeholder='请输入'
                style={{ marginRight: 8 }}
                onChange={(value) => {
                  setValue(value + 'px');
                  handlePropsChange(item.key, value + 'px');
                }}
              />
              px
            </div>
          </div>
        )}
        dropdownMenuStyle={{ height: 120 }}
      >
        {item.range.map((option: any) => (
          <Option key={option.key} value={option.value}>
            {option.text}
          </Option>
        ))}
      </Select>
    </Form.Item>
  );
};

export default DynamicColumnGapConfig;

registerConfigRenderer(CONFIG_TYPES.COLUMN_GAP_SELECT, ({ handlePropsChange, item, configs }) => (
  <DynamicColumnGapConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));