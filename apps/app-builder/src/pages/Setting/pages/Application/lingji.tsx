import {
  Divider,
  Form,
  Input,
  Modal,
  Pagination,
  Select,
  Space,
  Spin,
  Typography,
  Grid
} from '@arco-design/web-react';
import { IconLeft, IconRight, IconSearch } from '@arco-design/web-react/icon';
import React from 'react';

import emptyApplicationSVG from '@/assets/images/applicationLogo.svg';
import CreateAppModal from '@/components/CreateApp';
import CreateDataSource from '@/components/CreateDataSource';
import LingjiAppCard from './components/AppCard/lingji';
import { appOptions, createTimeOptions, statusOptions } from './const';
import styles from './lingji.module.less';
import { PermissionButton as Button } from '@/components/PermissionControl';
import aiCreateSVG from '@/assets/images/ai_create.svg';
import createSvg from '@/assets/images/appBasic/app_create.svg';
import cloneSvg from '@/assets/images/appBasic/app_clone.svg';
import importSvg from '@/assets/images/appBasic/app_import.svg';
import AppImportModal from '@/components/AppImportModal';
import { TENANT_APP_PERMISSION as ACTIONS } from '@onebase/common';
import { useApplicationPage } from './utils/useApplicationPage';

const Option = Select.Option;

const LingjiApplicationPage: React.FC = () => {
  const {
    form,
    navigate,
    pageSize,
    pageNo,
    dataList,
    total,
    loading,
    ownerTag,
    orderByTime,
    status,
    appName,
    setAppName,
    createType,
    setCreateType,
    deleteApp,
    datasource,
    deleteVisible,
    setDeleteVisible,
    createVisible,
    setCreateVisible,
    createLoading,
    deleteLoading,
    applicationEmpty,
    applicationFilterEmpty,
    currentStep,
    setCurrentStep,
    dbTypeSelect,
    setDbTypeSelect,
    createDatasourceRef,
    appContainerRef,
    optionVisibleId,
    importVisible,
    setImportVisible,
    setPageNo,
    setPageSize,
    handleSearchChange,
    handleCreateApp,
    handleDeleteApp,
    handleOptionVisibleChange,
    handleEdit,
    handleLaunch,
    handleDelete,
    getApplicationList
  } = useApplicationPage();

  return (
    <div className={styles.appPage}>
      <div className={styles.appContainer}>
        <div className={styles.appHasDataBox}>
          {ACTIONS.CREATE && (
            <Grid.Row gutter={24} className={styles.appCreate}>
              <Grid.Col span={9}>
                <div className={styles.aiCreate} onClick={() => navigate('/aigen/chat')}>
                  <div className={styles.aiCreateTitle}>AI生成应用</div>
                  <div className={styles.aiCreateDesc}>输入需求或上传文档，大模型自动帮您搭建零代码应用</div>
                </div>
              </Grid.Col>
              <Grid.Col span={5}>
                <div className={styles.otherCreate} onClick={() => setCreateVisible(true)}>
                  <div className={styles.otherCreateContent}>
                    <div className={styles.otherCreateTitle}>手动创建应用</div>
                    <div className={styles.otherCreateDesc}>手动拖拉拽可视化组件，从零开始创建零代码应用</div>
                  </div>
                  <img src={createSvg} alt="" />
                </div>
              </Grid.Col>
              <Grid.Col span={5}>
                <div className={styles.otherCreate}>
                  <div className={styles.otherCreateContent}>
                    <div className={styles.otherCreateTitle}>智能克隆</div>
                    <div className={styles.otherCreateDesc}>智能解析高码应用，快速转换为零代码应用</div>
                  </div>
                  <img src={cloneSvg} alt="" />
                </div>
              </Grid.Col>
              <Grid.Col span={5}>
                <div className={styles.otherCreate} onClick={() => setImportVisible(true)}>
                  <div className={styles.otherCreateContent}>
                    <div className={styles.otherCreateTitle}>应用导入</div>
                    <div className={styles.otherCreateDesc}>快速导入应用包，一键复用已有配置</div>
                  </div>
                  <img className={styles.otherCreateImg} src={importSvg} alt="" />
                </div>
              </Grid.Col>
            </Grid.Row>
          )}

          <div
            className={styles.appFilter}
            style={{
              pointerEvents: applicationEmpty ? 'auto' : 'unset'
            }}
          >
            <Input
              className={styles.appInput}
              allowClear
              suffix={<IconSearch />}
              onChange={handleSearchChange}
              placeholder="请输入"
            />

            <div>
              <Select
                placeholder="全部应用"
                bordered={false}
                style={{ width: 100 }}
                value={ownerTag}
                onChange={(value) => setOwnerTag(value as 0 | 1)}
              >
                {appOptions.map((option, index) => (
                  <Option key={index} value={option.value}>
                    {option.label}
                  </Option>
                ))}
              </Select>
              <Select
                placeholder="按创建时间排序"
                bordered={false}
                style={{ width: 138 }}
                onChange={(value) => setOrderByTime(value as 'create' | 'update')}
                value={orderByTime}
              >
                {createTimeOptions.map((option, index) => (
                  <Option key={index} value={option.value}>
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

          <Spin className={styles.appListLoading} loading={loading} size={40} tip="加载中..." ref={appContainerRef}>
            <div className={styles.appList}>
              {(applicationFilterEmpty || applicationEmpty) && !loading && (
                <div className={styles.applicationEmpty}>
                  <img src={emptyApplicationSVG} alt="暂无应用" />
                  <Typography.Text type="secondary">还没有应用</Typography.Text>
                  <Button
                    className={styles.goCreateApplication}
                    permission={ACTIONS.CREATE}
                    onClick={() => setCreateVisible(true)}
                  >
                    去创建
                    <IconRight style={{ marginLeft: '4px' }} />
                  </Button>
                </div>
              )}
              {dataList?.map((item) => (
                <LingjiAppCard
                  key={item.id}
                  item={item}
                  optionVisibleId={optionVisibleId}
                  onOptionVisibleChange={handleOptionVisibleChange}
                  onEdit={handleEdit}
                  onLaunch={handleLaunch}
                  onDelete={handleDelete}
                  onUpdate={getApplicationList}
                />
              ))}
            </div>
          </Spin>

          <Pagination
            className={styles.appPagination}
            total={total}
            current={pageNo}
            pageSize={pageSize}
            onChange={(pNo: number, pSize: number) => {
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
            backgroundColor: '#FF4D4F',
            borderColor: '#FF4D4F'
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
        style={{ width: '1030px' }}
        className={styles.createAppModal}
      >
        <div className={styles.createAppWrapper}>
          <CreateAppModal
            form={form}
            dataSourceCreated={!!datasource}
            onCreateDatasource={() => setCreateType('datasource')}
            isCreateVisible={createVisible}
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
              padding: '0 150px',
              boxSizing: 'border-box',
              transform: createType === 'datasource' ? 'translateX(0)' : 'translateX(100%)'
            }}
          />
        </div>
      </Modal>

      <AppImportModal
        visible={importVisible}
        onClose={() => setImportVisible(false)}
        onComplete={() => {
          getApplicationList();
        }}
      />
    </div>
  );
};

export default LingjiApplicationPage;
