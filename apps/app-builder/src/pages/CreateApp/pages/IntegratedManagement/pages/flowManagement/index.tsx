import { useAppStore } from '@/store';
import {
  Button,
  Dropdown,
  Form,
  Input,
  Menu,
  Modal,
  Pagination,
  Radio,
  Select,
  Spin,
  Switch,
  Tabs
} from '@arco-design/web-react';
import { IconApps, IconDown, IconList, IconPlus } from '@arco-design/web-react/icon';
import {
  createFlowMgmt,
  deleteFlowMgmt,
  getFlowMgmt,
  listFlowMgmt,
  ProcessDefinition,
  ProcessStatus,
  updateFlowMgmt,
  type CreateFlowMgmtReq,
  type ListFlowMgmtReq,
  type UpdateFlowMgmtReq
} from '@onebase/app';
import { debounce } from 'lodash-es';
import React, { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import FlowCard from './components/card';
import styles from './index.module.less';

const RadioGroup = Radio.Group;
const FormItem = Form.Item;
const TabPane = Tabs.TabPane;
const Option = Select.Option;

/**
 * 流程管理页面
 * 目前集成触发器编辑器作为主内容
 */
const FlowManagementPage: React.FC = () => {
  const navigate = useNavigate();

  const [createForm] = Form.useForm();
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [createLoading, setCreateLoading] = useState(false);

  const [editForm] = Form.useForm();
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [editLoading, setEditLoading] = useState(false);

  const [loading, setLoading] = useState(false);
  const [searchFlowProccessStatus, setSearchFlowProccessStatus] = useState<ProcessStatus | undefined>(undefined);
  const [searchFlowProcessName, setSearchFlowProccessName] = useState('');
  const [searchTriggerType, setSearchTriggerType] = useState<ProcessDefinition | undefined>(undefined);

  const [pageSize, setPageSize] = useState<number>(8);
  const [pageNo, setPageNo] = useState(1);
  //   TODO
  const [dataList, setDataList] = useState<any[]>();
  const [total, setTotal] = useState(0);
  const { curAppId } = useAppStore();

  useEffect(() => {
    pageSize && curAppId && getFlowMgmtList();
  }, [pageNo, pageSize, curAppId]);

  useEffect(() => {
    pageSize &&
      curAppId &&
      debouncedSearch(curAppId, searchFlowProcessName, searchFlowProccessStatus, searchTriggerType);
  }, [searchFlowProcessName, searchFlowProccessStatus, searchTriggerType]);

  const debouncedSearch = useCallback(
    debounce(
      (
        appId: string,
        processName: string,
        processStatus: ProcessStatus | undefined,
        triggerType: ProcessDefinition | undefined
      ) => {
        getFlowMgmtList(appId, processName, processStatus, triggerType);
      },
      500
    ),
    []
  );

  useEffect(() => {
    return () => debouncedSearch.cancel();
  }, [debouncedSearch]);

  const handleCreateFlow = async () => {
    // navigate('/create-app/integrated-management/flow-management/create');
    try {
      setCreateLoading(true);
      const req: CreateFlowMgmtReq = {
        applicationId: curAppId,
        processName: createForm.getFieldValue('processName'),
        processStatus: createForm.getFieldValue('processStatus') ? ProcessStatus.ENABLED : ProcessStatus.DISABLED,
        processDescription: createForm.getFieldValue('processDescription'),
        processDefinition: createForm.getFieldValue('processDefinition'),
        triggerType: createForm.getFieldValue('triggerType')
      };

      const res = await createFlowMgmt(req);

      setCreateModalVisible(false);
      getFlowMgmtList();
    } catch (error) {
      console.error('创建流程失败:', error);
    } finally {
      setCreateLoading(false);
    }
  };

  const handleEditFlow = async (id: string) => {
    editForm.resetFields();

    const res = await getFlowMgmt(id);
    editForm.setFieldsValue({ id: id });
    editForm.setFieldsValue({ processName: res.processName });
    editForm.setFieldsValue({ processStatus: res.processStatus == ProcessStatus.ENABLED ? true : false });
    editForm.setFieldsValue({ processDescription: res.processDescription });
    editForm.setFieldsValue({ processDefinition: res.processDefinition });
    editForm.setFieldsValue({ triggerType: res.triggerType });

    setEditModalVisible(true);
  };

  const handleUpdateFlowMgmt = async () => {
    try {
      setEditLoading(true);
      const req: UpdateFlowMgmtReq = {
        id: editForm.getFieldValue('id'),
        applicationId: curAppId,
        processName: editForm.getFieldValue('processName'),
        processStatus: editForm.getFieldValue('processStatus') ? ProcessStatus.ENABLED : ProcessStatus.DISABLED,
        processDescription: editForm.getFieldValue('processDescription'),
        processDefinition: editForm.getFieldValue('processDefinition'),
        triggerType: editForm.getFieldValue('triggerType')
      };

      const res = await updateFlowMgmt(req);
      setEditModalVisible(false);
      getFlowMgmtList();
    } catch (error) {
      console.error('更新流程失败:', error);
    } finally {
      setEditLoading(false);
    }
  };

  const handleDeleteFlow = async (id: string) => {
    const res = await deleteFlowMgmt(id);
    getFlowMgmtList();
  };

  const getFlowMgmtList = async (
    appId?: string,
    processName?: string,
    processStatus?: ProcessStatus,
    triggerType?: ProcessDefinition
  ) => {
    setLoading(true);
    const req: ListFlowMgmtReq = {
      applicationId: appId ? appId : curAppId,
      pageNum: pageNo,
      pageSize: pageSize || 8,
      processName: processName ? processName : searchFlowProcessName,
      processStatus: processStatus ? processStatus : searchFlowProccessStatus,
      triggerType: triggerType ? triggerType : searchTriggerType
    };

    const res = await listFlowMgmt(req);
    setDataList(res.list || []);
    setTotal(res.total || 0);
    setLoading(false);
  };

  return (
    <div className={styles.flowManagementPage}>
      <div className={styles.header}>
        <Button
          type="primary"
          icon={<IconPlus />}
          onClick={() => {
            createForm.resetFields();
            setCreateModalVisible(true);
          }}
        >
          新建流程
        </Button>

        <RadioGroup type="button" name="lang" defaultValue="card" style={{ marginRight: 20 }}>
          <Radio value="card">
            <IconList />
          </Radio>
          <Radio value="list">
            <IconApps />
          </Radio>
        </RadioGroup>
      </div>

      <div className={styles.body}>
        <div className={styles.tabs}>
          <Tabs
            onChange={(key) => {
              if (key === 'all') {
                setSearchTriggerType(undefined);
              } else {
                setSearchTriggerType(key as ProcessDefinition);
              }
            }}
          >
            <TabPane key="all" title="全部"></TabPane>
            <TabPane key={ProcessDefinition.FORM} title="表单触发"></TabPane>
            <TabPane key={ProcessDefinition.DATE_FIELD} title="日期触发"></TabPane>
            <TabPane key={ProcessDefinition.Time} title="定时触发"></TabPane>
            <TabPane key={ProcessDefinition.API} title="接口触发"></TabPane>
            <TabPane key={ProcessDefinition.ENTITY} title="实体触发"></TabPane>
          </Tabs>
        </div>
        <div className={styles.content}>
          <div className={styles.searchContainer}>
            <Input.Search
              allowClear
              placeholder="请输入流程名称"
              style={{ width: 240 }}
              onChange={(value) => {
                setSearchFlowProccessName(value);
              }}
            />

            <Dropdown
              droplist={
                <Menu>
                  <Menu.Item key="all_process_status" onClick={() => setSearchFlowProccessStatus(undefined)}>
                    所有状态
                  </Menu.Item>
                  <Menu.Item
                    key="enable_process_status"
                    onClick={() => setSearchFlowProccessStatus(ProcessStatus.ENABLED)}
                  >
                    启用
                  </Menu.Item>
                  <Menu.Item
                    key="disable_process_status"
                    onClick={() => setSearchFlowProccessStatus(ProcessStatus.DISABLED)}
                  >
                    禁用
                  </Menu.Item>
                </Menu>
              }
              position="bl"
            >
              <Button type="text">
                <IconDown />
                {searchFlowProccessStatus
                  ? searchFlowProccessStatus === ProcessStatus.ENABLED
                    ? '启用'
                    : '禁用'
                  : '所有状态'}
              </Button>
            </Dropdown>
          </div>
          <Spin loading={loading} size={40} style={{ width: '100%', height: '100%' }} tip="加载中...">
            <div className={styles.tableContainer}>
              {dataList?.map((item, index) => (
                <FlowCard key={index} data={item} handleEdit={handleEditFlow} handleDelete={handleDeleteFlow} />
              ))}
            </div>
          </Spin>
        </div>
        <div className={styles.footer}>
          <Pagination
            className={styles.myAppPagination}
            total={total}
            current={pageNo}
            pageSize={pageSize}
            onChange={(pNo, pSize) => {
              setPageNo(pNo);
              setPageSize(pSize);
            }}
          />
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
        <Form layout="horizontal" form={createForm}>
          <FormItem field="applicationId" hidden={true} initialValue={curAppId}>
            <Input />
          </FormItem>

          <FormItem label="流程名称" field="processName">
            <Input />
          </FormItem>

          <FormItem label="流程状态" field="processStatus">
            <Switch />
          </FormItem>

          <FormItem label="流程描述" field="processDescription">
            <Input.TextArea placeholder="请输入流程描述" maxLength={100} allowClear />
          </FormItem>

          <FormItem label="流程定义" field="processDefinition">
            <Select>
              <Option value={ProcessDefinition.Time}>时间触发</Option>
              <Option value={ProcessDefinition.FORM}>表单触发</Option>
              <Option value={ProcessDefinition.DATE_FIELD}>日期字段触发</Option>
              <Option value={ProcessDefinition.ENTITY}>实体触发</Option>
              <Option value={ProcessDefinition.API}>API触发</Option>
            </Select>
          </FormItem>

          <FormItem label="触发类型" field="triggerType">
            <RadioGroup>
              <Radio value={'manual'}>手工触发</Radio>
              <Radio value={'auto'}>自动触发</Radio>
            </RadioGroup>
          </FormItem>
        </Form>
      </Modal>

      <Modal
        title="编辑流程"
        visible={editModalVisible}
        onOk={handleUpdateFlowMgmt}
        onCancel={() => setEditModalVisible(false)}
        confirmLoading={editLoading}
        okText="更新"
        cancelText="取消"
      >
        <Form layout="horizontal" form={editForm}>
          <FormItem field="id" hidden={true} initialValue={editForm.getFieldValue('id')}>
            <Input />
          </FormItem>

          <FormItem field="applicationId" hidden={true} initialValue={curAppId}>
            <Input />
          </FormItem>

          <FormItem label="流程名称" field="processName">
            <Input />
          </FormItem>

          <FormItem label="流程状态" field="processStatus" triggerPropName="checked">
            <Switch />
          </FormItem>

          <FormItem label="流程描述" field="processDescription">
            <Input.TextArea placeholder="请输入流程描述" maxLength={100} allowClear />
          </FormItem>

          <FormItem label="流程定义" field="processDefinition">
            <Select>
              <Option value={ProcessDefinition.Time}>时间触发</Option>
              <Option value={ProcessDefinition.FORM}>表单触发</Option>
              <Option value={ProcessDefinition.DATE_FIELD}>日期字段触发</Option>
              <Option value={ProcessDefinition.ENTITY}>实体触发</Option>
              <Option value={ProcessDefinition.API}>API触发</Option>
            </Select>
          </FormItem>

          <FormItem label="触发类型" field="triggerType">
            <RadioGroup>
              <Radio value={'manual'}>手工触发</Radio>
              <Radio value={'auto'}>自动触发</Radio>
            </RadioGroup>
          </FormItem>
        </Form>
      </Modal>
    </div>
  );
};

export default FlowManagementPage;
