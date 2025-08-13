import { STATUS_OPTIONS, STATUS_VALUES } from '@/components/Materials/constants';
import { Button, Form, Input, Table } from '@arco-design/web-react';
import { IconDelete, IconEdit } from '@arco-design/web-react/icon';
import { memo, useEffect, useState } from 'react';

import { EDITOR_TYPES } from '@/pages/Editor/utils/const';
import { dataMethodDelete, dataMethodPage, type DeleteMethodParam, type PageMethodParam } from '@onebase/app';
import { useNavigate } from 'react-router-dom';
import styles from './index.module.less';
import type { XTableConfig } from './schema';

const XTable = memo((props: XTableConfig & { edit?: boolean }) => {
  const { edit = true } = props;
  const navigate = useNavigate();

  const {
    label,
    status,
    defaultValue,
    metaData,
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

  const [tableData, setTableData] = useState<any[]>([]);

  const opearate: any = {
    title: '操作',
    dataIndex: 'op',
    fixed: null,
    width: '110px',
    render: (_: any, record: any) => (
      <>
        <Button
          type="text"
          style={{ marginRight: 5 }}
          icon={<IconEdit />}
          onClick={() => {
            handleEdit(record.id);
          }}
        />
        <Button
          status="danger"
          type="text"
          icon={<IconDelete />}
          onClick={() => {
            handleDelete(record.id);
          }}
        />
      </>
    )
  };

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

  useEffect(() => {
    console.log(finalColumns);
    if (finalColumns) {
      handlePage();
    }
  }, [finalColumns]);

  const handleCreate = () => {
    if (edit) {
      return;
    }
    const hash = window.location.hash;
    const queryIndex = hash.indexOf('?');
    if (queryIndex !== -1) {
      const queryString = hash.substring(queryIndex + 1);
      const params = new URLSearchParams(queryString);
      const pageSetCode = params.get('pageSetCode') || '';
      navigate(`/onebase/preview-app/preview?pageSetCode=${pageSetCode}&pageType=${EDITOR_TYPES.FORM_EDITOR}`);
    }
  };

  const handlePage = async () => {
    if (edit) {
      return;
    }
    const req: PageMethodParam = {
      entityId: metaData,
      pageNo: 1,
      pageSize: 10
    };
    const res = await dataMethodPage(req);
    console.log(res);

    const { list, total } = res;

    const newTableData = (list || []).map((item: any) => {
      return {
        ...item.data,
        key: item.data.id
      };
    });

    console.log(newTableData);
    setTableData(newTableData);
  };

  const handleDelete = async (id: string) => {
    if (edit) {
      return;
    }
    console.log(id);
    const req: DeleteMethodParam = {
      entityId: metaData,
      id: id
    };
    const res = await dataMethodDelete(req);
    console.log(res);

    handlePage();
  };

  const handleEdit = (id: string) => {
    if (edit) {
      return;
    }
    console.log(id);
    const hash = window.location.hash;
    const queryIndex = hash.indexOf('?');
    if (queryIndex !== -1) {
      const queryString = hash.substring(queryIndex + 1);
      const params = new URLSearchParams(queryString);
      const pageSetCode = params.get('pageSetCode') || '';
      navigate(`/onebase/preview-app/preview?pageSetCode=${pageSetCode}&pageType=${EDITOR_TYPES.FORM_EDITOR}&id=${id}`);
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
            data={tableData}
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
