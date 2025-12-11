import { useEffect, useState, type FC } from 'react';
import { Button, Input, Spin, Pagination, Modal } from '@arco-design/web-react';
import { IconPlus, IconSearch } from '@arco-design/web-react/icon';
import ScreenCard from '../ScreenCard';
import styles from './index.module.less';
interface dataList {
  id: string;
  name: string;
  state: string;
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
        state: '已发布'
      },
      {
        id: '2',
        name: '这是一个大屏名称2',
        state: '已发布'
      },
      {
        id: '3',
        name: '这是一个大屏名称4',
        state: '已发布'
      },
      {
        id: '4',
        name: 'screen1',
        state: '已发布'
      }
    ]);
  }, []);
  const handleSearchChange = () => {};
  const handleAdd = () => {};

  const [deleteVisible, setDeleteVisible] = useState<boolean>(false);
  const handleDelete = (item: dataList) => {
    // setAppName('');
    // setDeleteApp(item);
    console.log(item);
    setDeleteVisible(true);
  };
  const handleDeleteScreen = () => {
    console.log('删除当前screen');
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
            <ScreenCard key={item.id} item={item} onDelete={handleDelete} />
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
      {/* 删除卡片弹框 */}
      <Modal
        title={<div style={{ textAlign: 'left' }}>确认删除</div>}
        visible={deleteVisible}
        onOk={handleDeleteScreen}
        onCancel={() => setDeleteVisible(false)}
        autoFocus={false}
        focusLock={true}
      >
        <p>您确定要删除此大屏吗？删除后将无法恢复，请谨慎操作。</p>
        <div>大屏名称：这是一个大屏名称</div>
      </Modal>
    </div>
  );
};
export default LargeScreen;
