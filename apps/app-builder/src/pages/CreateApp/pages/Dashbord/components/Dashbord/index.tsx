import CreateDashboardModal from '@/components/CreateDashboardModal';
import { Button, Form, Input, Modal, Pagination, Spin } from '@arco-design/web-react';
import { IconPlus, IconSearch } from '@arco-design/web-react/icon';
import { useEffect, useState, type FC } from 'react';
import ScreenCard from '../DashbordCard';
import styles from './index.module.less';
import { getDashboardListApi, editDashboardInfoApi, deleteDashboardApi } from '@onebase/app';
import { TokenManager } from '@onebase/common';
const FormItem = Form.Item;
const { useForm } = Form;
interface dataList {
  appId: string;
  id: string;
  projectName: string;
  updateTime: string;
  indexImage: string;
  remarks: string;
  state: number;
  desc: string;
}

const LargeScreen: FC = () => {
  const [loading, setLoading] = useState(false);
  const [dataList, setDataList] = useState<dataList[]>([]);
  const [total, setTotal] = useState(30);
  const [pageSize, setPageSize] = useState<number>(10);
  const [pageNo, setPageNo] = useState(1);
  const [dashboardName, setDashboardName] = useState<string>('');

  const tokenInfo = TokenManager.getTokenInfo();
  const tenantId = tokenInfo?.tenantId;
  const accessToken = tokenInfo?.accessToken;
  useEffect(() => {
    setLoading(false);
    console.log('tokenInfo:', tokenInfo);
    getDashboardList();
  }, []);

  const getDashboardList = async () => {
    const params = {
      page: pageNo,
      limit: pageSize
    };
    const res = await getDashboardListApi(params);
    console.log('res:', res);
    setDataList(res.list);
    setTotal(res.total);
  };
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
    console.log('tiem:', item);
    editForm.setFieldValue('projectName', item.projectName);
    editForm.setFieldValue('remarks', item.remarks);
    editForm.setFieldValue('id', item.id);
    setEditVisible(true);
  };
  const handleEditOk = async () => {
    editForm.validate(async (error) => {
      if (error !== null) return;
      try {
        const params = await editForm.validate();
        const res = await editDashboardInfoApi(params);
        console.log('res:', res);
        setEditVisible(false);
        getDashboardList();
      } catch (error) {}
    });
  };
  //取消弹框
  const handleEditCancel = () => {
    setEditVisible(false);
  };
  //编辑大屏
  const handleEdit = (item: dataList) => {
    window.open(`http://s25029301301.dev.internal.virtueit.net:81/v0/appdashboard/#/chart/home/${item.id}`, '_blank');
  };
  //预览
  const handlePreview = (item: dataList) => {
    console.log('预览 item:', item);
    window.open(
      `http://s25029301301.dev.internal.virtueit.net:81/v0/appdashboard/#/chart/preview/${item.id}`,
      '_blank'
    );
  };
  // 删除弹框
  const [deleteVisible, setDeleteVisible] = useState<boolean>(false);
  const [ModalScreenName, setModalScreenName] = useState('');
  const [screenId, setScreenId] = useState('');
  const handleDelete = (item: dataList) => {
    console.log(item);
    setModalScreenName(item.projectName);
    setScreenId(item.id);
    setDeleteVisible(true);
  };
  const handleDeleteScreenOk = async () => {
    console.log('删除当前screen');
    await deleteDashboardApi(screenId);
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
        value={dashboardName}
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
          <FormItem label="大屏名称" field="projectName" rules={[{ required: true, message: '请输入大屏名称' }]}>
            <Input placeholder="" />
          </FormItem>
          <FormItem label="大屏描述" field="remarks" rules={[{ required: true, message: '请输入大屏描述' }]}>
            <Input placeholder="" />
          </FormItem>
          <FormItem field="id" noStyle>
            <Input type="hidden" />
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
      <CreateDashboardModal
        title="新建大屏"
        type={'dashboard'}
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
