import { useEffect, useState, type FC } from 'react';
import { Button, Input, Spin, Pagination } from '@arco-design/web-react';
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
        id: '1',
        name: '这是一个大屏名称2',
        state: '已发布'
      },
      {
        id: '1',
        name: '这是一个大屏名称4',
        state: '已发布'
      },
      {
        id: '1',
        name: 'screen1',
        state: '已发布'
      }
    ]);
  }, [dataList]);
  const handleSearchChange = () => {};
  const handleAdd = () => {};
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
            <ScreenCard key={item.id} item={item} />
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
    </div>
  );
};
export default LargeScreen;
