import { useAppStore } from '@/store/store_app';
import {
  Divider,
  Form,
  Input,
  Message,
  Modal,
  Pagination,
  Select,
  Space,
  Spin,
  Typography
} from '@arco-design/web-react';
import { IconLeft, IconPlus, IconRight, IconSearch } from '@arco-design/web-react/icon';
import {
  createApplication,
  deleteApplication,
  listApplication,
  type Application,
  type CreateApplicationReq,
  type DatasourceSaveReqDTO,
  type DeleteApplicationReq,
  type PageParam
} from '@onebase/app';
import { getCommonPaginationList, getRuntimeURL, TENANT_APP_PERMISSION as ACTIONS } from '@onebase/common';
import { debounce } from 'lodash-es';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import emptyApplicationSVG from '@/assets/images/applicationLogo.svg';
import CreateAppModal from '@/components/CreateApp';
import { type Options } from '@/components/CreateApp/const';
import CreateDataSource, { type DataSourceHandle } from '@/components/CreateDataSource';
import AppCard from './components/AppCard';
import { appOptions, calculateMaxItems, createTimeOptions, statusOptions } from './const';
import styles from './index.module.less';
import { PermissionButton as Button } from '@/components/PermissionControl';

const Option = Select.Option;

const AppManagement: React.FC = () => {
  const [form] = Form.useForm();
  const navigate = useNavigate();

  const { tenantId } = useParams();

  const [pageSize, setPageSize] = useState<number>();
  const [pageNo, setPageNo] = useState(1);
  const [dataList, setDataList] = useState<Application[]>();
  const [total, setTotal] = useState(0);
  const [name, setName] = useState('');
  const [loading, setLoading] = useState(false);
  const [ownerTag, setOwnerTag] = useState<0 | 1>(0);
  const [orderByTime, setOrderByTime] = useState<'create' | 'update'>('create');
  const [status, setStatus] = useState<number | string>('');

  const [appName, setAppName] = useState<string>('');
  const [createType, setCreateType] = useState<'app' | 'datasource'>('app');
  const [deleteApp, setDeleteApp] = useState<Application>();
  const [datasource, setDdtasource] = useState<DatasourceSaveReqDTO | undefined>(); // 自有数据源
  const [deleteVisible, setDeleteVisible] = useState<boolean>(false);
  const [createVisible, setCreateVisible] = useState<boolean>(false);
  const [createLoading, setCreateLoading] = useState<boolean>(false);
  const [deleteLoading, setDeleteLoading] = useState<boolean>(false);

  const [applicationEmpty, setAapplicationEmpty] = useState<boolean>(false); // 未创建应用
  const [applicationFilterEmpty, setAapplicationFilterEmpty] = useState<boolean>(false); // 应用列表过滤后为空，此时applicationEmpty为true

  const [currentStep, setCurrentStep] = useState<number>(1); // 创建数据源步骤
  const [dbTypeSelect, setDbTypeSelect] = useState<string>(''); // 数据源类型

  const { setCurAppId } = useAppStore();

  const createDatasourceRef = useRef<DataSourceHandle>(null);
  const appContainerRef = useRef<HTMLDivElement>(null);

  // option dropdown
  const [optionVisibleId, setOptionVisibleId] = useState('');

  useEffect(() => {
    if (!appContainerRef.current) return;
    const containerWidth = appContainerRef.current?.offsetWidth;
    const containerHeight = appContainerRef.current?.offsetHeight;
    const maxAppInfo = calculateMaxItems(containerWidth, containerHeight);
    console.log('maxAppInfo', maxAppInfo, containerWidth, containerHeight);
    setPageSize(maxAppInfo.total || 8);
  }, [appContainerRef.current]);

  useEffect(() => {
    pageSize && getApplicationList();
  }, [pageNo, pageSize, name, orderByTime, status, ownerTag]);

  useEffect(() => {
    setDdtasource(undefined);
  }, []);

  useEffect(() => {
    setCreateType('app');
    setCurrentStep(1);
    setDbTypeSelect('');
  }, [createVisible]);

  useEffect(() => {
    // 只有ownerTag和status会影响应用列表长度
    if ((ownerTag === 0 || status === 0) && dataList?.length === 0) {
      setAapplicationEmpty(true);
    } else {
      setAapplicationEmpty(false);
    }
    if ((ownerTag === 1 || status === 1) && dataList?.length === 0) {
      setAapplicationEmpty(false);
      setAapplicationFilterEmpty(true);
    } else {
      setAapplicationFilterEmpty(false);
    }
  }, [ownerTag, status, dataList]);

  const getApplicationList = async () => {
    setLoading(true);
    const req: PageParam = {
      pageNo,
      pageSize: pageSize || 8,
      name,
      ownerTag,
      orderByTime,
      status: status === '' ? null : Number(status)
    };
    const res = await getCommonPaginationList(listApplication, req, setPageNo);
    if (res) {
      setDataList(res.list || []);
      setTotal(res.total || 0);
      setLoading(false);
    }
  };

  const debouncedUpdate = useCallback(
    debounce((value) => {
      setName(value);
      setPageNo(1);
    }, 500),
    []
  );

  useEffect(() => {
    return () => debouncedUpdate.cancel();
  }, [debouncedUpdate]);

  const handleSearchChange = (value: string) => {
    debouncedUpdate(value);
  };

  /* 创建应用 */
  const handleCreateApp = async () => {
    try {
      const values = await form.validate(); // 等待校验完成并返回数据
      setCreateLoading(true);
      const { appCode, appName, iconColor, iconName, description, tagIds, themeColor, publishModel } = values;

      const params: CreateApplicationReq = {
        appCode,
        appMode: 'classic',
        appName,
        description,
        iconColor,
        iconName,
        tagIds: tagIds?.map((t: Options) => t.value),
        themeColor,
        datasourceSaveReq: datasource,
        publishModel: publishModel ? publishModel : 'inner'
      };
      const res = await createApplication(params);
      setCreateVisible(false);
      Message.success('应用创建成功');
      form.resetFields();
      navigate(`/onebase/${tenantId}/home/create-app/data-factory?appId=${res.id}`);
    } catch (error) {
      return null;
    } finally {
      setCreateLoading(false);
    }
  };

  /* 删除应用 */
  const handleDeleteApp = async () => {
    if (appName !== deleteApp?.appName) {
      Message.warning('请输入正确的应用名称');
      return;
    }
    try {
      setDeleteLoading(true);
      const params: DeleteApplicationReq = {
        id: deleteApp?.id,
        name: appName
      };
      const res = await deleteApplication(params);
      if (res) {
        Message.success('删除成功');
        getApplicationList();
      }
    } finally {
      setAppName('');
      setDeleteLoading(false);
      setDeleteVisible(false);
    }
  };

  const nagivateToDataFactory = (appId: string) => {
    setCurAppId(appId);
    const newWindow = window.open('', '_blank');
    if (newWindow) {
      const baseUrl = getBaseUrl();
      const href = `${baseUrl}home/create-app/data-factory?appId=${appId}`;
      newWindow.location.href = href;
    }
  };

  const nagivateToRuntimeApp = (appId: string) => {
    const appUrl = `${getRuntimeURL()}/#/onebase/runtime/?appId=${appId}&tenantId=${tenantId}`;

    const newWindow = window.open('', '_blank');
    if (newWindow) {
      newWindow.location.href = `${getRuntimeURL()}/#/login?redirectURL=${appUrl}`;
    }
  };

  const getBaseUrl = () => {
    const baseUrl = window.location.href.replace(/setting.*$/, '');
    return baseUrl;
  };

  const handleOptionVisibleChange = (v: boolean, id: string) => {
    setOptionVisibleId(v ? id : '');
  };

  const handleEdit = (appId: string) => {
    nagivateToDataFactory(appId);
  };

  const handleLaunch = (appId: string) => {
    nagivateToRuntimeApp(appId);
  };

  const handleDelete = (item: Application) => {
    setAppName('');
    setDeleteApp(item);
    setDeleteVisible(true);
  };

  return (
    <div className={styles.appPage}>
      <div className={styles.appContainer}>
        <div className={styles.appHasDataBox}>
          <div
            className={styles.appFilter}
            style={{
              pointerEvents: applicationEmpty ? 'auto' : 'unset'
            }}
          >
            <Button
              type="primary"
              size="large"
              permission={ACTIONS.CREATE}
              icon={<IconPlus fontSize={16} />}
              onClick={() => {
                setCreateVisible(true);
              }}
            >
              创建应用
            </Button>

            {/* 筛选下拉框 */}
            <div>
              <Select
                placeholder="全部应用"
                bordered={false}
                style={{ width: 100 }}
                value={ownerTag}
                onChange={(value) => setOwnerTag(value)}
              >
                {appOptions.map((option, index) => (
                  <Option key={index} value={option.value}>
                    {option.label}
                  </Option>
                ))}
              </Select>
              <Divider type="vertical" />
              <Select
                placeholder="按创建时间排序"
                bordered={false}
                style={{ width: 138 }}
                onChange={(value) => setOrderByTime(value)}
                value={orderByTime}
              >
                {createTimeOptions.map((option, index) => (
                  <Option key={index} value={option.value}>
                    {option.label}
                  </Option>
                ))}
              </Select>
              <Divider type="vertical" />
              <Select
                placeholder="全部状态"
                bordered={false}
                style={{ width: 100 }}
                onChange={(value) => setStatus(value)}
                value={status}
              >
                {statusOptions.map((option, index) => (
                  <Option key={index} value={option.value}>
                    {option.label}
                  </Option>
                ))}
              </Select>

              <Input
                className={styles.appInput}
                allowClear
                suffix={<IconSearch />}
                onChange={handleSearchChange}
                placeholder="搜索"
              />
            </div>
          </div>

          {/* 我的应用列表 */}
          <Spin className={styles.appListLoading} loading={loading} size={40} tip="加载中..." ref={appContainerRef}>
            <div className={styles.appList}>
              {(applicationFilterEmpty || applicationEmpty) && !loading && (
                <div className={styles.applicationEmpty}>
                  <img src={emptyApplicationSVG} alt="暂无应用" />
                  <Typography.Text type="secondary">还没有应用</Typography.Text>
                  <Button className={styles.goCreateApplication} permission={ACTIONS.CREATE} onClick={() => setCreateVisible(true)}>
                    去创建
                    <IconRight style={{ marginLeft: '4px' }} />
                  </Button>
                </div>
              )}
              {dataList?.map((item, _index) => (
                <AppCard
                  key={item.id}
                  item={item}
                  optionVisibleId={optionVisibleId}
                  onOptionVisibleChange={handleOptionVisibleChange}
                  onEdit={handleEdit}
                  onLaunch={handleLaunch}
                  onDelete={handleDelete}
                />
              ))}
            </div>
          </Spin>

          <Pagination
            className={styles.appPagination}
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
        title={`确认要删除应用（${deleteApp?.appName ?? ''}）吗？`}
        visible={deleteVisible}
        onOk={handleDeleteApp}
        onCancel={() => setDeleteVisible(false)}
        autoFocus={false}
        focusLock={true}
        confirmLoading={false}
        okButtonProps={{
          loading: deleteLoading,
          disabled: appName?.trim().length === 0,
          style: {
            backgroundColor: '#FF4D4F', // 自定义背景色
            borderColor: '#FF4D4F' // 自定义边框色
          }
        }}
      >
        <div
          style={{
            height: 130,
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'space-between'
          }}
        >
          <div>
            删除应用，其流程、流程数据、表单、列表、模型、权限组等都会删除，请谨慎操作。
            <br />
            <br />
            如确定删除，请输入应用名称：{deleteApp?.appName ?? ''}
          </div>
          <Input value={appName} allowClear placeholder="请输入应用名称" style={{ width: 400 }} onChange={setAppName} />
        </div>
      </Modal>
      <Modal
        title={
          <div style={{ textAlign: 'left' }}>
            {createType === 'app' ? (
              '创建空白应用'
            ) : (
              <div>
                <IconLeft style={{ cursor: 'pointer' }} onClick={() => setCreateType('app')} />
                使用自有数据源
              </div>
            )}
          </div>
        }
        visible={createVisible}
        simple
        unmountOnExit
        footer={
          <Space style={{ display: 'flex', justifyContent: 'space-between' }}>
            <Button
              type="default"
              onClick={() => setCurrentStep(1)}
              style={{ visibility: createType === 'datasource' && currentStep === 2 ? 'visible' : 'hidden' }}
            >
              上一步
            </Button>

            <Space>
              <Button type="default" onClick={() => setCreateVisible(false)} style={{ marginRight: 12 }}>
                取消
              </Button>

              {createType === 'datasource' && currentStep === 1 && (
                <Button type="primary" onClick={() => setCurrentStep(2)} disabled={!dbTypeSelect}>
                  下一步
                </Button>
              )}
              {createType !== 'app' ? (
                <>
                  {currentStep === 2 && (
                    <Button
                      type="primary"
                      onClick={async () => {
                        if (createType === 'datasource') {
                          const res = await createDatasourceRef.current?.handleGetDatasource?.();
                          setDdtasource(res);
                        }
                        setCreateType('app');
                        setCurrentStep(1);
                      }}
                    >
                      完成
                    </Button>
                  )}
                </>
              ) : (
                <Button type="primary" loading={createLoading} onClick={handleCreateApp}>
                  创建
                </Button>
              )}
            </Space>
          </Space>
        }
        confirmLoading={true}
        onCancel={() => setCreateVisible(false)}
        style={{ width: '1300px' }}
        className={styles.createAppModal}
      >
        <div className={styles.createAppWrapper}>
          <CreateAppModal
            form={form}
            status="create"
            previewBgColor="#F2F3F5BF"
            dataSourceCreated={!!datasource}
            onCreateDatasource={() => setCreateType('datasource')}
            style={{
              position: 'absolute',
              transform: createType === 'app' ? 'translateX(0)' : 'translateX(-100%)'
            }}
          />
          <CreateDataSource
            ref={createDatasourceRef}
            currentStep={currentStep}
            dbTypeSelect={dbTypeSelect}
            setDbTypeSelect={setDbTypeSelect}
            style={{
              position: 'absolute',
              padding: '0 200px',
              boxSizing: 'border-box',
              transform: createType === 'datasource' ? 'translateX(0)' : 'translateX(100%)'
            }}
          />
        </div>
      </Modal>
    </div>
  );
};

export default AppManagement;
