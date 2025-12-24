import { Checkbox, Form, Input, Select, Modal } from '@arco-design/web-react';
import { IconEdit } from '@arco-design/web-react/icon';
import styles from '../styles/index.module.less';
import { useState } from 'react';

// Mock constants
const SECURITY_ENCODE_TYPES = {
  NAME: 'name',
  PHONE: 'phone',
  EMAIL: 'email',
  MONEY: 'money',
  ID: 'id',
  ADDRESS: 'address',
  IP: 'ip',
  CAR_ID: 'car_id'
};

interface Props {
  onChange: (value: any) => void;
  item: any;
  value: any;
}

const securityOptions = [
  { label: '姓名', value: SECURITY_ENCODE_TYPES.NAME, describe: '显示前1个字，后1个字' },
  { label: '手机号', value: SECURITY_ENCODE_TYPES.PHONE, describe: '显示前3位，后4位' },
  { label: '邮箱', value: SECURITY_ENCODE_TYPES.EMAIL, describe: '显示前3位字符，以及@符合和它后面的内容' },
  { label: '金额', value: SECURITY_ENCODE_TYPES.MONEY, describe: '全掩盖，虚拟为5位' },
  { label: '身份证号', value: SECURITY_ENCODE_TYPES.ID, describe: '显示后4位' },
  { label: '住址', value: SECURITY_ENCODE_TYPES.ADDRESS, describe: '显示前4个字，后4个字' },
  { label: 'IP地址', value: SECURITY_ENCODE_TYPES.IP, describe: '显示第1段IP' },
  { label: '车牌号', value: SECURITY_ENCODE_TYPES.CAR_ID, describe: '显示第1个字，后2位' }
];

const DynamicSecurityConfig = ({ onChange, item, value }: Props) => {
  const config = value || {};
  const [visible, setVisible] = useState(false);
  const [securityForm] = Form.useForm();

  // 弹窗 确定
  const handleConfirm = () => {
    const securityType = securityForm.getFieldValue('type');
    onChange({ ...config, type: securityType });
    handleCancel();
  };
  
  // 弹窗 取消
  const handleCancel = () => {
    setVisible(false);
  };

  return (
    <>
      <Form.Item
        className={styles.formItem}
        required
        label={
          <>
            {item.name}
            <Checkbox
              checked={config.display}
              style={{ float: 'right' }}
              onChange={(v) => {
                onChange({ ...config, display: v });
              }}
            >
              掩码显示
            </Checkbox>
          </>
        }
      >
        <Input
          addBefore="掩码方式"
          value={
            config.type
              ? securityOptions.find((ele) => ele.value === config.type)?.label
              : ''
          }
          readOnly
          onClick={() => setVisible(true)}
          suffix={<IconEdit onClick={() => setVisible(true)} />}
        />
      </Form.Item>
      <Modal
        title="掩码显示"
        visible={visible}
        onCancel={handleCancel}
        onOk={handleConfirm}
        maskClosable={false}
        unmountOnExit
      >
        <Form layout="vertical" form={securityForm}>
          <Form.Item label="掩码方式" field="type" initialValue={config.type || undefined}>
            <Select
              renderFormat={(option, value) => {
                return securityOptions.find((ele) => ele.value === value)?.label;
              }}
            >
              {securityOptions.map((option) => (
                <Select.Option key={option.value} value={option.value}>
                  {option.label}
                  <span style={{ color: '#86909C', fontSize: 12, marginLeft: 8 }}>{option.describe}</span>
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
};

export default DynamicSecurityConfig;
