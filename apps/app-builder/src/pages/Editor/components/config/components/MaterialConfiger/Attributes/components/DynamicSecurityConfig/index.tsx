import { Checkbox, Form, Input, Select, Modal, Button } from '@arco-design/web-react';
import { IconEdit } from '@arco-design/web-react/icon';
import { getPopupContainer, CONFIG_TYPES, SECURITY_ENCODE_TYPES, securityEncodeText } from '@onebase/ui-kit';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { useState } from 'react';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
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

const DynamicSecurityConfig = ({ handlePropsChange, item, configs }: Props) => {
  const [visible, setVisible] = useState(false);
  const [encode, setEncode] = useState(true);
  const [testValue, setTestValue] = useState('');
  const [securityForm] = Form.useForm();

  // 掩码、解码
  const encodeChange = () => {
    setEncode(!encode);
  };
  const encryptionValue = () => {
    const securityType = securityForm.getFieldValue('type');
    const security = {
      display: true,
      type: securityType
    };
    return securityEncodeText(security, testValue);
  };
  // 弹窗 确定
  const handleConfirm = () => {
    const securityType = securityForm.getFieldValue('type');
    handlePropsChange(item.key, { ...configs[item.key], type: securityType });
    handleCancel();
  };
  // 弹窗 取消
  const handleCancel = () => {
    setVisible(false);
    setEncode(true);
    setTestValue('');
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
              checked={configs[item.key]['display']}
              style={{ float: 'right' }}
              onChange={(value) => {
                handlePropsChange(item.key, { ...configs[item.key], display: value });
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
            configs[item.key]['type']
              ? securityOptions.find((ele) => ele.value === configs[item.key]['type'])?.label
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
          <Form.Item label="掩码方式" field="type" initialValue={configs[item.key]['type'] || undefined}>
            <Select
              getPopupContainer={getPopupContainer}
              renderFormat={(option, value) => {
                const item = securityOptions.find((ele) => ele.value === value);
                // 自定义回显内容
                return (
                  <span style={{ color: '#272E3B' }}>
                    <span>{item?.label}</span>
                    <span> - {item?.describe}</span>
                  </span>
                );
              }}
              dropdownRender={(menu) => (
                <div>
                  <div style={{ borderBottom: '1px solid #e5e6eb', padding: '7px 12px' }}>全部规则</div>
                  {menu}
                </div>
              )}
            >
              {securityOptions.map((item) => (
                <Select.Option key={item.value} value={item.value}>
                  <span style={{ color: '#1D2129' }}>{item.label}</span>
                  <span style={{ color: '#1D2129' }}> - {item.describe}</span>
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item label="掩码测试">
            <div style={{ display: 'flex', alignItems: 'center' }}>
              {encode ? (
                <Input value={testValue} onChange={(value) => setTestValue(value)} />
              ) : (
                <div className="pc-input pc-input-size-default" style={{ height: '32px' }}>
                  {encryptionValue()}
                </div>
              )}

              <Button type="primary" style={{ marginLeft: '8px' }} onClick={encodeChange}>
                {encode ? '掩码' : '解码'}
              </Button>
            </div>
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
};

export default DynamicSecurityConfig;

registerConfigRenderer(CONFIG_TYPES.SECURITY, ({ handlePropsChange, item, configs }) => (
  <DynamicSecurityConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
