import { useI18n } from '@/hooks/useI18n';
import { useAppStore } from '@/store/store_app';
import {
  Button,
  Divider,
  Dropdown,
  Input,
  Link,
  Menu,
  Pagination,
  Select,
  Spin,
  Tag,
  Tooltip,
  Typography
} from '@arco-design/web-react';
import { IconEmpty, IconMoreVertical, IconSearch } from '@arco-design/web-react/icon';
import { listApplication, type Application, type PageParam } from '@onebase/app';
import { getCommonPaginationList, getRuntimeURL, TokenManager } from '@onebase/common';
import { debounce } from 'lodash-es';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import arrowRightUp from '@/assets/images/arrow-right-up.svg';
import emptyApplicationSVG from '@/assets/images/tenantNoContent.svg';
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

const EnterpriseAppPage: React.FC = () => {
  const { tenantId } = useParams();

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

  const [tagModalVisible, setTagModalVisible] = useState<boolean>(false);

  const [applicationEmpty, setAapplicationEmpty] = useState<boolean>(false); // 未创建应用
  const [applicationFilterEmpty, setAapplicationFilterEmpty] = useState<boolean>(false); // 应用列表过滤后为空，此时applicationEmpty为true

  const { setCurAppId } = useAppStore();

  const appContainerRef = useRef<HTMLDivElement>(null);

  // option dropdown
  const [optionVisibleId, setOptionVisibleId] = useState('');
  const timerRef: any = useRef<number | null>(null);

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
      status: 1
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

  const nagivateToDataFactory = (appId: string) => {
    setCurAppId(appId);
    const tenantId = TokenManager.getTenantInfo()?.tenantId || '';

    const newWindow = window.open('', '_blank');
    if (newWindow) {
      const redirectURL = `${getRuntimeURL()}/#/onebase/runtime/?appId=${appId}&tenantId=${tenantId}`;
      newWindow.location.href = `${getRuntimeURL()}/#/login?redirectURL=${redirectURL}`;
    }
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

  const getModel = (model?: string) => {
    if (model === 'inner') {
      return '内部模式';
    } else if (model === 'saas') {
      return 'SaaS模式';
    }
    return '';
  };

  const getColor = (model?: string) => {
    if (model === 'inner') {
      return 'cyan';
    } else if (model === 'saas') {
      return 'red';
    }
    return '';
  };

  const handleClickButton = () => {
    navigate(`/onebase/${tenantId}/setting/application`);
  };

  const menu = (item: any) => {
    return (
      <Menu onPointerEnter={clearTimer} onPointerLeave={() => startCloseTimer(80)}>
        <Menu.Item
          key="1"
          onClick={(e) => {
            e.stopPropagation();
            //TODO
          }}
        >
          启用
        </Menu.Item>
        <Menu.Item
          key="2"
          onClick={(e) => {
            e.stopPropagation();
            //TODO
          }}
        >
          下架
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
          type="outline"
          size="large"
          icon={<img src={arrowRightUp} alt="create application" />}
          className={styles.createAppButton}
          onClick={handleClickButton}
          style={{ color: 'rgb(var(--primary-6))' }}
        >
          {t('myApp.applicationManagement')}
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
            </div>
          </div>

          {/* 我的应用列表 */}
          <Spin loading={loading} size={40} style={{ width: '100%', height: '100%' }} tip="加载中...">
            <div className={styles.myAppList} ref={appContainerRef}>
              {applicationEmpty && !loading && (
                <div className={styles.applicationEmpty}>
                  <img src={emptyApplicationSVG} alt="暂无应用" />
                  <Typography.Text type="secondary">
                    还没有应用，
                    <Link
                      onClick={() => {
                        navigate('/onebase/setting/application');
                      }}
                      className={styles.linkText}
                    >
                      应用管理！
                    </Link>
                  </Typography.Text>
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
                            <Tooltip content={item.appName}>
                              <div className={styles.myAppTitle}>{item.appName}</div>
                            </Tooltip>
                            <Tag color={getColor(item.publishModel)} className={styles.tag}>
                              {getModel(item.publishModel)}
                            </Tag>
                          </div>
                          <Tag
                            color={TagColor[item.appStatus]}
                            style={{
                              fontSize: 12,
                              fontWeight: 400
                            }}
                          >
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
                        nagivateToDataFactory(item.id);
                      }}
                      // disabled={item.publishModel === "saas"}
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

export default EnterpriseAppPage;
