import { memo, useEffect, useState } from 'react';
import { Button, Form, Input, Message, Popconfirm, Space, Table, Tooltip } from '@arco-design/web-react';
import { STATUS_OPTIONS, STATUS_VALUES, BUTTON_OPTIONS, BUTTON_VALUES, TableOperationButton, TableOperationButtonStyle } from '../../../constants';

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
import type { XTableConfig } from './schema';
import DynamicIcon from '@/components/DynamicIcon';
import { iconMap } from '@/utils/const';
import './index.css';

const leftPanelWidth = 318;
const rightPanelWidth = 310;
const canvasPaddingWidth = 40 + 32 + 10;
const canvasMarginWidth = 10;
const componentMaxWidth = leftPanelWidth + rightPanelWidth + canvasPaddingWidth + canvasMarginWidth;

const XTable = memo((props: XTableConfig & { runtime?: boolean; showFromPageData?: Function, showAddBtn?: boolean }) => {
  const { setDrawerVisible, setDrawerPageId, setDetailPageViewId } = pagesRuntimeSignal;
  const { runtime = true, showFromPageData, showAddBtn = true } = props;
  const hasOperationPermission = true;

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
    sortByObject,
    advancedRowRedirect,
    redirectPageId,
    redirectMethod,
    operationButton,
    advancedButtonPermission,
    // operationButtonCollpaseNumber,
    operationButtonShowType,
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
    render: (_: any, record: any) => {
      if (advancedButtonPermission === BUTTON_VALUES[BUTTON_OPTIONS.HIDDEN] && !hasOperationPermission) return;
      const isDisabled = advancedButtonPermission === BUTTON_VALUES[BUTTON_OPTIONS.DISABLED] && !hasOperationPermission;
      return (
        <Space>
          {
            operationButton?.map((opearate, index) => (
              <Tooltip content={!hasOperationPermission && "无操作权限"} key={index}>
                {(opearate.type === TableOperationButton.EDIT && opearate.display) && (
                  <div
                    style={{ whiteSpace: 'nowrap', opacity: isDisabled ? .5 : 1, cursor: isDisabled ? 'not-allowed' : 'pointer', pointerEvents: isDisabled ? 'none' : 'auto' }}
                    onClick={(event) => {
                      event.stopPropagation();
                      if (redirectMethod === RedirectMethod.DRAWER) {
                        // 打开抽屉显示详情
                        console.log(redirectPageId);
                        pagesRuntimeSignal.setDrawerVisible(true);
                        redirectPageId && pagesRuntimeSignal.setDrawerPageId(redirectPageId);

                        handleEdit(record.id, false);
                      } else if (redirectMethod === RedirectMethod.NEW_TAB) {
                        // todo
                      } else if (redirectMethod === RedirectMethod.CURRENT_TAB) {
                      } else if (redirectMethod === RedirectMethod.MODAL) {
                      }
                    }}>
                    {<>
                      {(operationButtonShowType === TableOperationButtonStyle.ICON || operationButtonShowType === TableOperationButtonStyle.ALL) && <DynamicIcon
                        IconComponent={iconMap[opearate.buttonIcon as keyof typeof iconMap]}
                        theme="outline"
                        size="16"
                        fill={opearate.iconColor}
                        style={{
                          marginRight: 4
                        }}
                      />}
                      {(operationButtonShowType === TableOperationButtonStyle.TEXT || operationButtonShowType === TableOperationButtonStyle.ALL) && opearate.buttonName}
                    </>
                    }
                  </div>
                )}

                {(opearate.type === TableOperationButton.DELETE && opearate.display) && (
                  <div style={{ whiteSpace: 'nowrap', opacity: isDisabled ? .5 : 1, cursor: isDisabled ? 'not-allowed' : 'pointer', pointerEvents: isDisabled ? 'none' : 'auto' }}>
                    <Popconfirm
                      focusLock
                      title="确认删除"
                      content={opearate.confirmText}
                      onOk={(event) => {
                        event.stopPropagation();
                        handleDelete(record.id);
                        if (opearate.deletedAction === RedirectMethod.REFRESH) {
                          handlePage();
                        } else if (opearate.deletedAction === RedirectMethod.PROMPT_JUMP) {
                          // todo
                        }
                      }}
                    >
                      {(operationButtonShowType === TableOperationButtonStyle.ICON || operationButtonShowType === TableOperationButtonStyle.ALL) && <DynamicIcon
                        IconComponent={iconMap[opearate.buttonIcon as keyof typeof iconMap]}
                        theme="outline"
                        size="16"
                        fill={opearate.iconColor}
                        style={{
                          marginRight: 4
                        }}
                      />}
                      {(operationButtonShowType === TableOperationButtonStyle.TEXT || operationButtonShowType === TableOperationButtonStyle.ALL) && opearate.buttonName}
                    </Popconfirm>
                  </div>
                )}
              </Tooltip>
            ))
          }
        </Space>
      )
    }
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
      setFinalColumns(() => columns?.filter((v) => v.dataIndex !== 'op'));
    }
  }, [showOpearate, columns, fixedOpearate]);

  useEffect(() => {
    if (finalColumns && metaData) {
      handlePage();
    }
  }, [finalColumns, tablePageNo, metaData, sortByObject]);

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

    if (sortByObject?.fieldName) {
      req.sortField = sortByObject.fieldName;
      req.sortDirection = sortByObject.sortBy === 1 ? 'asc' : 'desc';
    }
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
  };

  const handleEdit = (id: string, toFormPage: boolean) => {
    if (!runtime) {
      return;
    }

    showFromPageData?.(id, toFormPage);
  };

  // 行点击事件
  const handleRowClick = (record: any) => {
    if (!runtime) {
      return;
    }

    if (advancedRowRedirect) {
      if (redirectMethod === RedirectMethod.DRAWER) {
        // 打开抽屉显示详情
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
          {showAddBtn && <Button type="primary" onClick={handleCreate}>
            新增
          </Button>}
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
          label={label.display && label.text}
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
