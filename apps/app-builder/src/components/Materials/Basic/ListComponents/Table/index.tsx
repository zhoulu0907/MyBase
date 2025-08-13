import { STATUS_OPTIONS, STATUS_VALUES } from '@/components/Materials/constants';
import { Button, Form, Input, Table } from '@arco-design/web-react';
import { IconDelete, IconEdit } from '@arco-design/web-react/icon';
import { memo, useEffect, useState } from 'react';

import { EDITOR_TYPES } from '@/pages/Editor/utils/const';
import { useNavigate } from 'react-router-dom';
import styles from './index.module.less';
import type { XTableConfig } from './schema';

const opearate: any = {
  title: '操作',
  dataIndex: 'op',
  fixed: null,
  width: '110px',
  render: () => (
    <>
      <Button type="text" style={{ marginRight: 5 }} icon={<IconEdit />} />
      <Button status="danger" type="text" icon={<IconDelete />} />
    </>
  )
};
const XTable = memo((props: XTableConfig) => {
  const navigate = useNavigate();

  const {
    label,
    status,
    defaultValue,
    searchItems,
    columns,
    hover,
    border,
    borderCell,
    showHeader,
    stripe,
    pagePosition,
    pageSize,
    showTotal,
    showOpearate,
    fixedOpearate
  } = props;

  const [finalColumns, setFinalColumns] = useState<any[]>();

  useEffect(() => {
    if (Object.keys(columns as any).length) {
      columns?.map((v) => {
        return {
          ...v,
          ellipsis: true,
          width: v.width + 'px'
        };
      });
    }
    if (showOpearate) {
      opearate.fixed = fixedOpearate ? 'right' : null;
      setFinalColumns([...(columns as any), opearate]);
    } else {
      setFinalColumns((pre) => pre?.filter((v) => v.dataIndex !== 'op'));
    }
  }, [showOpearate, columns, fixedOpearate]);

  const handleCreate = () => {
    const hash = window.location.hash;
    const queryIndex = hash.indexOf('?');
    if (queryIndex !== -1) {
      const queryString = hash.substring(queryIndex + 1);
      const params = new URLSearchParams(queryString);
      const pageSetCode = params.get('pageSetCode') || '';
      navigate(`/onebase/preview-app/preview?pageSetCode=${pageSetCode}&pageType=${EDITOR_TYPES.FORM_EDITOR}`);
    }
  };

  return (
    <div
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1
      }}
    >
      <div className={styles.tableHeader}>
        <div className={styles.searchGroup}>
          {searchItems?.map((item, idx) => (
            <Form.Item
              key={idx}
              className={styles.searchItem}
              label={
                <div
                  style={{
                    textAlign: 'right',
                    whiteSpace: 'nowrap',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis'
                  }}
                >
                  {`${item.label}`}
                </div>
              }
              style={{
                minWidth: '280px',
                maxWidth: '400px',
                marginBottom: 0
              }}
              layout={'horizontal'}
            >
              <Input placeholder={`请输入${item.label}`} />
            </Form.Item>
          ))}
        </div>

        <div className={styles.tableHeaderButton}>
          <Button type="primary">查询</Button>
          <Button type="primary">重置</Button>
          <Button type="primary" onClick={handleCreate}>
            新增
          </Button>
          {/* <Button
            type="outline"
            style={{
              border: 'none'
            }}
          >
            <IconDown />
            <span>展开</span>
          </Button> */}
        </div>
      </div>
      <div>
        <Form.Item
          label={label}
          layout={'vertical'}
          style={{
            width: '100%',
            pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset'
          }}
        >
          <Table
            scroll={{
              x: 'max-content'
            }}
            border={border}
            borderCell={borderCell}
            showHeader={showHeader}
            stripe={stripe}
            hover={hover}
            columns={finalColumns}
            data={defaultValue}
            pagePosition={pagePosition}
            pagination={{
              pageSize,
              showTotal
            }}
          />
        </Form.Item>
      </div>
    </div>
  );
});

export default XTable;
