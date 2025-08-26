import { Button, Form, Input, Table } from '@arco-design/web-react';
import { IconDelete, IconEdit } from '@arco-design/web-react/icon';
import { memo, useEffect, useState } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';

import {
  dataMethodDelete,
  dataMethodPage,
  getEntityFieldsWithChildren,
  type AppEntityField,
  type DeleteMethodParam,
  type PageMethodParam
} from '@onebase/app';
import { ENTITY_FIELD_TYPE_LABEL } from '../../../../DataFactory/const';
import './index.css';
import type { XTableConfig } from './schema';

const XTable = memo((props: XTableConfig & { runtime?: boolean; toCreatePage?: Function }) => {
  const { runtime = true, toCreatePage } = props;

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
    fixedOpearate,
    labelColSpan
  } = props;

  const [finalColumns, setFinalColumns] = useState<any[]>();

  const [tableData, setTableData] = useState<any[]>([]);
  const [tableTotal, setTableTotal] = useState<number>(0);
  const [tablePageNo, setTablePageNo] = useState<number>(1);

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
    if (finalColumns && metaData) {
      handlePage();
    }
  }, [finalColumns, tablePageNo, metaData]);

  const handleCreate = () => {
    if (!runtime) {
      return;
    }

    toCreatePage?.();
  };

  const handlePage = async () => {
    const req: PageMethodParam = {
      entityId: metaData,
      pageNo: tablePageNo,
      pageSize: pageSize || 10
    };
    const res = await dataMethodPage(req);

    const mainMetaData = await getEntityFieldsWithChildren(metaData);

    const { list, total } = res;

    const newTableData = (list || []).map((item: any) => {
      //   console.log(item);
      const newItem = item.data;
      Object.entries(newItem).forEach(([key, value]) => {
        console.log(key, value);
        // 优化：减少重复查找，提升可读性和性能
        if (Array.isArray(mainMetaData?.parentFields)) {
          const field = mainMetaData.parentFields.find(
            (field: AppEntityField) => field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE_LABEL.DATE
          );
          if (field && newItem[key]) {
            // 仅当字段类型为日期且有值时格式化
            const dateValue = new Date(newItem[key]);
            if (!isNaN(dateValue.getTime())) {
              newItem[key] = dateValue.toLocaleDateString();
            }
          }
        }
      });

      return {
        ...newItem,
        key: item.data.id
      };
    });

    console.log(newTableData);
    setTableData(newTableData);
    setTableTotal(total);
  };

  const handleDelete = async (id: string) => {
    if (!runtime) {
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
    if (!runtime) {
      return;
    }

    toCreatePage?.(id);
  };

  return (
    <div
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1
      }}
    >
      <div className="tableHeader">
        <div className="searchGroup">
          {searchItems?.map((item, idx) => (
            <Form.Item
              key={idx}
              className="searchItem"
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
                minWidth: '350px',
                maxWidth: '400px',
                marginBottom: 0
              }}
              layout={'horizontal'}
              labelCol={{
                style: { width: labelColSpan, flex: 'unset' }
              }}
              wrapperCol={{ style: { flex: 1 } }}
            >
              <Input placeholder={`请输入${item.label}`} />
            </Form.Item>
          ))}
        </div>

        <div className="tableHeaderButton">
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
              showTotal,
              current: tablePageNo,
              total: tableTotal,
              onChange: (pageNo: number) => {
                setTablePageNo(pageNo);
              }
            }}
          />
        </Form.Item>
      </div>
    </div>
  );
});

export default XTable;
