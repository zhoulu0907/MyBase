import { useAppStore } from '@/store';
import {
  Button,
  Divider,
  Dropdown,
  Input,
  Layout,
  Menu,
  Message,
  Modal,
  Pagination,
  Select,
  Spin,
  Tag,
  Typography
} from '@arco-design/web-react';
import { IconEmpty, IconMoreVertical, IconSearch } from '@arco-design/web-react/icon';
import { type PageParam } from '@onebase/app';
import {
  CORP_APP_AUTH_PERMISSION,
  getCommonPaginationList,
  getRuntimeURL,
  hasPermission,
  TokenManager
} from '@onebase/common';
import {
  getCorpAuthorizedAppListApiInCorp,
  updateAuthAppStatusInCorp,
  type authAppStatusParams
} from '@onebase/platform-center';
import { debounce } from 'lodash-es';
import React, { useCallback, useEffect, useRef, useState } from 'react';

import noContentSVG from '@/assets/images/noContent.svg';
import { DynamicIcon } from '@/components/DynamicIcon';

import { AppHeader } from '@/components/header';
import { StatusEnum, StatusEnumLabel, StatusLabelEnum } from '@/constants';
import type { ApplicationList, TagProps } from '@/types';
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
  const [pageSize, setPageSize] = useState<number>();
  const [pageNo, setPageNo] = useState(1);
  const [dataList, setDataList] = useState<ApplicationList[]>();
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
    const tokenInfo = TokenManager.getTokenInfo();
    setLoading(true);
    const req: PageParam = {
      pageNo,
      pageSize: pageSize || 8,
      name,
      ownerTag,
      orderByTime,
      status: 0,
      corpId: tokenInfo?.corpId || ''
    };
    const res = await getCommonPaginationList(getCorpAuthorizedAppListApiInCorp, req, setPageNo);
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

  const nagivateToRuntimeApp = (appId: string) => {
    setCurAppId(appId);
    const tenantId = TokenManager.getTenantInfo()?.tenantId || '';

    const redirectURL = `${getRuntimeURL()}/#/onebase/${tenantId}/${appId}/runtime`;
    console.log('redirectURL: ', redirectURL);

    window.open(redirectURL, '_blank');
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

  const handleChangeStatus = async (item: any) => {
    if (item.showStatus === 2) {
      const params: authAppStatusParams = { id: item.id, status: '1' };
      try {
        await updateAuthAppStatusInCorp(params);
        getApplicationList();
      } catch (error) {
        Message.error('启用失败');
      }
    } else {
      const params: authAppStatusParams = { id: item.id, status: '0' };
      return Modal.confirm({
        title: `禁用应用(${item.applicationName})? `,
        content: '禁用状态下，企业用户无法使用该应用，再次启用时用户可恢复正常使用',
        okButtonProps: {
          status: 'danger'
        },
        onOk: async () => {
          await updateAuthAppStatusInCorp(params);
          getApplicationList();
          Message.success('禁用成功');
        }
      });
    }
  };
  

  const menu = (item: any) => {
    return (
      <Menu onPointerEnter={clearTimer} onPointerLeave={() => startCloseTimer(80)}>
        <Menu.Item
          key="1"
          onClick={(e) => {
            e.stopPropagation();
            handleChangeStatus(item);
          }}
        >
          {item.showStatus === 1 ? StatusLabelEnum.DISABLE : StatusLabelEnum.ENABLE}
        </Menu.Item>
      </Menu>
    );
  };

  return (
    <Layout className={styles.homePage}>
      <AppHeader />
      <Layout className={styles.myAppPage}>
        <div className={styles.myAppPageHeader}></div>
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
                    <img src={noContentSVG} alt="暂无应用" />
                    <Typography.Text type="secondary">你的企业还没有应用，联系管理员开通吧！</Typography.Text>
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
                              <div className={styles.myAppTitle}>{item.applicationName}</div>
                            </div>
                            <Tag
                              color={TagColor[item.appStatus]}
                              style={{
                                fontSize: 12,
                                fontWeight: 400
                              }}
                            >
                              {item.appStatus === 1 ? StatusEnumLabel.ENABLE : StatusEnumLabel.DISABLE}
                            </Tag>
                            <Divider type="vertical" style={{ margin: '0 4px 0 6px', height: '8px' }} />
                            <span className={styles.versionNumber}>{item.versionNumber}</span>
                          </div>
                        </div>
                        <Dropdown
                          droplist={hasPermission(CORP_APP_AUTH_PERMISSION.ENABLE) ? menu(item) : null}
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
                          {item.tags?.map((tag: TagProps) => (
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
                        type={item.showStatus === StatusEnum.DISABLE ? 'secondary' : 'outline'}
                        long
                        onClick={() => {
                          nagivateToRuntimeApp(item.applicationId);
                        }}
                        disabled={item.showStatus === StatusEnum.DISABLE}
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
      </Layout>
    </Layout>
  );
};

export default MyAppPage;
