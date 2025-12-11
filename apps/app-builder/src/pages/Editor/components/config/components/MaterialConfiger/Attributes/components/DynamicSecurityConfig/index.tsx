import { Checkbox, Form, Input, Select, Modal, Button } from '@arco-design/web-react';
import { IconEdit } from '@arco-design/web-react/icon';
import { getPopupContainer, CONFIG_TYPES } from '@onebase/ui-kit';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { useCallback, useEffect, useState } from 'react';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const securityOptions = [
  { label: '姓名', value: 'name', describe: '显示前1个字，后1个字' },
  { label: '手机号', value: 'phone', describe: '显示前3位，后4位' },
  { label: '邮箱', value: 'email', describe: '显示前3位字符，以及@符合和它后面的内容' },
  { label: '金额', value: 'money', describe: '全掩盖，虚拟为5位' },
  { label: '身份证号', value: 'id', describe: '显示后4位' },
  { label: '住址', value: 'address', describe: '显示前4个字，后4个字' },
  { label: 'IP地址', value: 'ip', describe: '显示第1段IP' },
  { label: '车牌号', value: 'car_id', describe: '显示第1个字，后2位' }
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
    if (securityType && testValue) {
      // TODO 掩码展示
      switch (securityType) {
        case 'name':
          // 姓名
          if (testValue.length < 3) {
            return testValue.substring(0, 1) + '*';
          }
          const centerName = testValue.substring(1, testValue.length - 1).replace(/./g, '*');
          return testValue.substring(0, 1) + centerName + testValue.substring(testValue.length - 1);
        case 'phone':
          // 手机号
          if (testValue.length < 7) {
            return testValue;
          }
          const centerPhone = testValue.substring(3, testValue.length - 4).replace(/./g, '*');
          return testValue.substring(0, 3) + centerPhone + testValue.substring(testValue.length - 4);
        case 'email':
          // 邮箱
          const lastIndex = testValue.lastIndexOf('@');
          if (lastIndex !== -1) {
            if (lastIndex <= 3) {
              return testValue.substring(0, lastIndex) + '*' + testValue.substring(lastIndex);
            }
            const centerEmail = testValue.substring(3, lastIndex).replace(/./g, '*');
            return testValue.substring(0, 3) + centerEmail + testValue.substring(lastIndex);
          }
          return testValue;
        case 'money':
          // 金额
          return '*****';
        case 'id':
          // 身份证号
          if (testValue.length < 4) {
            return testValue;
          }
          const centeId = testValue.substring(0, testValue.length - 4).replace(/./g, '*');
          return centeId + testValue.substring(testValue.length - 4);
        case 'address':
          // 住址
          if (testValue.length < 8) {
            return testValue;
          }
          const centeAddress = testValue.substring(0, testValue.length - 4).replace(/./g, '*');
          return testValue.substring(0, 4) + centeAddress + testValue.substring(testValue.length - 4);
        case 'ip':
          // IP地址
          const index = testValue.indexOf('.');
          if (index !== -1) {
            const centerIp = testValue.substring(index).replace(/./g, '*');
            return testValue.substring(0, index) + centerIp;
          }
          return testValue;
        case 'car_id':
          // 车牌号
          if (testValue.length < 4) {
            return testValue.substring(0, 1) + '**';
          }
          const centerCar = testValue.substring(1, testValue.length - 2).replace(/./g, '*');
          return testValue.substring(0, 1) + centerCar + testValue.substring(testValue.length - 2);
        default:
          return '';
      }
    }
    return testValue;
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
