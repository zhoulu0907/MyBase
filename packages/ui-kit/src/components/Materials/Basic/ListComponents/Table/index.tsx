import { Button, Form, Input, Message, Popconfirm, Table } from '@arco-design/web-react';
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
import { pagesRuntimeSignal } from '@onebase/common';
import { ENTITY_FIELD_TYPE } from '../../../../DataFactory/const';
import { RedirectMethod } from '../../../constants';
import './index.css';
import type { XTableConfig } from './schema';

const leftPanelWidth = 318;
const rightPanelWidth = 310;
const canvasPaddingWidth = 40 + 32 + 10;
const canvasMarginWidth = 10;
const componentMaxWidth = leftPanelWidth + rightPanelWidth + canvasPaddingWidth + canvasMarginWidth;

const XTable = memo((props: XTableConfig & { runtime?: boolean; showFromPageData?: Function }) => {
  const { setDrawerVisible, setDrawerPageId, setDetailPageViewId } = pagesRuntimeSignal;

  const { runtime = true, showFromPageData } = props;

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
    labelColSpan,
    advancedRowRedirect,
    redirectPageId,
    redirectMethod
  } = props;

  const [finalColumns, setFinalColumns] = useState<any[]>();
  // 实际查询用的参数
  let queryData: object = {};

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
          onClick={(event) => {
            handleEdit(record.id, true);
            event.stopPropagation();
          }}
        />
        <Popconfirm
          focusLock
          title="确认删除"
          content="确定要删除这条数据吗？"
          onOk={(event) => {
            handleDelete(record.id);
            event.stopPropagation();
          }}
        >
          <Button status="danger" type="text" icon={<IconDelete />} />
        </Popconfirm>
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
    console.log('点击新增');

    console.log('runtime: ', runtime);
    if (!runtime) {
      return;
    }

    showFromPageData?.(null, true);
  };

  // 查询
  const handleSearch = () => {
    queryData = form.getFieldsValue();
    setTablePageNo(1);
    handlePage();
  };

  // 重置
  const handleReset = () => {
    form.resetFields();
    queryData = {};
    setTablePageNo(1);
    handlePage();
  };

  const handlePage = async () => {
    if (!runtime) {
      return;
    }
    const req: PageMethodParam = {
      entityId: metaData,
      pageNo: tablePageNo,
      pageSize: pageSize || 10,
      filters: queryData
    };
    const res = await dataMethodPage(req);

    const mainMetaData = await getEntityFieldsWithChildren(metaData);

    const { list, total } = res;

    const newTableData = (list || []).map((item: any) => {
      //   console.log(item);
      const newItem = item.data;
      Object.entries(newItem).forEach(([key, value]) => {
        // console.log(key, value);
        // 优化：减少重复查找，提升可读性和性能
        if (Array.isArray(mainMetaData?.parentFields)) {
          const field = mainMetaData.parentFields.find(
            (field: AppEntityField) => field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE.DATE.VALUE
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
    if (res) {
      Message.success('删除成功');
    }
    handlePage();
  };

  const handleEdit = (id: string, toFormPage: boolean) => {
    if (!runtime) {
      return;
    }

    showFromPageData?.(id, toFormPage);
  };

  // 行点击事件
  const handleRowClick = (record: any) => {
    if (advancedRowRedirect) {
      if (redirectMethod === RedirectMethod.DRAWER) {
        // 打开抽屉显示详情
        console.log(redirectPageId);
        setDrawerVisible(true);
        redirectPageId && setDrawerPageId(redirectPageId);

        handleEdit(record.id, false);
        if (runtime) {
          redirectPageId && setDetailPageViewId(redirectPageId);
        }
      } else if (redirectMethod === RedirectMethod.NEW_TAB) {
        // 打开新的标签页
      }
    }
  };

  const [form] = Form.useForm();

  return (
    <div
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
        display: runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 'none' : 'unset'
      }}
    >
      <div className="tableHeader">
        <Form form={form} className="searchGroup">
          {searchItems?.map((item, idx) => (
            <Form.Item
              key={idx}
              className="searchItem"
              field={item.value}
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
        </Form>

        <div className="tableHeaderButton">
          {searchItems?.length ? (
            <>
              <Button type="primary" onClick={handleSearch}>
                查询
              </Button>
              <Button type="primary" onClick={handleReset}>
                重置
              </Button>
            </>
          ) : null}
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
            maxWidth: runtime ? '100%' : `calc(100vw - ${componentMaxWidth}px)`,
            pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset'
          }}
        >
          <div style={{ width: '100%' }}>
            <Table
              scroll={{
                x: 'max-content'
              }}
              onRow={(record, index) => {
                return {
                  onClick: (event) => {
                    handleRowClick(record);
                  }
                };
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
          </div>
        </Form.Item>
      </div>
    </div>
  );
});

export default XTable;
