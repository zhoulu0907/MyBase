import React, { useEffect, useState } from 'react';
import { Form, Modal, DatePicker, Select } from '@arco-design/web-react';
import { getUserPage, type PageParam } from '@onebase/platform-center';

import dayjs from 'dayjs';
import { agentCreate, agentUpdate } from '@onebase/app/src/services';
/**
 * EditProxyModal 组件
 * 用于页面管理器中重命名弹窗的占位组件
 * 实际弹窗逻辑在 PageManagerPage 中实现
 */
interface ModalProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  initRowData: any;
  fetchProxyList: () => void;
  appId: string | null;
  userInfo: any;
}

const Option = Select.Option;

const EditProxyModal: React.FC<ModalProps> = ({
  visible,
  setVisible,
  initRowData,
  fetchProxyList,
  appId,
  userInfo
}) => {
  const [userOptions, setUserOptions] = useState<any[]>([]);
  let [form] = Form.useForm();
  const handleFormOk = () => {
    form
      .validate()
      .then(async (values) => {
        const agentUser = userOptions.find((user) => user.id === values.agentId);
        if (!initRowData) {
          const params = {
            agentId: values.agentId,
            agentName: agentUser.nickname,
            startTime: dayjs(values.timer[0]).format('YYYY-MM-DD HH:mm:ss'),
            endTime: dayjs(values.timer[1]).format('YYYY-MM-DD HH:mm:ss'),
            appId: appId || '',
            principalId: userInfo.id,
            principalName: userInfo.nickname
          };
          await agentCreate(params);
        } else {
          const params = {
            id: initRowData.id,
            agentId: values.agentId,
            agentName: agentUser.nickname,
            startTime: dayjs(values.timer[0]).format('YYYY-MM-DD HH:mm:ss'),
            endTime: dayjs(values.timer[1]).format('YYYY-MM-DD HH:mm:ss')
          };
          await agentUpdate(params);
        }
        setVisible(false);
        fetchProxyList();
      })
      .catch((error) => {
        console.error('表单验证失败:', error);
      });
  };

  const initUserData = () => {
    const params: PageParam = {
      pageNo: 1,
      pageSize: 100
    };
    getUserPage(params)
      .then((res: any) => {
        setUserOptions(res?.list || []);
      })
      .catch((err: any) => {
        console.info('Api getUserPage Error:', err);
      });
  };

  useEffect(() => {
    initUserData();
    if (initRowData) {
      form.setFieldsValue({
        permissionId: initRowData.principal.userId,
        agentId: initRowData.agent.userId,
        timer: [dayjs(initRowData.startTime), dayjs(initRowData.endTime)]
      });
    } else if (userInfo?.id) {
      form.setFieldsValue({
        permissionId: userInfo.id
      });
    }
  }, []);

  return (
    <Modal
      title={<div style={{ textAlign: 'left' }}>新增代理</div>}
      visible={visible}
      onOk={handleFormOk}
      onCancel={() => {
        setVisible(false);
      }}
      unmountOnExit={true}
    >
      <div>
        <Form layout="vertical" form={form}>
          <Form.Item label="被代理人" field="permissionId" rules={[{ required: true, message: '请选择被代理人' }]}>
            <Select placeholder="请选择被代理人" style={{ width: '100%' }} disabled>
              <Option value={userInfo.id}>{userInfo.nickname}</Option>
            </Select>
          </Form.Item>
          <Form.Item label="代理人" field="agentId" rules={[{ required: true, message: '请选择代理人' }]}>
            <Select placeholder="请选择代理人" style={{ width: '100%' }}>
              {userOptions?.map((option: any) => (
                <Option key={option?.id} value={option?.id}>
                  {option.nickname}
                </Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item label="代理有效期" field="timer" rules={[{ required: true, message: '请选择代理有效期' }]}>
            <DatePicker.RangePicker
              style={{ width: '100%' }}
              disabledDate={(current) => current.isBefore(dayjs().subtract(1, 'day'))}
            />
          </Form.Item>
        </Form>
      </div>
    </Modal>
  );
};

export default EditProxyModal;
