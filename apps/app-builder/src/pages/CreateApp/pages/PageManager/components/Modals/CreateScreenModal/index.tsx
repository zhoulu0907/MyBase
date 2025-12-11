import MenuComp from '@/components/MenuIcon';
import { Button, Form, Input, Modal, Pagination, Select, TreeSelect, type FormInstance } from '@arco-design/web-react';
import { PageType, RootParentPage } from '@onebase/app';
import { webMenuIcons } from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';
import screenNew from '@/assets/images/screenNew.svg';
import screenTemplate from '@/assets/images/screenTemplate.svg';
import screenLink from '@/assets/images/screenLink.svg';
import screenChange from '@/assets/images/screenChange.svg';

interface CreateModalProps {
  title: string;
  handleCreate: () => void;
  onCancel: () => void;
  form: FormInstance;
  // pageSetTypeOptions: { label: string; value: any }[];
  visibleCreateForm: string;
  initValue: { pageType: number; menuName: string; parentId: string };
  treeData: any[];
  entityListOptions: { label: string; value: any }[];
}

const CreateModal: React.FC<CreateModalProps> = ({
  title,
  handleCreate,
  onCancel,
  form,
  // pageSetTypeOptions,
  visibleCreateForm,
  initValue,
  treeData,
  entityListOptions
}) => {
  const allWebMenuIcons = webMenuIcons.map((ele) => ele.children).reduce((acc, current) => acc.concat(current), []);
  const InputSearch = Input.Search;

  const [menuIcon, setMenuIcon] = useState<string>();
  const [visibleMenuIcon, setVisibleMenuIcon] = useState<boolean>(false);
  const [screenMethod, setScreenMethod] = useState<string>('screenNew');
  const [screenTemplateTab, setScreenTemplateTab] = useState<string>('allTemplate');
  const [screenMethodLoading, setScreenMethodLoading] = useState<boolean>(false);
  const [screenTemplateTabLoading, setScreenTemplateTabLoading] = useState<boolean>(false);
  const [screenPagination, setScreenPagination] = useState<{ current: number; pageSize: number; total: number }>({
    current: 1,
    pageSize: 8,
    total: 160
  });
  const [screenTemplateData, setScreenTemplateData] = useState<any[]>([
    {
      title: '这是大屏名称',
      src: screenChange
    },
    {
      title: '这是大屏名称',
      src: screenChange
    },
    {
      title: '这是大屏名称',
      src: screenChange
    },
    {
      title: '这是大屏名称',
      src: screenChange
    }
  ]);

  useEffect(() => {
    if (menuIcon) {
      form.setFieldValue('menuIcon', menuIcon);
    } else {
      form.setFieldValue('menuIcon', 'FormPage');
    }
  }, [menuIcon, visibleCreateForm]);

  useEffect(() => {
    console.log('screenMethod:', screenMethod);
    setScreenPagination((prev) => ({
      ...prev,
      current: 1
    }));
    setScreenTemplateTab('allTemplate');
  }, [screenMethod]);

  const nameMap = {
    page: '页面',
    group: '分组',
    workbench: '页面',
    screen: '大屏名称'
  };
  const screenMethodData = [
    // 从空白页面新建
    {
      key: 'screenNew',
      icon: screenNew,
      screenName: '从空白页面新建'
    },
    // 从模版创建
    {
      key: 'screenTemplate',
      icon: screenTemplate,
      screenName: '从模版创建'
    },
    // 关联已有大屏
    {
      key: 'screenLink',
      icon: screenLink,
      screenName: '关联已有大屏'
    }
  ];

  const screenTemplateTabs = [
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

  const handleChangeScreenMethod = (value: string) => {
    setScreenMethodLoading(true);
    setScreenMethod(value);
    setTimeout(() => {
      setScreenMethodLoading(false);
    }, 3000);
  };
  const handleScreenChange = () => {
    console.log('handleScreenChange:', '换一批');
  };

  const handleSearchTemplate = (value: string) => {
    console.log('handleSearchTemplate:', value);
  };

  const handleChangeTemplateTab = (value: string) => {
    // setScreenTemplateTabLoading(true);
    setScreenTemplateTab(value);
  };

  const handleChangePagination = (current: number) => {
    // setScreenTemplateTabLoading(true);
    console.log('handleChangePagination:', current);
    setScreenPagination((prev) => ({
      ...prev,
      current
    }));
  };

  const screenTemplateCard = (item: any) => (
    <div className={styles.screenTemplateCard}>
      <div className={styles.screenTemplateCardImg}>
        <img src={item.src} alt="" />
        <Button className={styles.screenTemplateCardBtn}>预览</Button>
      </div>
      <div className={styles.screenTemplateCardTitle}>{item.title}</div>
    </div>
  );

  return (
    <Modal
      title={title}
      visible={visibleCreateForm !== ''}
      onOk={handleCreate}
      onCancel={handleCloseModal}
      closable={!visibleMenuIcon}
      autoFocus={false}
      focusLock={true}
      unmountOnExit={true}
      className={styles.createModal}
      footer={
        <div style={{ textAlign: 'right', visibility: !visibleMenuIcon ? 'visible' : 'hidden' }}>
          <Button type="default" onClick={handleCloseModal} style={{ marginRight: 12 }}>
            取消
          </Button>
          <Button type="primary" onClick={handleCreate}>
            创建
          </Button>
        </div>
      }
    >
      <div className={styles.createContainer}>
        <div className={styles.infoContainer}>
          <div className={styles.infoTitle}>
            <span>基础信息</span>
          </div>
          <Form
            className={styles.infoForm}
            layout="vertical"
            form={form}
            initialValues={{
              pageType: initValue.pageType,
              menuName: initValue.menuName,
              parentId: form.getFieldValue('parentId') || RootParentPage.id
            }}
            style={{
              transform: visibleMenuIcon ? 'translateX(-100%)' : ''
            }}
          >
            {/* {visibleCreateForm !== 'group' && (
            <Form.Item
              label="页面类型"
              field="pageSetType"
              rules={[{ required: true, message: '请选择页面类型' }]}
              disabled={visibleCreateForm === 'workbench'}
            >
              <Select options={getPageSetTypeOptions()} placeholder="请选择页面类型" allowClear />
            </Form.Item>
          )} */}

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
              <TreeSelect treeData={treeData} placeholder="请选择父级页面" allowClear />
            </Form.Item>
            {visibleCreateForm === 'page' && (
              <Form.Item label="数据资产" field="entityUuid" rules={[{ required: true, message: '请选择数据资产' }]}>
                <Select options={entityListOptions} placeholder="请选择数据资产" allowClear />
              </Form.Item>
            )}
          </Form>
        </div>
        <div className={styles.screenContainer}>
          <div className={styles.screenCreationMethod}>
            <div className={styles.infoTitle}>
              <span>大屏创建方式</span>
            </div>
            <div className={styles.screenCreationMethodContent}>
              {screenMethodData.map((item) => (
                <div
                  className={
                    item.key === screenMethod
                      ? styles.screenCreationMethodContentItemActive
                      : styles.screenCreationMethodContentItem
                  }
                  key={item.key}
                  onClick={() => handleChangeScreenMethod(item.key)}
                >
                  <div className={styles.screenCreationMethodContentItemIcon}>
                    {/* {item.icon} */}
                    <img src={item.icon} alt="" />
                  </div>
                  <div className={styles.screenCreationMethodContentItemText}>{item.screenName}</div>
                </div>
              ))}
            </div>
          </div>
          <div className={styles.screenTemplate}>
            <div className={styles.screenTitle}>
              <div className={styles.screenTitleText}>
                <span>
                  {screenMethod === 'screenNew'
                    ? '热门模版'
                    : screenMethod === 'screenTemplate'
                      ? '大屏模版'
                      : '已创建的大屏'}
                </span>
              </div>
              {screenMethod === 'screenNew' && (
                <div className={styles.screenChange} onClick={handleScreenChange}>
                  <img src={screenChange} alt="" />
                  <div>换一批</div>
                </div>
              )}
            </div>
            {screenMethod !== 'screenNew' && (
              <>
                <div className={styles.screenTemplateSearch}>
                  {screenMethod === 'screenTemplate' && (
                    <div className={styles.screenTemplateSearchTabs}>
                      {screenTemplateTabs.map((item) => (
                        <div
                          className={
                            screenTemplateTab === item.value
                              ? styles.screenTemplateSearchTabsItemActive
                              : styles.screenTemplateSearchTabsItem
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
            {screenMethod === 'screenNew' && (
              <>
                <div className={styles.screenTemplateContent}>
                  {screenTemplateData.map((item, index) => (
                    <div key={index}>{screenTemplateCard(item)}</div>
                  ))}
                </div>
              </>
            )}
            {screenMethod === 'screenTemplate' && (
              <>
                <div className={styles.screenTemplateContent}>
                  {screenTemplateData.map((item, index) => (
                    <div key={index}>{screenTemplateCard(item)}</div>
                  ))}
                </div>
              </>
            )}
            {screenMethod === 'screenLink' && (
              <>
                <div className={styles.screenTemplateContent}>
                  {screenTemplateData.map((item, index) => (
                    <div key={index}>{screenTemplateCard(item)}</div>
                  ))}
                </div>
              </>
            )}
            {screenMethod !== 'screenNew' && (
              <div className={styles.screenPagination}>
                <Pagination
                  // size={'small'}
                  total={screenPagination.total}
                  current={screenPagination.current}
                  pageSize={screenPagination.pageSize}
                  sizeOptions={[8]}
                  showTotal
                  sizeCanChange
                  onChange={handleChangePagination}
                  disabled={screenTemplateTabLoading}
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
