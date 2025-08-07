import appDeleteSVG from '@/assets/images/app_delete.svg';
import appEditSVG from '@/assets/images/app_edit.svg';
import appIconSVG from '@/assets/images/app_icon.svg';
import { useAppStore } from '@/store';
import { UserPermissionManager } from '@/utils/permission';
import { Avatar, Button, Input, Modal, Pagination, Select, Spin, Tag, Form, Message } from '@arco-design/web-react';
import { IconPlusCircle, IconSearch } from '@arco-design/web-react/icon';
import { listApplication, createApplication, type Application, type ListApplicationReq, type CreateApplicationReq } from '@onebase/app';
import dayjs from 'dayjs';
import React, { useEffect, useState, useCallback } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { debounce } from 'lodash-es';
import CreateApp from '@/components/CreateApp';
import styles from './index.module.less';

const Option = Select.Option;
const appOptions = [
  {
    label: '全部应用',
    value: 0
  },
  {
    label: '我创建的',
    value: 1
  }
];
const createTimeOptions = [
  {
    label: '按创建时间排序',
    value: 'create'
  },
  {
    label: '按更新时间排序',
    value: 'update'
  }
];
const statusOptions = [
  {
    label: '全部状态',
    value: ''
  },
  {
    label: '开发中',
    value: 0
  },
  {
    label: '已发布',
    value: 1
  }
];

const MyAppPage: React.FC = () => {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const { t } = useTranslation();

  const [pageSize, setPageSize] = useState(8);
  const [pageNo, setPageNo] = useState(1);
  const [dataList, setDataList] = useState<Application[]>([]);
  const [total, setTotal] = useState(0);
  const [name, setName] = useState('');
  const [loading, setLoading] = useState(false);
  const [ownerTag, setOwnerTag] = useState<number>(0);
  const [orderByTime, setOrderByTime] = useState<'create' | 'update'>('create');
  const [status, setStatus] = useState<number | string>('');

  const [inputValue, setInputValue] = useState<string>('');
  const [deleteVisible, setDeleteVisible] = useState<boolean>(false);
  const [createVisible, setCreateVisible] = useState<boolean>(false);
  const [createLoading, setCreateLoading] = useState<boolean>(false);

  const { setCurAppCode } = useAppStore();

  useEffect(() => {
    getApplicationList();
  }, [pageNo, pageSize, name, orderByTime, status]);

  const getApplicationList = async () => {
    setLoading(true);
    const req: ListApplicationReq = {
      pageNo,
      pageSize,
      name,
      ownerTag: ownerTag === 1 ? true : false,
      orderByTime,
      status: status === '' ? null : Number(status)
    };
    const res = await listApplication(req);
    setDataList(res.list || []);
    setTotal(res.total || 0);
    setLoading(false);
  };

  const debouncedUpdate = useCallback(
    debounce((value) => {
      setName(value);
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
    form.validate(async (error, data) => {
      if (error !== null) return;
      setCreateLoading(true);
      const { appCode, appName, iconColor, iconName, description, tagIds, themeColor } = data;

      const params: CreateApplicationReq = {
        appCode,
        appMode: 'classic',
        appName,
        datasourceId: 1,
        description,
        iconColor,
        iconName,
        tagIds,
        themeColor
      };
      createApplication(params)
        .then(() => {
          setCreateVisible(false);
          Message.success({
            content: '应用创建成功，3s后跳转...',
            duration: 3000,
            onClose: () => {
              navigate('/onebase/create-app/data-factory');
            }
          });
        })
        .finally(() => {
          setCreateLoading(false);
        });
    });
  };

  const nagivateToAppPage = (appCode: Number) => {
    setCurAppCode(appCode);
    navigate(`/onebase/create-app/data-factory?appCode=${appCode}`);
  };

  return (
    <div className={styles.myAppPage}>
      <div className={styles.myAppPageHeader}>
        <div className={styles.myAppWelcome}>
          Hi {UserPermissionManager.getUserPermissionInfo()?.user.nickname || '用户'}
          ，您好！
        </div>
        <Button
          type="primary"
          size="large"
          icon={<IconPlusCircle />}
          className={styles.createAppButton}
          onClick={() => setCreateVisible(true)}
        >
          {t('myApp.createApp')}
        </Button>
      </div>

      <div className={styles.myAppContainer}>
        <div className={styles.myAppFilter}>
          <Input
            allowClear
            style={{ width: 316, height: 42, borderRadius: 6 }}
            suffix={<IconSearch />}
            onChange={handleSearchChange}
            placeholder="请输入应用名称"
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
                <Option key={index} disabled={index === 3} value={option.value}>
                  {option.label}
                </Option>
              ))}
            </Select>
            <Select
              placeholder="按创建时间排序"
              bordered={false}
              style={{ width: 138 }}
              onChange={(value) => setOrderByTime(value)}
              value={orderByTime}
            >
              {createTimeOptions.map((option, index) => (
                <Option key={index} disabled={index === 3} value={option.value}>
                  {option.label}
                </Option>
              ))}
            </Select>
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
          <div className={styles.myAppList}>
            {dataList.map((item, index) => (
              <div
                className={styles.myAppCard}
                key={index}
                onClick={() => {
                  nagivateToAppPage(Number(item.appCode));
                }}
              >
                <div className={styles.myAppCardHeader}>
                  <div className={styles.myAppName}>
                    <img className={styles.myAppIcon} src={appIconSVG} alt="应用图标" />
                    <div className={styles.myAppTitle}>{item.appName}asddddddddddddddddddd</div>
                  </div>
                  <Tag
                    style={{
                      fontSize: 11,
                      color: 'rgba(42, 130, 228, 1)'
                    }}
                  >
                    {item.appStatusText}
                  </Tag>
                </div>

                <div className={styles.myAppCardBody}>
                  <div className={styles.myAppDesc}>{item.description}</div>
                  <div className={styles.myAppTags}>
                    {item.tags?.map((tag) => (
                      <Tag key={tag.id} color="green">
                        {tag.tagName}
                      </Tag>
                    ))}
                  </div>
                </div>

                <div className={styles.myAppCardFooter}>
                  <div className={styles.myAppCreator}>
                    <Avatar
                      size={24}
                      style={{
                        backgroundColor: '#4FAE7B'
                      }}
                    >
                      {item.createUser?.slice(0, 1) || 'U'}
                    </Avatar>
                    <div className={styles.myAppCreatorName}>{item.createUser}</div>

                    <div className={styles.myAppTime}>{dayjs(item.updateTime).format('YYYY-MM-DD HH:mm:ss')}</div>
                  </div>

                  <div className={styles.myAppOperate}>
                    <img
                      src={appEditSVG}
                      alt="菜单"
                      className={styles.operateIcon}
                      onClick={(e) => {
                        e.stopPropagation();
                        nagivateToAppPage(Number(item.appCode));
                      }}
                    />
                    <img
                      src={appDeleteSVG}
                      alt="删除"
                      className={styles.operateIcon}
                      onClick={(e) => {
                        e.stopPropagation();
                        setDeleteVisible(true);
                      }}
                    />
                  </div>
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

      <Modal
        title="确认删除应用"
        visible={deleteVisible}
        onOk={() => setDeleteVisible(false)}
        onCancel={() => setDeleteVisible(false)}
        autoFocus={false}
        focusLock={true}
        confirmLoading={false}
        okButtonProps={{
          disabled: inputValue?.trim().length === 0,
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
            删除应用则页面及数据将一并删除，并且无法还原。
            <br />
            为防止误操作，如确定删除，请输入
            <strong>&quot;&lt;应用名称&gt;&quot;</strong>进行确认：
          </div>
          <Input value={inputValue} allowClear placeholder="请输入要删除的应用名称" style={{ width: 476 }} onChange={setInputValue} />
        </div>
      </Modal>
      <Modal
        title={<div style={{ textAlign: 'left' }}>创建空白应用</div>}
        visible={createVisible}
        simple
        footer={
          <div style={{ textAlign: 'right' }}>
            <Button type="default" onClick={() => setCreateVisible(false)} style={{ marginRight: 12 }}>
              取消
            </Button>
            <Button type="primary" loading={createLoading} onClick={handleCreateApp}>
              创建
            </Button>
          </div>
        }
        confirmLoading={true}
        onCancel={() => setCreateVisible(false)}
        className={styles.createAppModal}
      >
        <div className={styles.createAppWrapper}>
          <CreateApp form={form} previewBgColor="#F2F3F5BF" />
        </div>
      </Modal>
    </div>
  );
};

export default MyAppPage;
