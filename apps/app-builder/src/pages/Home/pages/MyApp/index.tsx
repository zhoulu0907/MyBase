import { useI18n } from '@/hooks/useI18n';
import { useAppStore } from '@/store/store_app';
import {
  Button,
  Divider,
  Dropdown,
  Form,
  Input,
  Menu,
  Message,
  Modal,
  Pagination,
  Select,
  Space,
  Spin,
  Tag
} from '@arco-design/web-react';
import { IconEmpty, IconLeft, IconMoreVertical, IconSearch, IconSettings } from '@arco-design/web-react/icon';
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
import { getCommonPaginationList } from '@onebase/common';
import { debounce } from 'lodash-es';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import emptyApplicationSVG from '@/assets/images/empty_application.svg';
import plusSVG from '@/assets/images/plus_icon.svg';
import CreateApp from '@/components/CreateApp';
import { type Options } from '@/components/CreateApp/const';
import CreateDataSource, { type DataSourceHandle } from '@/components/CreateDataSource';
import DynamicIcon from '@/components/DynamicIcon';
import { PermissionButton } from '@/components/PermissionControl';
import { TENANT_DEPT_PERMISSION as ACTIONS } from '@/constants/permission';
import { appIconMap } from '@onebase/ui-kit';
import TagModal from './components/tagModal';
import {
  appOptions,
  calculateMaxItems,
  createTimeOptions,
  defaultTheme,
  statusOptions,
  TagColor,
  ThemeColorMap
} from './const';
import styles from './index.module.less';

const Option = Select.Option;

const MyAppPage: React.FC = () => {
  const [form] = Form.useForm();
  const { t } = useI18n();
  const navigate = useNavigate();

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
  const [tagModalVisible, setTagModalVisible] = useState<boolean>(false);

  const [applicationEmpty, setAapplicationEmpty] = useState<boolean>(false); // 未创建应用
  const [applicationFilterEmpty, setAapplicationFilterEmpty] = useState<boolean>(false); // 应用列表过滤后为空，此时applicationEmpty为true

  const [currentStep, setCurrentStep] = useState<number>(1); // 创建数据源步骤
  const [dbTypeSelect, setDbTypeSelect] = useState<string>(''); // 数据源类型

  const { setCurAppId } = useAppStore();

  const createDatasourceRef = useRef<DataSourceHandle>(null);
  const appContainerRef = useRef<HTMLDivElement>(null);

  // option dropdown
  const [optionVisibleId, setOptionVisibleId] = useState('');
  const timerRef = useRef<number | null>(null);

  useEffect(() => {
    if (!appContainerRef.current) return;
    const containerWidth = appContainerRef.current?.offsetWidth;
    const containerHeight = appContainerRef.current?.offsetHeight;
    const maxAppInfo = calculateMaxItems(containerWidth, containerHeight);
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
      const { appCode, appName, iconColor, iconName, description, tagIds, themeColor } = values;

      const params: CreateApplicationReq = {
        appCode,
        appMode: 'classic',
        appName,
        description,
        iconColor,
        iconName,
        tagIds: tagIds?.map((t: Options) => t.value),
        themeColor,
        datasourceSaveReq: datasource
      };
      const res = await createApplication(params);
      setCreateVisible(false);
      Message.success('应用创建成功');
      form.resetFields();
      navigate(`/onebase/create-app/data-factory?appId=${res.id}`);
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

  /* 跳转到编辑页 */
  const navigateToAppPage = (appId: string) => {
    setCurAppId(appId);
    const newWindow = window.open('', '_blank');
    if (newWindow) {
      const baseUrl = getBaseUrl();
      const href = `${baseUrl}create-app/app-setting?appId=${appId}`;
      newWindow.location.href = href;
    }
  };

  const navigateToDataFactory = (appId: string) => {
    setCurAppId(appId);
    const newWindow = window.open('', '_blank');
    if (newWindow) {
      const baseUrl = getBaseUrl();
      const href = `${baseUrl}create-app/data-factory?appId=${appId}`;
      newWindow.location.href = href;
    }
  };

  const getBaseUrl = () => {
    const baseUrl = window.location.href.replace(/my-app.*$/, '');
    return baseUrl;
  };

  const handleOptionVisibleChange = (v: boolean, id: string) => {
    setOptionVisibleId(v ? id : '');
  };

  const clearTimer = () => {
    if (timerRef.current) {
      window.clearTimeout(timerRef.current);
      timerRef.current = null;
    }
  };
  const startCloseTimer = (delay = 120) => {
    clearTimer();
    timerRef.current = window.setTimeout(() => {
      setOptionVisibleId('');
      timerRef.current = null;
    }, delay);
  };

  const menu = (item: any) => {
    return (
      <Menu onPointerEnter={clearTimer} onPointerLeave={() => startCloseTimer(80)}>
        <Menu.Item
          key="1"
          onClick={(e) => {
            e.stopPropagation();
            navigateToAppPage(item.id);
          }}
        >
          编辑
        </Menu.Item>
        <Menu.Item
          key="2"
          onClick={(e) => {
            e.stopPropagation();
            setDeleteApp(item);
            setDeleteVisible(true);
          }}
        >
          删除
        </Menu.Item>
        {/* <Menu.Item key="3">应用管理</Menu.Item> */}
      </Menu>
    );
  };

  return (
    <div className={styles.myAppPage}>
      <div className={styles.myAppPageHeader}>
        <PermissionButton
          permission={ACTIONS.CREATE}
          type="default"
          size="large"
          icon={<img src={plusSVG} alt="create application" />}
          className={styles.createAppButton}
          onClick={() => setCreateVisible(true)}
          style={{ color: 'rgb(var(--primary-6))' }}
        >
          {t('myApp.createApp')}
        </PermissionButton>
      </div>

      <div className={styles.myAppContainer}>
        <div className={styles.appHasDataBox}>
          <div
            className={styles.myAppFilter}
            style={{
              pointerEvents: applicationEmpty ? 'auto' : 'unset'
            }}
          >
            <Input
              className={styles.myAppInput}
              allowClear
              suffix={<IconSearch />}
              onChange={handleSearchChange}
              placeholder="搜索"
            />

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

              <Button
                type="text"
                icon={<IconSettings />}
                style={{ color: '#21252e' }}
                onClick={() => {
                  setTagModalVisible(true);
                }}
              >
                标签管理
              </Button>
            </div>
          </div>

          {/* 我的应用列表 */}
          <Spin
            loading={loading}
            size={40}
            style={{ width: '100%', height: '100%' }}
            tip="加载中..."
            ref={appContainerRef}
          >
            <div className={styles.myAppList}>
              {applicationEmpty && !loading && (
                <div className={styles.applicationEmpty}>
                  <img src={emptyApplicationSVG} alt="暂无应用" />
                  <div className={styles.goCreateApplication} onClick={() => setCreateVisible(true)} />
                </div>
              )}
              {applicationFilterEmpty && !loading && (
                <div className={styles.applicationEmpty}>
                  <IconEmpty fontSize={56} />
                  暂无数据
                </div>
              )}
              {dataList?.map((item, index) => (
                <div className={styles.myAppCard} key={index}>
                  <div className={styles.myAppCardTop}>
                    <div className={styles.myAppCardHeader}>
                      <div className={styles.myAppName}>
                        <div className={styles.myAppIcon} style={{ backgroundColor: item.iconColor }}>
                          <DynamicIcon
                            IconComponent={appIconMap[item.iconName as keyof typeof appIconMap]}
                            theme="outline"
                            size="32"
                            fill="#F2F3F5"
                          />
                        </div>
                        <div className={styles.myAppCardInfo}>
                          <div className={styles.infoHeader}>
                            <div className={styles.myAppTitle}>{item.appName}</div>
                            {/* TODO */}
                            {/* <Tag color={TagColor[item.appStatus]} className={styles.tag}>
                              SaaS模式
                            </Tag> */}
                          </div>
                          <Tag color={TagColor[item.appStatus]} className={styles.tag}>
                            {item.appStatusText}
                          </Tag>
                          <Divider type="vertical" style={{ margin: '0 4px 0 6px', height: '8px' }} />
                          <span className={styles.versionNumber}>{item.versionNumber}</span>
                        </div>
                      </div>
                      <Dropdown
                        droplist={menu(item)}
                        trigger="click"
                        position="bottom"
                        popupVisible={optionVisibleId === item.id}
                        onVisibleChange={(v) => handleOptionVisibleChange(v, item.id)}
                        getPopupContainer={(node) => node.parentNode as HTMLElement}
                      >
                        <Button
                          type="text"
                          className={styles.optionbtn}
                          onPointerEnter={clearTimer}
                          onPointerLeave={() => startCloseTimer(80)}
                        >
                          <IconMoreVertical style={{ color: '#272e3b' }} />
                        </Button>
                      </Dropdown>
                    </div>

                    <div className={styles.myAppCardBody}>
                      <div className={styles.myAppDesc}>{item.description ?? '该应用暂无介绍。'}</div>
                      <div className={styles.myAppTags}>
                        {item.tags?.map((tag: { id: string; tagName: string }) => (
                          <Tag
                            key={tag.id}
                            style={{
                              color: item.themeColor || defaultTheme,
                              height: '22px',
                              backgroundColor: ThemeColorMap[item.themeColor ?? defaultTheme]
                            }}
                          >
                            {tag.tagName}
                          </Tag>
                        ))}
                      </div>
                    </div>
                  </div>
                  <div className={styles.myAppCardFooter}>
                    <Button
                      className={styles.footerBtn}
                      type="outline"
                      long
                      onClick={() => {
                        navigateToDataFactory(item.id);
                      }}
                    >
                      进入应用
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          </Spin>

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
        title="确认删除应用"
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
            height: 171,
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'space-between'
          }}
        >
          <div>
            卸载应用，其流程、流程数据、表单、列表、模型、权限组等都会删除，操作需谨慎。
            <br />
            <br />
            为防止误操作，如确定删除，请输入
            <strong>&quot;&lt;应用名称&gt;&quot;</strong>进行确认：
          </div>
          <Input
            value={appName}
            allowClear
            placeholder="请输入要删除的应用名称"
            style={{ width: 476 }}
            onChange={setAppName}
          />
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
            <Button type="default" onClick={() => setCurrentStep(1)} style={{ visibility: createType === 'datasource' && currentStep === 2 ? 'visible' : 'hidden' }}>
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
                  {currentStep === 2 && <Button type="primary" onClick={async () => {
                    if (createType === 'datasource') {
                      const res = await createDatasourceRef.current?.handleGetDatasource?.();
                      setDdtasource(res);
                    }
                    setCreateType('app');
                    setCurrentStep(1);
                  }}>
                    完成
                  </Button>}
                </>
              )
                : (
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
          <CreateApp
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
      <TagModal
        visible={tagModalVisible}
        onOk={() => {
          setTagModalVisible(false);
        }}
        onCancel={() => {
          setTagModalVisible(false);
        }}
      />
    </div>
  );
};

export default MyAppPage;
