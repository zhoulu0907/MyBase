import { Checkbox, Form, Select } from '@arco-design/web-react';
import { getPopupContainer, CONFIG_TYPES } from '@onebase/ui-kit';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const Option = Select.Option;

const securityOptions = [
  { label: '姓名', value: 'name' },
  { label: '手机号', value: 'phone' },
  { label: '邮箱', value: 'email' },
  { label: '金额', value: 'money' },
  { label: '身份证号', value: 'id' },
  { label: '住址', value: 'address' },
  { label: 'IP地址', value: 'ip' },
  { label: '车牌号', value: 'car_id' }
];

const DynamicSecurityConfig = ({ handlePropsChange, item, configs }: Props) => {
  return (
    <Form.Item className={styles.formItem} required label={<>
      {item.name}
      <Checkbox
        checked={configs[item.key]['display']}
        style={{ float: 'right' }}
        onChange={(value) => {
          handlePropsChange(item.key, { ...configs[item.key], display: value });
        }}
      >
        掩码显示
      </Checkbox>
    </>}>
      <Select
        addBefore="掩码方式"
        defaultValue={configs[item.key]['type']}
        getPopupContainer={getPopupContainer}
        onChange={(value) => handlePropsChange(item.key, { ...configs[item.key], type: value })}
      >
        {securityOptions.map((option, index) => (
          <Option key={index} value={option.value}>
            {option.label}
          </Option>
        ))}
      </Select>
    </Form.Item>
  );
};

export default DynamicSecurityConfig;

registerConfigRenderer(CONFIG_TYPES.SECURITY, ({ handlePropsChange, item, configs }) => (
  <DynamicSecurityConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));