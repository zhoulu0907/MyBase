import { memo, useState, useEffect, useCallback } from 'react';
import { Table } from '@arco-design/web-react';
import { dataMethodPageV2, menuSignal, type PageMethodV2Params } from '@onebase/app';
import { WORKBENCH_STATUS_OPTIONS, WORKBENCH_STATUS_VALUES } from '../../core/constants';
import tableIcon from '@/assets/workbench/data-list/table.svg';
import type { XDataListConfig } from './schema';
import styles from './index.module.css';


interface ColumnDef {
  title: string;
  dataIndex: string;
  key: string;
  [key: string]: unknown;
}

type RowData = Record<string, unknown>;

const INIT_COLUMNS: ColumnDef[] = [
  { title: '序号', dataIndex: 'index', key: 'index' },
  { title: '单号', dataIndex: 'no', key: 'no' },
  { title: '标题/名称', dataIndex: 'title', key: 'title' },
  { title: '类型', dataIndex: 'type', key: 'type' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '负责人', dataIndex: 'owner', key: 'owner' },
  { title: '所属部门', dataIndex: 'department', key: 'department' },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt' },
];

const INIT_DATA_SOURCE: RowData[] = [
  { index: 1, no: 'NO20260312001', title: '项目立项申请-01', type: '立项申请', status: '待提交', owner: '张三', department: '技术部', createdAt: '2026-3-12 9:00' },
  { index: 2, no: 'NO20260312002', title: '项目立项申请-02', type: '立项申请', status: '审批中', owner: '李四', department: '运营部', createdAt: '2026-3-13 9:00' },
  { index: 3, no: 'NO20260312003', title: '设备维修工单-01', type: '维修工单', status: '已完成', owner: '王五', department: '设备部', createdAt: '2026-3-14 9:00' },
  { index: 4, no: 'NO20260312004', title: '采购申请单-01', type: '采购申请', status: '已驳回', owner: '赵六', department: '采购部', createdAt: '2026-3-15 9:00' },
  { index: 5, no: 'NO20260312005', title: '巡检任务-01', type: '巡检任务', status: '处理中', owner: '陈晨', department: '安全部', createdAt: '2026-3-16 9:00' },
  { index: 6, no: 'NO20260312006', title: '合同审批-01', type: '合同审批', status: '待审核', owner: '孙敏', department: '法务部', createdAt: '2026-3-17 9:00' },
  { index: 7, no: 'NO20260312007', title: '资产入库登记-01', type: '资产管理', status: '已完成', owner: '周婷', department: '行政部', createdAt: '2026-3-18 9:00' },
  { index: 8, no: 'NO20260312008', title: '用印申请-01', type: '行政申请', status: '已撤回', owner: '吴杰', department: '综合部', createdAt: '2026-3-19 9:00' },
  { index: 9, no: 'NO20260312009', title: '费用报销申请-01', type: '费用报销', status: '审核中', owner: '刘洋', department: '财务部', createdAt: '2026-3-20 9:00' },
  { index: 10, no: 'NO20260312010', title: '会议室申请-01', type: '行政申请', status: '已完成', owner: '何静', department: '综合管理部', createdAt: '2026-3-21 9:00' },
];

function flattenRowValue(value: unknown): unknown {
  if (Array.isArray(value)) {
    if (value.length === 0) return '';
    const first = value[0];
    if (first && typeof first === 'object' && 'name' in first) return (first as { name: string }).name;
    return String(first);
  }
  if (value && typeof value === 'object') {
    if ('name' in value) return (value as { name: string }).name;
    if ('id' in value) return (value as { id: string }).id;
    return '';
  }
  return value;
}

function processTableData(data: RowData[]): RowData[] {
  return data.map((item) => {
    const processed: RowData = {};
    Object.keys(item).forEach((key) => {
      processed[key] = flattenRowValue(item[key]);
    });
    return processed;
  });
}

// 预览态: 根据列定义和条数生成mock数据
function generateMockData(columns: ColumnDef[], count: number): RowData[] {
  const total = Math.max(count, 0);
  return Array.from({ length: total }, (_, i) => {
    const row: RowData = {};
    columns.forEach((col) => {
      row[col.dataIndex] = `${col.title}${i + 1}`;
    });
    return row;
  });
}

const XDataList = memo((props: XDataListConfig & { runtime?: boolean }) => {
  const { status, runtime, label, tableInfo, dataCount } = props; // TODO 其他表格属性待补足
  const hiddenStatusValue = WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.HIDDEN];

  const [finalCols, setFinalCols] = useState<ColumnDef[]>(INIT_COLUMNS);
  const [finalData, setFinalData] = useState<RowData[]>(INIT_DATA_SOURCE.slice(0, dataCount));
  const [pagination, setPagination] = useState({
    showTotal: true,
    total: 0,
    pageSize: dataCount,
    current: 1,
    pageSizeChangeResetCurrent: true,
  });

  const { curMenu } = menuSignal;

  // 运行态：从接口获取表格数据
  const loadRuntimeData = useCallback(async () => {
    const req: PageMethodV2Params = {
      pageNo: 1,
      pageSize: dataCount,
    };
    try {
      const res = await dataMethodPageV2(tableInfo.tableName, curMenu.value?.id, req);
      if (res) {
        setFinalData(processTableData(res.list || []));
        setPagination((prev) => ({ ...prev, total: res.total, pageSize: dataCount }));
      }
    } catch (error) {
      console.error('[XDataList] 获取表格数据失败', error);
    }
  }, [tableInfo?.tableName, curMenu.value?.id, dataCount]);

  useEffect(() => {
    const customCols = tableInfo?.columns as ColumnDef[] | undefined;

    if (customCols && customCols.length > 0) {
      setFinalCols(customCols);
      if (runtime) {
        loadRuntimeData();
      } else {
        setFinalData(generateMockData(customCols, dataCount));
        setPagination({...pagination, total: dataCount});
      }
    } else {
      setFinalCols(INIT_COLUMNS);
      setFinalData(INIT_DATA_SOURCE.slice(0, dataCount));
      setPagination({...pagination, total: dataCount});
    }
  }, [tableInfo, dataCount, runtime]);

  if (runtime && status === hiddenStatusValue) {
    return null;
  }

  return (
    <div className={styles.containerStyle}>
      <div className={styles.dataListHeader}>
        {label?.display && (
          <span className={styles.dataListHeaderTitle}>{label?.text}</span>
        )}
      </div>
      <div>
        <Table
          data={finalData}
          columns={finalCols}
          pagination={pagination}
          noDataElement={
            <div className={styles.emptyText}>
              <img src={tableIcon} alt="tableIcon" />
              <span>请选择要插入的数据列表</span>
            </div>
          }
        />
      </div>
    </div>
  );
});

export default XDataList;
