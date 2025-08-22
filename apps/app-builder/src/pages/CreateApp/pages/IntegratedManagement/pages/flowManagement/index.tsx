import { Button, Form, Input, Menu, Modal, Radio } from '@arco-design/web-react';
import { IconApps, IconList, IconPlus, IconRobot } from '@arco-design/web-react/icon';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './index.module.less';

const RadioGroup = Radio.Group;
const FormItem = Form.Item;

/**
 * 流程管理页面
 * 目前集成触发器编辑器作为主内容
 */
const FlowManagementPage: React.FC = () => {
  const navigate = useNavigate();
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [createLoading, setCreateLoading] = useState(false);

  const handleCreateFlow = () => {
    navigate('/create-app/integrated-management/flow-management/create');
  };

  return (
    <div className={styles.flowManagementPage}>
      <div className={styles.triggerClassifyContainer}>
        <div className={styles.triggerClassifyContent}>
          <div className={styles.triggerClassifyHeader}>触发器类型分类</div>
          <Menu>
            <Menu.Item key="all">
              <IconRobot /> 所有触发类型
            </Menu.Item>
            <Menu.Item key="form">
              <IconRobot /> 表单触发
            </Menu.Item>
            <Menu.Item key="process">
              <IconRobot /> 流程触发
            </Menu.Item>
            <Menu.Item key="schedule">
              <IconRobot /> 定时触发
            </Menu.Item>
            <Menu.Item key="api">
              <IconRobot /> 接口触发
            </Menu.Item>
            <Menu.Item key="manual">
              <IconRobot /> 手动触发
            </Menu.Item>
          </Menu>
        </div>
      </div>

      <div className={styles.body}>
        <div className={styles.header}>
          <RadioGroup type="button" name="lang" defaultValue="card" style={{ marginRight: 20 }}>
            <Radio value="card">
              <IconList /> 列表
            </Radio>
            <Radio value="list">
              <IconApps />
              卡片
            </Radio>
          </RadioGroup>
          <Button type="primary" icon={<IconPlus />} onClick={() => setCreateModalVisible(true)}>
            新建流程
          </Button>
        </div>
        <div className={styles.content}>
          <div className={styles.searchContainer}>
            <Input.Search allowClear placeholder="请输入流程名称" style={{ width: 240 }} />
          </div>
          <div className={styles.tableContainer}>TODO</div>
        </div>
      </div>

      <Modal
        title="创建流程"
        visible={createModalVisible}
        onOk={handleCreateFlow}
        onCancel={() => setCreateModalVisible(false)}
        confirmLoading={createLoading}
        okText="创建"
        cancelText="取消"
      >
        <Form layout="horizontal">
          <FormItem label="流程名称" field="triggerFlowName">
            <Input />
          </FormItem>
        </Form>
      </Modal>
    </div>
  );
};

export default FlowManagementPage;
