import type { CSSProperties } from 'react';
import { memo, useState, useEffect } from 'react';
import { Table, TableColumnProps } from '@arco-design/web-react';
import { dataMethodPageV2, menuSignal, PageMethodV2Params } from '@onebase/app';
import { WORKBENCH_STATUS_OPTIONS, WORKBENCH_STATUS_VALUES } from '../../core/constants';
import type { XDataListConfig } from './schema';
import styles from './index.module.css';

const initColumns = [
  {
    title: '标题',
    dataIndex: 'title',
    key: 'title'
  },
  {
    title: '内容',
    dataIndex: 'content',
    key: 'content'
  }
];
const initDataSource = [
  {
    id: 'init-1',
    title: '标题1',
    content: '内容1'
  }
];

const XDataList = memo((props: XDataListConfig & { runtime?: boolean }) => {
  const { status, runtime, label, tableInfo } = props; // TODO 其他表格属性待补足
  const hiddenStatusValue = WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.HIDDEN];

  const [finalCols, setFinalCols] = useState<object[]>(initColumns);
  const [finalData, setFinalData] = useState(initDataSource);
  const [pagination, setPagination] = useState({
    showTotal: true,
    total: 0,
    pageSize: 10,
    current: 1,
    pageSizeChangeResetCurrent: true,
  });

  const { curMenu } = menuSignal;

  if (runtime && status === hiddenStatusValue) {
    return null;
  }

  // 处理数据，将对象类型的字段值转换为字符串
  const processTableData = (data: any[]) => {
    return data.map(item => {
      const processedItem = { ...item };
      Object.keys(processedItem).forEach(key => {
        const value = processedItem[key];
        // 如果是数组，取第一个元素的 name 或转换为字符串
        if (Array.isArray(value)) {
          if (value.length > 0 && value[0] && typeof value[0] === 'object' && 'name' in value[0]) {
            processedItem[key] = value[0].name;
          } else if (value.length > 0) {
            processedItem[key] = String(value[0]);
          } else {
            processedItem[key] = '';
          }
        }
        // 如果是对象且有 name 属性，取 name 值
        else if (value && typeof value === 'object' && 'name' in value) {
          processedItem[key] = value.name;
        }
        // 如果是对象但没有 name 属性，取 id 或转换为空字符串
        else if (value && typeof value === 'object') {
          processedItem[key] = value.id || '';
        }
        // 其他情况保持原值
      });
      return processedItem;
    });
  };

  // 获取运行态表格数据
  const getTableData = async() => {
    const req: PageMethodV2Params = {
      pageNo: pagination.current,
      pageSize: pagination.pageSize
    };

    try {
      const res = await dataMethodPageV2(tableInfo.tableName, curMenu.value?.id, req);
      if (res) {
        const processedData = processTableData(res?.list || []);
        setFinalData(processedData);
        setPagination({...pagination, total: res?.total});
      }
    } catch(error) {
      console.log(error)
    }
  };

  useEffect(() => {
    if (tableInfo?.columns?.length > 0) {
      setFinalCols(tableInfo.columns);
    }

    if (runtime) {
      getTableData();
    } else {
      setFinalData([]);
    }
  }, [tableInfo])

  return (
    <div className={styles.containerStyle}>
      <div className={styles.dataListHeader}>
        {label?.display && (
          <span className={styles.dataListHeaderTitle}>{label?.text}</span>
        )}
      </div>

      <div>
        <Table data={finalData} columns={finalCols} pagination={pagination} rowKey="id" />
      </div>
    </div>
  );
});

export default XDataList;

