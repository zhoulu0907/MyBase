import { useEffect, useState, type FC } from 'react';
import { Button, Input, Tabs, Typography, Modal, Form } from '@arco-design/web-react';
import { IconPlus, IconSearch, IconDownload } from '@arco-design/web-react/icon';
import TemplateCard from '../TemplateCard';
import styles from './index.module.less';
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const { useForm } = Form;
const ScreenTemplate: FC = () => {
  const handleSearchChange = () => {};
  const handleAdd = () => {};
  interface applicationDataList {
    id: string;
    name: string;
    desc: string;
  }
  interface systemDataList {
    id: string;
    name: string;
    desc: string;
  }

  const [applicationDataList, setApplicationDataList] = useState<applicationDataList[]>();
  const [systemDataList, setSystemDataList] = useState<systemDataList[]>();
  useEffect(() => {
    setApplicationDataList([
      {
        id: '110',
        name: '应用1',
        desc: '描述1'
      },
      {
        id: '220',
        name: '应用1',
        desc: '描述2'
      }
    ]);
    setSystemDataList([
      {
        id: '011',
        name: '系统1',
        desc: '系统描述1'
      },
      {
        id: '022',
        name: '系统1',
        desc: '系统描述1'
      }
    ]);
  }, []);
  //编辑
  // 编辑弹框
  const [editForm] = useForm();
  const [editVisible, setEditVisible] = useState<boolean>(false);

  const handleEditName = (item: applicationDataList) => {
    console.log(item, '编辑模板名字弹框');
    editForm.setFieldValue('name', item.name);
    editForm.setFieldValue('desc', item.desc);
    setEditVisible(true);
  };
  //编辑弹框确定按钮
  const handleEditOk = () => {};
  const handleEditTemplate = (item: applicationDataList) => {
    console.log(item, '编辑模板');
  };
  //取消弹框
  const handleEditCancel = () => {
    setEditVisible(false);
  };
  //预览
  const handlePreview = (item: applicationDataList) => {
    console.log(item, '预览');
  };
  //导入模板
  const [importVisible, setImportVisible] = useState<boolean>(false);

  const handleImportTemplate = () => {
    setImportVisible(true);
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
                  />
                ))}
              </div>
            </Typography.Paragraph>
          </TabPane>
        </Tabs>
        <Input
          className={styles.appInput}
          allowClear
          suffix={<IconSearch />}
          onChange={handleSearchChange}
          placeholder="搜索"
        />
      </div>
      {/* 编辑弹框 */}
      <Modal
        title={<div style={{ textAlign: 'left', fontWeight: 500 }}>修改大屏信息</div>}
        visible={editVisible}
        onOk={handleEditOk}
        onCancel={handleEditCancel}
      >
        <Form form={editForm} autoComplete="off">
          <FormItem label="大屏名称" field="name" rules={[{ required: true, message: '请输入大屏名称' }]}>
            <Input placeholder="" />
          </FormItem>
          <FormItem label="大屏描述" field="desc" rules={[{ required: true, message: '请输入大屏描述' }]}>
            <Input placeholder="" />
          </FormItem>
        </Form>
      </Modal>
      {/* 导入模板 */}
      <Modal
        title={<div style={{ textAlign: 'left', fontWeight: 500 }}>导入模板</div>}
        visible={importVisible}
        onOk={() => setImportVisible(false)}
        onCancel={() => setImportVisible(false)}
        autoFocus={false}
        focusLock={true}
      >
        <p></p>
      </Modal>
    </div>
  );
};
export default ScreenTemplate;
