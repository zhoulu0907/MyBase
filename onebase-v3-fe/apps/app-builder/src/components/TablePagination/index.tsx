import { InputNumber, Pagination, Select } from '@arco-design/web-react';
import type { PaginationProps } from '@arco-design/web-react';
import React from 'react';
import styles from './index.module.less';

interface TablePaginationProps extends Omit<PaginationProps, 'showTotal' | 'showJumper' | 'sizeCanChange'> {
  sizeOptions?: number[];
}

/**
 * 自定义分页组件
 * 布局顺序：共X页Y条 | 每页X条 | 翻页 | 跳转X页
 */
const TablePagination: React.FC<TablePaginationProps> = ({
  current,
  pageSize,
  total,
  onChange,
  onPageSizeChange,
  sizeOptions = [10, 20, 50],
  ...props
}) => {
  const totalPages = Math.ceil((total || 0) / (pageSize || 10));

  return (
    <div className={styles.paginationWrapper}>
      {/* 共多少页，多少条 */}
      <span className={styles.totalText}>
        共 {totalPages} 页，{total} 条
      </span>

      {/* 每页条数切换 */}
      <span className={styles.pageSizeSelect}>
        <span className={styles.pageSizeLabel}>每页</span>
        <Select
          value={pageSize}
          onChange={(value) => onPageSizeChange?.(value, current || 1)}
          style={{ width: 80 }}
          size="small"
        >
          {sizeOptions.map((option) => (
            <Select.Option key={option} value={option}>
              {option} 条
            </Select.Option>
          ))}
        </Select>
      </span>

      {/* 翻页 */}
      <Pagination
        current={current}
        pageSize={pageSize}
        total={total}
        onChange={onChange}
        showTotal={false}
        showJumper={false}
        sizeCanChange={false}
        size="small"
        {...props}
      />

      {/* 跳转到X页 */}
      <span className={styles.jumper}>
        <span className={styles.jumperLabel}>跳转到</span>
        <InputNumber
          value={current}
          onChange={(value) => {
            if (value && value >= 1 && value <= totalPages) {
              onChange?.(value);
            }
          }}
          style={{ width: 60 }}
          size="small"
        />
        <span className={styles.jumperSuffix}>页</span>
      </span>
    </div>
  );
};

export default TablePagination;