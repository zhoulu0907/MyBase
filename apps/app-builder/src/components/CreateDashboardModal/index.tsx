import MenuComp from '@/components/MenuIcon';
import { Button, Form, Input, Modal, Pagination, Select, TreeSelect, type FormInstance } from '@arco-design/web-react';
import { RootParentPage } from '@onebase/app';
import { webMenuIcons } from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';
import dashboardNew from '@/assets/images/dashboard_new.svg';
import dashboardTemplate from '@/assets/images/dashboard_template.svg';
import dashboardLink from '@/assets/images/dashboard_link.svg';
import dashboardChange from '@/assets/images/dashboard_change.svg';

interface CreateModalProps {
  title: string;
  type: 'page' | 'dashboard';
  handleCreate: (selectedTemplateId?: string, dashboardMethod?: string) => void;
  onCancel: () => void;
  visibleCreateForm: string;
  form: FormInstance;
  initValue?: { pageType: number; menuName: string; parentId: string };
  treeData?: any[];
  entityListOptions?: { label: string; value: any }[];
}

const CreateModal: React.FC<CreateModalProps> = ({
  title,
  type,
  handleCreate,
  onCancel,
  form,
  visibleCreateForm,
  initValue = { pageType: 0, menuName: '', parentId: '' },
  treeData = [],
  entityListOptions
}) => {
  // useEffect(() => {
  //   console.log('entityListOptions:', entityListOptions);
  //   console.log('form:', form);
  // }, []);

  const allWebMenuIcons = webMenuIcons.map((ele) => ele.children).reduce((acc, current) => acc.concat(current), []);
  const InputSearch = Input.Search;

  const [menuIcon, setMenuIcon] = useState<string>();
  const [visibleMenuIcon, setVisibleMenuIcon] = useState<boolean>(false);
  const [dashboardMethod, setDashboardMethod] = useState<string>('dashboardNew');
  const [dashboardTemplateTab, setDashboardTemplateTab] = useState<string>('allTemplate');
  const [dashboardMethodLoading, setDashboardMethodLoading] = useState<boolean>(false);
  const [dashboardTemplateTabLoading, setDashboardTemplateTabLoading] = useState<boolean>(false);
  const [dashboardPagination, setDashboardPagination] = useState<{ current: number; pageSize: number; total: number }>({
    current: 1,
    pageSize: dashboardMethod === 'dashboardNew' ? 4 : 8,
    total: 160
  });
  const [dashboardTemplateData, setDashboardTemplateData] = useState<any[]>([
    {
      id: 'template_1',
      title: '这是大屏名称',
      src: dashboardChange
    },
    {
      id: 'template_2',
      title: '这是大屏名称',
      src: dashboardChange
    },
    {
      id: 'template_3',
      title: '这是大屏名称',
      src: dashboardChange
    },
    {
      id: 'template_4',
      title: '这是大屏名称',
      src: dashboardChange
    }
  ]);
  const [selectedTemplateId, setSelectedTemplateId] = useState<string>('');

  useEffect(() => {
    if (menuIcon) {
      form.setFieldValue('menuIcon', menuIcon);
    } else {
      form.setFieldValue('menuIcon', 'FormPage');
    }
  }, [menuIcon, visibleCreateForm]);

  useEffect(() => {
    console.log('dashboardMethod:', dashboardMethod);
    setDashboardPagination((prev) => ({
      ...prev,
      current: 1
    }));
    setDashboardTemplateTab('allTemplate');

    if (dashboardMethod !== 'dashboardNew' && dashboardTemplateData.length > 0 && !selectedTemplateId) {
      setSelectedTemplateId(dashboardTemplateData[0].id);
    }
  }, [dashboardMethod]);

  const nameMap = {
    page: '页面',
    group: '分组',
    workbench: '页面',
    dashboard: '大屏名称'
  };

  const getDashboardMethodData = () => {
    const baseData = [
      {
        key: 'dashboardNew',
        icon: dashboardNew,
        dashboardName: '从空白页面新建'
      },
      {
        key: 'dashboardTemplate',
        icon: dashboardTemplate,
        dashboardName: '从模版创建'
      }
    ];

    if (type === 'page') {
      return [
        ...baseData,
        {
          key: 'dashboardLink',
          icon: dashboardLink,
          dashboardName: '关联已有大屏'
        }
      ];
    }

    return baseData;
  };
  const dashboardMethodData = getDashboardMethodData();

  const dashboardTemplateTabs = [
    {
      label: '全部',
      value: 'allTemplate'
    },
    {
      label: '系统模版',
      value: 'systemTemplate'
    },
    {
      label: '应用模版',
      value: 'applicationTemplate'
    }
  ];

  const handleCloseModal = () => {
    setMenuIcon('');
    onCancel();
  };

  const handleChangeDashboardMethod = (value: string) => {
    setDashboardMethodLoading(true);
    /**
     * TODO 切换大屏创建方式
     * params: dashboardMethod
     */
    setDashboardMethod(value);
    setSelectedTemplateId('');
    setTimeout(() => {
      setDashboardMethodLoading(false);
    }, 3000);
  };
  const handleDashboardChange = () => {
    // console.log('换一批 handleDashboardChange:', dashboardPagination);
    /**
     * TODO 换一批
     * params: dashboardMethod == dashboardNew + change
     */
    setDashboardPagination((prev) => ({
      ...prev,
      current: prev.current + 1 > 3 ? 1 : prev.current + 1
    }));
  };

  const handleSearchTemplate = (value: string) => {
    console.log('handleSearchTemplate:', value);
    /**
     * TODO 搜索模版
     * params: dashboardMethod !== dashboardNew + value
     */
  };

  const handleChangeTemplateTab = (value: string = '') => {
    // setDashboardTemplateTabLoading(true);
    /**
     * TODO 切换模版tab
     * params: dashboardMethod == dashboardTemplate + dashboardTemplateTab
     */
    setDashboardTemplateTab(value);
  };

  const handleChangePagination = (current: number) => {
    // setDashboardTemplateTabLoading(true);
    console.log('handleChangePagination:', current);
    /**
     * TODO 改变分页
     * params: dashboardMethod !== dashboardNew + dashboardPagination
     */
    setDashboardPagination((prev) => ({
      ...prev,
      current
    }));
  };

  const handlePreview = (dashboardProjectId: string) => {
    // 在新窗口打开预览页面，使用 hash 路由
    window.open(
      `${window.location.origin}${window.location.pathname}#/onebase/dashboard/preview/${dashboardProjectId}`,
      '_blank'
    );
  };

  const handleDashboardTemplateCard = (id: string) => {
    setSelectedTemplateId(id);
  };

  const dashboardTemplateCard = (item: any) => (
    <div
      className={`${styles.dashboardTemplateCard} ${selectedTemplateId === item.id ? styles.dashboardTemplateCardSelected : ''}`}
      onClick={() => handleDashboardTemplateCard(item.id)}
    >
      <div className={styles.dashboardTemplateCardImg}>
        <img src={item.src} alt="" />
        <Button onClick={() => handlePreview(item.id)} className={styles.dashboardTemplateCardBtn}>
          预览
        </Button>
      </div>
      <div className={styles.dashboardTemplateCardTitle}>{item.title}</div>
    </div>
  );

  return (
    <Modal
      title={title}
      visible={visibleCreateForm !== ''}
      onOk={() => handleCreate(selectedTemplateId, dashboardMethod)}
      onCancel={handleCloseModal}
      closable={!visibleMenuIcon}
      autoFocus={false}
      focusLock={true}
      unmountOnExit={true}
      className={type === 'page' ? styles.createPageModal : styles.createDashboardModal}
      footer={
        <div style={{ textAlign: 'right', visibility: !visibleMenuIcon ? 'visible' : 'hidden' }}>
          <Button type="default" onClick={handleCloseModal} style={{ marginRight: 12 }}>
            取消
          </Button>
          <Button type="primary" onClick={() => handleCreate(selectedTemplateId, dashboardMethod)}>
            创建
          </Button>
        </div>
      }
    >
      <div className={styles.createContainer}>
        {type === 'page' && (
          <div className={styles.infoContainer}>
            <div className={styles.infoTitle}>
              <span>基础信息</span>
            </div>
            <Form
              className={styles.infoForm}
              layout="vertical"
              form={form}
              initialValues={{
                pageType: initValue.pageType || 0,
                menuName: initValue.menuName || '',
                parentId: form?.getFieldValue('parentId') || initValue?.parentId || RootParentPage.id
              }}
              style={{
                transform: visibleMenuIcon ? 'translateX(-100%)' : ''
              }}
            >
              <Form.Item
                label={nameMap[visibleCreateForm as keyof typeof nameMap]}
                field="menuName"
                rules={[
                  { required: true, message: `请输入${nameMap[visibleCreateForm as keyof typeof nameMap]}` },
                  { maxLength: 20, message: '页面名称不能超过20个字符' }
                ]}
              >
                <Input
                  maxLength={20}
                  placeholder={`请输入${nameMap[visibleCreateForm as keyof typeof nameMap]}，不超过20个字符`}
                  allowClear
                  onChange={(value) => {
                    form.setFieldValue('menuName', value);
                  }}
                />
              </Form.Item>

              <Form.Item label={'菜单图标'} field="menuIcon" rules={[{ required: true, message: '请选择菜单图标' }]}>
                <div style={{ display: 'flex', alignItems: 'flex-end' }}>
                  <div
                    style={{
                      width: 32,
                      height: 32,
                      marginRight: 4,
                      borderRadius: 2,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      backgroundColor: '#F2F3F5',
                      cursor: 'pointer'
                    }}
                    onClick={() => setVisibleMenuIcon(true)}
                  >
                    {menuIcon ? (
                      <img
                        style={{ width: 'auto', height: '18px', fill: '#333' }}
                        src={allWebMenuIcons.find((ele) => ele.code === menuIcon)?.icon}
                        alt=""
                      />
                    ) : (
                      <img
                        style={{ width: 'auto', height: '18px', fill: '#333' }}
                        src={allWebMenuIcons.find((ele) => ele.code === 'FormPage')?.icon}
                        alt=""
                      />
                    )}
                  </div>
                </div>
              </Form.Item>
              <Form.Item label="父级页面" field="parentId">
                <TreeSelect treeData={treeData || {}} placeholder="请选择父级页面" allowClear />
              </Form.Item>
              {visibleCreateForm === 'page' && entityListOptions && (
                <Form.Item label="数据资产" field="entityUuid" rules={[{ required: true, message: '请选择数据资产' }]}>
                  <Select options={entityListOptions} placeholder="请选择数据资产" allowClear />
                </Form.Item>
              )}
            </Form>
          </div>
        )}
        <div className={styles.dashboardContainer}>
          <div className={styles.dashboardCreationMethod}>
            <div className={styles.infoTitle}>
              <span>大屏创建方式</span>
            </div>
            <div className={styles.dashboardCreationMethodContent}>
              {dashboardMethodData.map((item) => (
                <div
                  className={
                    item.key === dashboardMethod
                      ? styles.dashboardCreationMethodContentItemActive
                      : styles.dashboardCreationMethodContentItem
                  }
                  key={item.key}
                  onClick={() => handleChangeDashboardMethod(item.key)}
                >
                  <img src={item.icon} alt="" />
                  <div className={styles.dashboardCreationMethodContentItemText}>{item.dashboardName}</div>
                </div>
              ))}
            </div>
          </div>
          <div className={styles.dashboardTemplate}>
            <div className={styles.dashboardTitle}>
              <div className={styles.dashboardTitleText}>
                <span>
                  {dashboardMethod === 'dashboardNew'
                    ? '热门模版'
                    : dashboardMethod === 'dashboardTemplate'
                      ? '大屏模版'
                      : '已创建的大屏'}
                </span>
              </div>
              {dashboardMethod === 'dashboardNew' && (
                <div className={styles.dashboardChange} onClick={handleDashboardChange}>
                  <img src={dashboardChange} alt="" />
                  <div>换一批</div>
                </div>
              )}
            </div>
            {dashboardMethod !== 'dashboardNew' && (
              <>
                <div className={styles.dashboardTemplateSearch}>
                  {dashboardMethod === 'dashboardTemplate' && (
                    <div className={styles.dashboardTemplateSearchTabs}>
                      {dashboardTemplateTabs.map((item) => (
                        <div
                          className={
                            dashboardTemplateTab === item.value
                              ? styles.dashboardTemplateSearchTabsItemActive
                              : styles.dashboardTemplateSearchTabsItem
                          }
                          key={item.value}
                          onClick={() => handleChangeTemplateTab(item.value)}
                        >
                          {item.label}
                        </div>
                      ))}
                    </div>
                  )}
                  <div>
                    <InputSearch
                      searchButton
                      placeholder="请输入大屏名称搜索"
                      allowClear
                      style={{ width: 220 }}
                      onSearch={handleSearchTemplate}
                    />
                  </div>
                </div>
              </>
            )}
            {dashboardMethod === 'dashboardNew' && (
              <>
                <div className={styles.dashboardTemplateContent}>
                  {dashboardTemplateData.map((item, index) => (
                    <div key={index}>{dashboardTemplateCard(item)}</div>
                  ))}
                </div>
              </>
            )}
            {dashboardMethod === 'dashboardTemplate' && (
              <>
                <div className={styles.dashboardTemplateContent}>
                  {dashboardTemplateData.map((item, index) => (
                    <div key={index}>{dashboardTemplateCard(item)}</div>
                  ))}
                </div>
              </>
            )}
            {dashboardMethod === 'dashboardLink' && (
              <>
                <div className={styles.dashboardTemplateContent}>
                  {dashboardTemplateData.map((item, index) => (
                    <div key={index}>{dashboardTemplateCard(item)}</div>
                  ))}
                </div>
              </>
            )}
            {dashboardMethod !== 'dashboardNew' && (
              <div className={styles.dashboardPagination}>
                <Pagination
                  total={dashboardPagination.total}
                  current={dashboardPagination.current}
                  pageSize={dashboardPagination.pageSize}
                  sizeOptions={[8]}
                  showTotal
                  sizeCanChange
                  onChange={handleChangePagination}
                  disabled={dashboardTemplateTabLoading}
                />
              </div>
            )}
          </div>
        </div>
        <MenuComp
          style={{ transform: visibleMenuIcon ? 'translateX(0)' : '' }}
          onSelected={setMenuIcon}
          handleBack={() => setVisibleMenuIcon(false)}
        />
      </div>
    </Modal>
  );
};

export default CreateModal;
