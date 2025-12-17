import { useEffect, useState, type FC } from 'react';
import { Button, Input, Tabs, Typography, Modal, Form, Space, Pagination } from '@arco-design/web-react';
import { IconPlus, IconSearch, IconDownload } from '@arco-design/web-react/icon';
import TemplateCard from '../TemplateCard';
import styles from './index.module.less';
import settings from '@/assets/images/screen/settings.png';
import application from '@/assets/images/screen/application.png';
import cloud from '@/assets/images/screen/cloud.png';
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const { useForm } = Form;
interface screenTemplate {
  id: string;
  name: string;
  desc: string;
}

const ScreenTemplate: FC = () => {
  const handleSearchChange = () => {};
  const handleAdd = () => {};
  const [applicationDataList, setApplicationDataList] = useState<screenTemplate[]>();
  const [systemDataList, setSystemDataList] = useState<screenTemplate[]>();
  const [total, setTotal] = useState(1);
  const [pageSize, setPageSize] = useState<number>();
  const [pageNo, setPageNo] = useState(1);
  useEffect(() => {
    setApplicationDataList([
      {
        id: '110',
        name: '应用1',
        desc: '描述1'
      },
      {
        id: '220',
        name: '应用2',
        desc: '描述2'
      },
      {
        id: '222',
        name: '应用3',
        desc: '描述2'
      },
      {
        id: '223',
        name: '应用4',
        desc: '描述2'
      },
      {
        id: '224',
        name: '应用5',
        desc: '描述2'
      },
      {
        id: '225',
        name: '应用6',
        desc: '描述2'
      }
    ]);
    setSystemDataList([
      {
        id: '111',
        name: '系统1',
        desc: '系统描述1'
      },
      {
        id: '112',
        name: '系统1',
        desc: '系统描述1'
      },
      {
        id: '113',
        name: '系统3',
        desc: '系统描述1'
      }
    ]);
  }, []);
  //编辑
  // 编辑弹框
  const [editForm] = useForm();
  const [editVisible, setEditVisible] = useState<boolean>(false);
  const handleEditName = (item: screenTemplate) => {
    console.log(item, '编辑模板名字弹框');
    editForm.setFieldValue('name', item.name);
    editForm.setFieldValue('desc', item.desc);
    setEditVisible(true);
  };
  //编辑弹框确定按钮
  const handleEditOk = async () => {
    await editForm.validate();
    setEditVisible(false);
  };
  const handleEditTemplate = (item: screenTemplate) => {
    console.log(item, '编辑模板');
  };
  //取消弹框
  const handleEditCancel = () => {
    setEditVisible(false);
  };
  //预览
  const handlePreview = (item: screenTemplate) => {
    console.log(item, '预览');
  };
  //导入模板
  const [selectedButton, setSelectedButton] = useState('');

  const [importVisible, setImportVisible] = useState<boolean>(false);
  const [importForm] = useForm();
  const handleImportTemplate = () => {
    importForm.resetFields();
    setImportVisible(true);
  };
  // 清空表单
  // const clearImportForm = () => {
  //   setSelectedButton('');
  //   importForm.setFieldValue('templateNmae', '');
  //   importForm.setFieldValue('desc', '');
  //   importForm.setFieldValue('type', '');
  //   importForm.setFieldValue('screeenFile', '');
  // };
  const handleClickButtom = (name: string) => {
    setSelectedButton(name);
    importForm.setFieldValue('type', name);
  };
  const handleImportOk = async () => {
    importForm.setFieldValue('screeenFile', '');
    await importForm.validate();
    console.log(importForm);
    setImportVisible(false);
  };
  //删除弹框
  const [deleteVisible, setDeleteVisible] = useState<boolean>(false);
  const [ModalScreenName, setModalScreenName] = useState('');
  const handleDelete = (item: screenTemplate) => {
    console.log(item);
    setModalScreenName(item.name);
    setDeleteVisible(true);
  };
  const handleDeleteOk = () => {
    console.log('删除当前screen');
    setDeleteVisible(false);
  };
  return (
    <div className={styles.datasetPage}>
      <div className={styles.dataFilter}>
        <div className={styles.datasetTitle}>大屏模板</div>
        <div>
          <Button style={{ marginRight: '6px' }} type="outline" icon={<IconDownload />} onClick={handleImportTemplate}>
            导入模板
          </Button>
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
        <Tabs defaultActiveTab="1">
          <TabPane key="1" title="应用模板">
            <Typography.Paragraph>
              <div className={styles.appList}>
                {applicationDataList?.map((item) => (
                  <TemplateCard
                    key={item.id}
                    item={item}
                    title="应用模板"
                    onEditTemplate={handleEditName}
                    onEdit={handleEditTemplate}
                    onPreview={handlePreview}
                    onDelete={handleDelete}
                  />
                ))}
              </div>
            </Typography.Paragraph>
          </TabPane>
          <TabPane key="2" title="系统模板">
            <Typography.Paragraph>
              <div className={styles.appList}>
                {systemDataList?.map((item) => (
                  <TemplateCard
                    key={item.id}
                    item={item}
                    title="系统模板"
                    onEditTemplate={handleEditName}
                    onEdit={handleEditTemplate}
                    onPreview={handlePreview}
                    onDelete={handleDelete}
                  />
                ))}
              </div>
            </Typography.Paragraph>
          </TabPane>
        </Tabs>
      </div>
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
      {/* 编辑弹框 */}
      <Modal
        title={<div style={{ textAlign: 'left', fontWeight: 500 }}>修改大屏信息</div>}
        visible={editVisible}
        onOk={handleEditOk}
        onCancel={handleEditCancel}
      >
        <Form form={editForm} autoComplete="off">
          <FormItem label="大屏名称" field="name" rules={[{ required: true, message: '请输入大屏名称' }]}>
            <Input placeholder="请输入名称,不超过20个字符" />
          </FormItem>
          <FormItem label="大屏描述" field="desc" rules={[{ required: true, message: '请输入大屏描述' }]}>
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
          <FormItem label="描述信息" field="desc" rules={[{ required: true, message: '请输入大屏描述' }]}>
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
                <div className={styles.uploadDescOk}>
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
                <div className={styles.uploadDesc}>
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
