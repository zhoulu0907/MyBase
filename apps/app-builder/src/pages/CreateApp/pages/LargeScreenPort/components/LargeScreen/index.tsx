import { useEffect, useState, type FC } from 'react';
import { Button, Input, Spin, Pagination, Modal, Form } from '@arco-design/web-react';
import { IconPlus, IconSearch } from '@arco-design/web-react/icon';
import CreateScreenModal from '@/components/CreateScreenModal';
import ScreenCard from '../ScreenCard';
import styles from './index.module.less';
const FormItem = Form.Item;
const { useForm } = Form;
interface dataList {
  id: string;
  name: string;
  state: string;
  desc: string;
}

const LargeScreen: FC = () => {
  const [loading, setLoading] = useState(false);
  const [dataList, setDataList] = useState<dataList[]>();
  const [total, setTotal] = useState(1);
  const [pageSize, setPageSize] = useState<number>();
  const [pageNo, setPageNo] = useState(1);
  useEffect(() => {
    setLoading(false);
    setDataList([
      {
        id: '1',
        name: '这是一个大屏名称',
        state: '已发布',
        desc: '描述1'
      },
      {
        id: '2',
        name: '这是一个大屏名称2',
        state: '已发布',
        desc: '描述2'
      },
      {
        id: '3',
        name: '这是一个大屏名称4',
        state: '已发布',
        desc: '描述3'
      },
      {
        id: '4',
        name: 'screen1',
        state: '已发布',
        desc: '描述4'
      }
    ]);
  }, []);
  const handleSearchChange = () => {};
  // 创建大屏弹窗
  const [createForm] = Form.useForm();
  const [visibleCreateScreenForm, setVisibleCreateScreenForm] = useState('');
  const handleAdd = () => {
    setVisibleCreateScreenForm('screen');
  };
  const handleCreateOk = () => {};
  // 编辑弹框
  const [editForm] = useForm();
  const [editVisible, setEditVisible] = useState<boolean>(false);

  const handleEditScreen = (item: dataList) => {
    console.log(item, '编辑大屏弹框');
    editForm.setFieldValue('name', item.name);
    editForm.setFieldValue('desc', item.desc);
    setEditVisible(true);
  };
  const handleEditOk = async () => {
    await editForm.validate();
    setEditVisible(false);
  };
  //取消弹框
  const handleEditCancel = () => {
    setEditVisible(false);
  };
  //编辑大屏
  const handleEdit = (item: dataList) => {
    console.log(item, '跳转到第三方');
  };
  //预览
  const handlePreview = () => {
    console.log('预览');
  };

  // 删除弹框
  const [deleteVisible, setDeleteVisible] = useState<boolean>(false);
  const [ModalScreenName, setModalScreenName] = useState('');
  const handleDelete = (item: dataList) => {
    console.log(item);
    setModalScreenName(item.name);
    setDeleteVisible(true);
  };
  const handleDeleteScreenOk = () => {
    console.log('删除当前screen');
    setDeleteVisible(false);
  };

  // 另存为模板
  const [onSaveVisible, setOnSaveVisible] = useState<boolean>(false);
  const handleOnSaveAs = () => {
    console.log('存模板');
    setOnSaveVisible(true);
  };
  const handleOnSaveAsOk = () => {
    console.log('确认存模板');
    setOnSaveVisible(false);
  };

  return (
    <div className={styles.datasetPage}>
      <div className={styles.dataFilter}>
        <div className={styles.datasetTitle}>大屏</div>
        <Button type="primary" icon={<IconPlus />} onClick={handleAdd}>
          新建大屏
        </Button>
      </div>
      <Input
        className={styles.appInput}
        allowClear
        suffix={<IconSearch />}
        onChange={handleSearchChange}
        placeholder="搜索"
      />
      <Spin className={styles.appListLoading} loading={loading} size={40} tip="加载中...">
        <div className={styles.appList}>
          {dataList?.map((item) => (
            <ScreenCard
              key={item.id}
              item={item}
              onDelete={handleDelete}
              onSaveAs={handleOnSaveAs}
              onEditScreen={handleEditScreen}
              onEdit={handleEdit}
              onPreview={handlePreview}
            />
          ))}
        </div>
      </Spin>
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
            <Input placeholder="" />
          </FormItem>
          <FormItem label="大屏描述" field="desc" rules={[{ required: true, message: '请输入大屏描述' }]}>
            <Input placeholder="" />
          </FormItem>
        </Form>
      </Modal>
      {/* 删除卡片弹框 */}
      <Modal
        title={<div style={{ textAlign: 'left', fontWeight: 500 }}>确认删除</div>}
        visible={deleteVisible}
        onOk={handleDeleteScreenOk}
        onCancel={() => setDeleteVisible(false)}
        autoFocus={false}
        focusLock={true}
        footer={
          <>
            <Button type="secondary" size="default" style={{ marginRight: 10 }} onClick={() => setDeleteVisible(false)}>
              取消
            </Button>
            <Button type="primary" status="danger" size="default" onClick={handleDeleteScreenOk}>
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
      {/* 另存为模板弹框 */}
      <Modal
        title={<div style={{ textAlign: 'left' }}>另存为模板</div>}
        visible={onSaveVisible}
        onOk={handleOnSaveAsOk}
        onCancel={() => setOnSaveVisible(false)}
        autoFocus={false}
        focusLock={true}
      >
        <div className={styles.templateModal}>
          <p>确定另存为模板吗?</p>
          <p>确定后将新增一个应用模板</p>
        </div>
      </Modal>
      {/* 新建大屏 */}
      <CreateScreenModal
        title="新建大屏"
        type={'screen'}
        handleCreate={handleCreateOk}
        onCancel={() => {
          setVisibleCreateScreenForm('');
        }}
        form={createForm}
        visibleCreateForm={visibleCreateScreenForm}
      />
    </div>
  );
};
export default LargeScreen;
