import type { CSSProperties } from 'react';
import { memo, useMemo, useState, useEffect } from 'react';
import { Table, TableColumnProps } from '@arco-design/web-react';
import { WORKBENCH_STATUS_OPTIONS, WORKBENCH_STATUS_VALUES, WORKBENCH_THEME_OPTIONS } from '../../core/constants';
import type { XDataListConfig } from './schema';
import styles from './index.module.css';

const containerStyle: CSSProperties = {
  width: '100%',
  padding: '16px',
  borderRadius: 8,
  boxSizing: 'border-box'
};

const XDataList = memo((props: XDataListConfig & { runtime?: boolean }) => {
  const { status, runtime, label, tableUuid } = props;
  const hiddenStatusValue = WORKBENCH_STATUS_VALUES[WORKBENCH_STATUS_OPTIONS.HIDDEN];

  const columns = useMemo(() => [
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
  ], []);
  const dataSource = useMemo(() => [
    {
      title: '标题1',
      content: '内容1'
    }
  ], []);

  if (runtime && status === hiddenStatusValue) {
    return null;
  }

  return (
    <div style={containerStyle}>
      <div className={styles.dataListHeader}>
        {label?.display && (
          <span className={styles.dataListHeaderTitle}>{label?.text}</span>
        )}
      </div>

      <div>
        <Table data={dataSource} columns={columns} />
      </div>
    </div>
  );
});

export default XDataList;

