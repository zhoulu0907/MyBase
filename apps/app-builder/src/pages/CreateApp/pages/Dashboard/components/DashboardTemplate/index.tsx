import { useCallback, useRef, useEffect, useState, type FC } from 'react';
import { Button, Input, Tabs, Typography, Modal, Form, Space, Pagination } from '@arco-design/web-react';
import { IconPlus, IconSearch } from '@arco-design/web-react/icon';
import TemplateCard from '../TemplateCard';
import styles from './index.module.less';
import settings from '@/assets/images/screen/settings.png';
import application from '@/assets/images/screen/application.png';
import cloud from '@/assets/images/screen/cloud.png';
import {
  DashboardTemplateParams,
  DelDashboardTemplate,
  upLoadDashboardTemplate,
  createDashboardTemplate
} from '@onebase/platform-center';
import { useLocation } from 'react-router-dom';
import { getDashBoardURL } from '@onebase/common';

const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const { useForm } = Form;
interface screenTemplate {
  remarks: string;
  id: string;
  templateName: string;
  templateType: string;
  indexImage: string;
}

const ScreenTemplate: FC = () => {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const appId = searchParams.get('appId');
  const resourceUrl = getDashBoardURL();
  //创建模板
  const handleAdd = async () => {
    const res = await createDashboardTemplate({ templateType: 'template', appId: appId });
    window.open(`${resourceUrl}chart/home/${res}/template`, '_blank');
  };
  const [applicationDataList, setApplicationDataList] = useState<screenTemplate[]>();
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(6);
  const [total, setTotal] = useState(1);
  useEffect(() => {
    getTemplateList();
  }, [currentPage]);
  //列表
  const getTemplateList = async (tabType: string = 'app', searchValue: string = '') => {
    const res = await DashboardTemplateParams({
      pageNo: currentPage,
      pageSize: pageSize,
      type: tabType,
      templateName: searchValue
    });
    setApplicationDataList(res.list);
    setTotal(res.total);
  };
  // 处理分页变化
  const handlePageChange = async (pageNum: number) => {
    setCurrentPage(pageNum);
    getTemplateList();
  };

  const [activeTab, setActiveTab] = useState('1');
  const searchTimeoutRef = useRef<NodeJS.Timeout | null>(null);
  const handleSearchChange = useCallback(
    (value: string) => {
      // 清除之前的定时器
      if (searchTimeoutRef.current) {
        clearTimeout(searchTimeoutRef.current);
      }
      // 设置新的定时器
      searchTimeoutRef.current = setTimeout(() => {
        console.log(value);
        // 在这里执行搜索逻
        getTemplateList(activeTab === '1' ? 'app' : 'system', value);
      }, 1000);
    },
    [activeTab]
  );
  const handleTabChange = (key: string) => {
    const tabType = key === '1' ? 'app' : 'system';
    setActiveTab(key);
    getTemplateList(tabType);
  };
  // 修改弹框
  const [editForm] = useForm();
  const [editVisible, setEditVisible] = useState<boolean>(false);
  const [editid, setEditid] = useState('');
  const handleEditName = (item: screenTemplate) => {
    setEditid(item.id);
    editForm.setFieldValue('templateName', item.templateName);
    editForm.setFieldValue('remarks', item.remarks);
    setEditVisible(true);
  };
  //修改弹框确定按钮
  const handleEditOk = async () => {
    await editForm.validate();
    upLoadDashboardTemplate({
      id: editid,
      templateName: editForm.getFieldValue('templateName'),
      remarks: editForm.getFieldValue('remarks')
    });
    setEditVisible(false);
    const currentType = activeTab === '1' ? 'app' : 'system';
    await getTemplateList(currentType);
  };
  const handleEditTemplate = (item: screenTemplate) => {
    window.open(`${resourceUrl}chart/home/${item.id}/${appId}/template`, '_blank');
  };
  //取消弹框
  const handleEditCancel = () => {
    setEditVisible(false);
  };
  //预览
  const handlePreview = (item: screenTemplate) => {
    window.open(`${resourceUrl}chart/preview/${item.id}/template`, '_blank');
  };
  //导入模板
  const [selectedButton, setSelectedButton] = useState('');
  const [importVisible, setImportVisible] = useState<boolean>(false);
  const [importForm] = useForm();
  // const handleImportTemplate = () => {
  //   importForm.resetFields();
  //   setImportVisible(true);
  // };
  const handleClickButtom = (name: string) => {
    setSelectedButton(name);
    importForm.setFieldValue('type', name);
  };
  const handleImportOk = async () => {
    importForm.setFieldValue('screeenFile', '');
    await importForm.validate();
    setImportVisible(false);
  };
  //删除弹框
  const [deleteVisible, setDeleteVisible] = useState<boolean>(false);
  const [ModalScreenName, setModalScreenName] = useState('');
  const [delid, setDelid] = useState('');
  const handleDelete = (item: screenTemplate) => {
    setDelid(item.id);
    setModalScreenName(item.templateName);
    setDeleteVisible(true);
  };
  const handleDeleteOk = async () => {
    await DelDashboardTemplate(delid);
    setDeleteVisible(false);
    const currentType = activeTab === '1' ? 'app' : 'system';
    await getTemplateList(currentType);
  };
  const TabPaneList = [
    {
      key: '1',
      title: '应用模板'
    },
    {
      key: '2',
      title: '系统模板'
    }
  ];
  return (
    <div className={styles.datasetPage}>
      <div className={styles.dataFilter}>
        <div className={styles.datasetTitle}>大屏模板</div>
        <div>
          {/* <Button style={{ marginRight: '6px' }} type="outline" icon={<IconDownload />} onClick={handleImportTemplate}>
            导入模板
          </Button> */}
          <Button type="primary" icon={<IconPlus />} onClick={handleAdd}>
            新建模板
          </Button>
        </div>
      </div>
      <div className={styles.dataContent}>
        <Input
          className={styles.appInput}
          allowClear
          suffix={<IconSearch />}
          onChange={handleSearchChange}
          placeholder="搜索"
        />
        <Tabs defaultActiveTab="1" onChange={handleTabChange}>
          {TabPaneList.map((item) => (
            <TabPane key={item.key} title={item.title}>
              <Typography.Paragraph>
                <div className={styles.appList}>
                  {applicationDataList?.map((item) => (
                    <TemplateCard
                      key={item.id}
                      item={item}
                      onEditTemplate={handleEditName}
                      onEdit={handleEditTemplate}
                      onPreview={handlePreview}
                      onDelete={handleDelete}
                    />
                  ))}
                </div>
              </Typography.Paragraph>
            </TabPane>
          ))}
        </Tabs>
      </div>
      <Pagination
        current={currentPage}
        pageSize={pageSize}
        total={total}
        onChange={handlePageChange}
        showTotal
        style={{
          justifyContent: 'flex-end'
        }}
      />
      {/* 编辑弹框 */}
      <Modal
        title={<div style={{ textAlign: 'left', fontWeight: 500 }}>修改大屏信息</div>}
        visible={editVisible}
        onOk={handleEditOk}
        onCancel={handleEditCancel}
      >
        <Form form={editForm} autoComplete="off">
          <FormItem label="大屏名称" field="templateName" rules={[{ required: true, message: '请输入大屏名称' }]}>
            <Input placeholder="请输入名称,不超过20个字符" />
          </FormItem>
          <FormItem label="大屏描述" field="remarks" rules={[{ required: true, message: '请输入大屏描述' }]}>
            <Input placeholder="请输入描述信息" />
          </FormItem>
        </Form>
      </Modal>
      {/* 删除卡片弹框 */}
      <Modal
        title={<div style={{ textAlign: 'left', fontWeight: 500 }}>确认删除</div>}
        visible={deleteVisible}
        onOk={handleDeleteOk}
        onCancel={() => setDeleteVisible(false)}
        autoFocus={false}
        focusLock={true}
        footer={
          <>
            <Button type="secondary" size="default" style={{ marginRight: 10 }} onClick={() => setDeleteVisible(false)}>
              取消
            </Button>
            <Button type="primary" status="danger" size="default" onClick={handleDeleteOk}>
              确认删除
            </Button>
          </>
        }
      >
        <p style={{ fontSize: 16, fontWeight: 500, color: '#1D2129' }}>
          您确定要删除此大屏吗？删除后将无法恢复，请谨慎操作。
        </p>
        <div className={styles.ModalScreenName}>大屏名称：{ModalScreenName}</div>
      </Modal>
      {/* 导入模板 */}
      <Modal
        title={<div style={{ textAlign: 'left', fontWeight: 500 }}>导入模板</div>}
        visible={importVisible}
        onOk={handleImportOk}
        onCancel={() => {
          setImportVisible(false);
          importForm.resetFields();
        }}
        autoFocus={false}
        focusLock={true}
      >
        <Form form={importForm} autoComplete="off" layout="vertical">
          <FormItem
            label="大屏模板名称"
            field="templateNmae"
            rules={[{ required: true, message: '请输入大屏模板名称' }]}
          >
            <Input maxLength={20} placeholder="请输入名称,不超过20个字符" />
          </FormItem>
          <FormItem label="描述信息" field="remarks" rules={[{ required: true, message: '请输入大屏描述' }]}>
            <Input.TextArea placeholder="请输入描述信息" />
          </FormItem>
          {importForm.getFieldValue('screeenFile') && (
            <FormItem label="模板分类" field="type" rules={[{ required: true, message: '请选择模板分类' }]}>
              <Space>
                <div
                  className={
                    selectedButton === 'setting' ? `${styles.activeTemplateButtom}` : `${styles.templateButtom}`
                  }
                  onClick={() => {
                    handleClickButtom('setting');
                  }}
                >
                  <div className={styles.buttomIcon}>
                    <img src={settings} alt="" width="16px" height="15px" />
                  </div>
                  <div className={styles.buttomTitle}>系统模板</div>
                </div>
                <div
                  className={
                    selectedButton === 'application' ? `${styles.activeTemplateButtom}` : `${styles.templateButtom}`
                  }
                  onClick={() => {
                    handleClickButtom('application');
                  }}
                >
                  <div className={styles.buttomIcon}>
                    <img src={application} alt="" width="17px" height="16px" />
                  </div>
                  <div className={styles.buttomTitle}>应用模板</div>
                </div>
              </Space>
            </FormItem>
          )}
          <FormItem label="上传JSON文件" field="screeenFile" rules={[{ required: true, message: '请上传JSON文件' }]}>
            <div className={styles.uploadContent}>
              <div className={styles.cloudIcon}>
                <img src={cloud} alt="" width="32px" height="32px" />
              </div>
              {importForm.getFieldValue('screeenFile') ? (
                <div className={styles.uploadremarksOk}>
                  <p>这是一个文件标题.json</p>
                  <p>
                    <Button type="text" size="mini">
                      重新上传
                    </Button>
                    <Button type="text" size="mini">
                      删除
                    </Button>
                  </p>
                </div>
              ) : (
                <div className={styles.uploadremarks}>
                  <p>
                    拖拽JSON文件到此处，或<span>点击上传</span>
                  </p>
                  <p>支持的格式：.json（文件大小不超过**MB）</p>
                </div>
              )}
            </div>
          </FormItem>
        </Form>
      </Modal>
    </div>
  );
};
export default ScreenTemplate;
