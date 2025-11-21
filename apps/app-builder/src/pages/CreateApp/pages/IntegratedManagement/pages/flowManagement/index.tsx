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
  getEntityListByApp,
  getFlowMgmt,
  getPageListByAppId,
  listFlowMgmt,
  ProcessStatus,
  TriggerType,
  updateFlowMgmt,
  type CreateFlowMgmtReq,
  type PageParam,
  type UpdateFlowMgmtReq
} from '@onebase/app';
import { getCommonPaginationList } from '@onebase/common';
import { debounce } from 'lodash-es';
import React, { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import FlowCard from '../../components/card';
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

  const [form] = Form.useForm();
  const [modalVisible, setModalVisible] = useState('');
  const [formLoading, setFormLoading] = useState(false);

  const triggerType = Form.useWatch('triggerType', form);

  const [loading, setLoading] = useState(false);
  const [searchFlowProccessStatus, setSearchFlowProccessStatus] = useState<ProcessStatus | undefined>(undefined);
  const [searchFlowProcessName, setSearchFlowProccessName] = useState('');
  const [searchTriggerType, setSearchTriggerType] = useState<TriggerType | undefined>(undefined);

  const [pageSize, setPageSize] = useState<number>(8);
  const [pageNo, setPageNo] = useState(1);

  const [flowMgmtList, setFlowMgmtList] = useState<any[]>();
  const [total, setTotal] = useState(0);
  const { curAppId } = useAppStore();
  const [pageList, setPageList] = useState<any[]>();
  const [entityList, setEntityList] = useState<any[]>();

  useEffect(() => {
    curAppId && handleGetPageList();
    curAppId && handleGetEntityListByApp();
  }, [curAppId]);

  useEffect(() => {
    pageSize && curAppId && getFlowMgmtList(searchFlowProcessName, searchFlowProccessStatus, searchTriggerType);
  }, [pageNo, pageSize, curAppId, searchFlowProcessName, searchFlowProccessStatus, searchTriggerType]);

  const debouncedSearch = useCallback(
    debounce((processName: string, enableStatus: ProcessStatus | undefined, triggerType: TriggerType | undefined) => {
      getFlowMgmtList(processName, enableStatus, triggerType);
    }, 500),
    []
  );

  useEffect(() => {
    return () => debouncedSearch.cancel();
  }, [debouncedSearch]);

  const toFlowEditor = (appId: string, flowId: string) => {
    navigate(`/onebase/create-app/integrated-management/flow-editor?appId=${appId}&flowId=${flowId}`);
  };

  const handleGetEntityListByApp = async () => {
    const res = await getEntityListByApp(curAppId);
    console.log('entityList: ', res);
    setEntityList(res);
  };

  const handleGetPageList = async () => {
    const res = await getPageListByAppId({ appId: curAppId });
    console.log('pageList: ', res);
    setPageList(res.pages);
  };

  const handleCreateFlow = async () => {
    try {
      setFormLoading(true);

      await form.validate();

      const req: CreateFlowMgmtReq = {
        applicationId: curAppId,
        processName: form.getFieldValue('processName'),
        // 默认禁用
        enableStatus: ProcessStatus.DISABLED,
        processDescription: form.getFieldValue('processDescription') || '',
        triggerType: form.getFieldValue('triggerType'),
        triggerConfig: {
          pageId: form.getFieldValue('pageId') || undefined,
          entityId: form.getFieldValue('entityId') || undefined
        }
      };

      const res = await createFlowMgmt(req);
      console.log('创建流程成功:', res);

      toFlowEditor(curAppId, res);

      setModalVisible('');
      getFlowMgmtList();
    } catch (error: any) {
      console.error('创建流程失败:', error.errors);
    } finally {
      setFormLoading(false);
    }
  };

  const handleEditFlow = async (id: string) => {
    form.resetFields();

    const res = await getFlowMgmt(id);
    form.setFieldsValue({ id: id });
    form.setFieldsValue({ processName: res.processName });
    form.setFieldsValue({ enableStatus: res.enableStatus == ProcessStatus.ENABLED ? true : false });
    form.setFieldsValue({ processDescription: res.processDescription });
    form.setFieldsValue({ triggerType: res.triggerType });

    res.triggerConfig && res.triggerConfig.pageId && form.setFieldsValue({ pageId: res.triggerConfig.pageId });
    res.triggerConfig && res.triggerConfig.entityId && form.setFieldsValue({ entityId: res.triggerConfig.entityId });

    setModalVisible('update');
  };

  const handleUpdateFlowMgmt = async () => {
    try {
      setFormLoading(true);

      await form.validate();

      const req: UpdateFlowMgmtReq = {
        id: form.getFieldValue('id'),
        applicationId: curAppId,
        processName: form.getFieldValue('processName'),
        enableStatus: form.getFieldValue('enableStatus') ? ProcessStatus.ENABLED : ProcessStatus.DISABLED,
        processDescription: form.getFieldValue('processDescription') || '',
        triggerType: form.getFieldValue('triggerType'),
        triggerConfig: {
          pageId: form.getFieldValue('pageId') || undefined,
          entityId: form.getFieldValue('entityId') || undefined
        }
      };

      await updateFlowMgmt(req);
      setModalVisible('');
      getFlowMgmtList();
    } catch (error: any) {
      console.error('更新流程失败:', error.errors);
    } finally {
      setFormLoading(false);
    }
  };

  const handleDeleteFlow = async (id: string) => {
    try {
      await deleteFlowMgmt(id);
    } catch (error: any) {
      console.error('删除流程失败:', error.errors);
    } finally {
      getFlowMgmtList();
    }
  };

  const getFlowMgmtList = async (
    // appId?: string,
    processName?: string,
    enableStatus?: ProcessStatus,
    triggerType?: TriggerType
  ) => {
    setLoading(true);

    const req: PageParam = {
      applicationId: curAppId,
      pageNo: pageNo,
      pageSize: pageSize || 8,
      processName: processName,
      enableStatus: enableStatus,
      triggerType: triggerType
    };
    const res = await getCommonPaginationList(listFlowMgmt, req, setPageNo);
    if (res) {
      // const res = await listFlowMgmt(req);
      setFlowMgmtList(res.list || []);
      setTotal(res.total || 0);
      setLoading(false);
    }
  };

  const getTriggerTypeList = () => {
    return [
      { label: '界面交互触发', value: TriggerType.FORM },
      { label: '表单(实体)触发', value: TriggerType.ENTITY },
      { label: '时间触发', value: TriggerType.TIME },
      { label: '日期字段触发', value: TriggerType.DATE_FIELD },
      { label: 'API触发', value: TriggerType.API }
      // { label: '子流程触发', value: TriggerType.BPM }
    ];
  };

  return (
    <div className={styles.flowManagementPage}>
      <div className={styles.header}>
        <Button
          type="primary"
          icon={<IconPlus />}
          onClick={() => {
            form.resetFields();
            setModalVisible('create');
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
        <div>
          <Tabs
            onChange={(key) => {
              if (key === 'all') {
                setSearchTriggerType(undefined);
              } else {
                setSearchTriggerType(key as TriggerType);
              }
            }}
          >
            <TabPane key="all" title="全部"></TabPane>
            <TabPane key={TriggerType.FORM} title="界面交互触发"></TabPane>
            <TabPane key={TriggerType.ENTITY} title="实体触发"></TabPane>
            <TabPane key={TriggerType.TIME} title="时间触发"></TabPane>
            <TabPane key={TriggerType.DATE_FIELD} title="日期字段触发"></TabPane>
            <TabPane key={TriggerType.API} title="API触发"></TabPane>
            {/* <TabPane key={TriggerType.BPM} title="子流程触发"></TabPane> */}
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
                {searchFlowProccessStatus === ProcessStatus.ENABLED && '启用'}
                {searchFlowProccessStatus === ProcessStatus.DISABLED && '禁用'}
                {searchFlowProccessStatus === undefined && '所有状态'}
              </Button>
            </Dropdown>
          </div>
          <Spin loading={loading} size={40} style={{ width: '100%', height: '100%' }} tip="加载中...">
            <div className={styles.tableContainer}>
              {flowMgmtList?.map((item, index) => (
                <FlowCard
                  key={`flow-${index}`}
                  data={item}
                  handleEdit={handleEditFlow}
                  handleDelete={handleDeleteFlow}
                  refreshList={getFlowMgmtList}
                  toFlowEditor={toFlowEditor}
                />
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
        title={modalVisible == 'create' ? '创建流程' : '更新流程'}
        visible={modalVisible !== ''}
        onOk={modalVisible == 'create' ? handleCreateFlow : handleUpdateFlowMgmt}
        onCancel={() => setModalVisible('')}
        confirmLoading={formLoading}
        okText={modalVisible == 'create' ? '创建' : '更新'}
        cancelText="取消"
      >
        <Form layout="horizontal" form={form}>
          {modalVisible == 'update' && (
            <FormItem field="id" hidden={true} initialValue={form.getFieldValue('id')}>
              <Input />
            </FormItem>
          )}

          <FormItem field="applicationId" hidden={true} initialValue={curAppId}>
            <Input />
          </FormItem>

          <FormItem label="流程名称" field="processName" rules={[{ required: true, message: '请输入流程名称' }]}>
            <Input />
          </FormItem>

          {modalVisible == 'update' && (
            <FormItem label="流程状态" field="enableStatus" triggerPropName="checked">
              <Switch />
            </FormItem>
          )}

          <FormItem label="流程描述" field="processDescription">
            <Input.TextArea placeholder="请输入流程描述" maxLength={100} allowClear />
          </FormItem>

          <FormItem label="流程定义" field="triggerType" rules={[{ required: true, message: '请选择流程定义' }]}>
            <Select disabled={modalVisible == 'update'}>
              {getTriggerTypeList().map((item) => (
                <Option key={item.value} value={item.value}>
                  {item.label}
                </Option>
              ))}
            </Select>
          </FormItem>

          {triggerType == TriggerType.FORM && (
            <FormItem label="表单ID" field="pageId" rules={[{ required: true, message: '请选择表单ID' }]}>
              <Select disabled={modalVisible == 'update'}>
                {pageList?.map((item) => (
                  <Option key={item.id} value={item.id}>
                    {item.pageName}
                  </Option>
                ))}
              </Select>
            </FormItem>
          )}

          {(triggerType == TriggerType.ENTITY || triggerType == TriggerType.DATE_FIELD) && (
            <FormItem label="实体ID" field="entityId" rules={[{ required: true, message: '请选择实体ID' }]}>
              <Select disabled={modalVisible == 'update'}>
                {entityList?.map((item) => (
                  <Option key={item.entityId} value={item.entityId}>
                    {item.entityName}
                  </Option>
                ))}
              </Select>
            </FormItem>
          )}
        </Form>
      </Modal>
    </div>
  );
};

export default FlowManagementPage;
