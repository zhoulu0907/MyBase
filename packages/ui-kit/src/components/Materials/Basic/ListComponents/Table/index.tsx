import { Button, Checkbox, Form, Message, Popconfirm, Space, Table, Tooltip } from '@arco-design/web-react';
import { memo, useEffect, useState } from 'react';
import {
  BUTTON_OPTIONS,
  BUTTON_VALUES,
  RedirectMethod,
  STATUS_OPTIONS,
  STATUS_VALUES,
  TableOperationButton,
  TableOperationButtonStyle
} from '../../../constants';

import DynamicIcon from '@/components/DynamicIcon';
import { iconMap } from '@/utils/const';
import { IconPlus, IconRefresh } from '@arco-design/web-react/icon';
import {
  CATEGORY_TYPE,
  dataMethodDeleteV2,
  dataMethodPageV2,
  DeleteMethodV2Params,
  getEntityFieldsWithChildren,
  menuSignal,
  PageMethodV2Params,
  queryFlowExecForm,
  TRIGGER_EVENTS,
  VALIDATION_TYPE,
  type AppEntityField
} from '@onebase/app';
import {
  isRuntimeEnv,
  pagesRuntimeSignal,
  SYSTEM_FIELD_CREATED_TIME,
  SYSTEM_FIELD_UPDATED_TIME
} from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import dayjs from 'dayjs';
import PreviewRender from 'src/components/render/PreviewRender';
import { useFormEditorSignal } from 'src/signals/page_editor';
import { ENTITY_FIELD_TYPE } from '../../../../DataFactory/const';
import './index.css';
import type { XTableConfig } from './schema';
import TableSearch from './tableSerach';

const leftPanelWidth = 318;
const rightPanelWidth = 310;
const canvasPaddingWidth = 40 + 32 + 10;
const canvasMarginWidth = 10;
const componentMaxWidth = leftPanelWidth + rightPanelWidth + canvasPaddingWidth + canvasMarginWidth;

type XTableSelectProps = {
  showSelect: boolean;
  defaultSelectedId?: string | number | null;
  onSelectedChange?: (value: any | null, fromDoubleClick?: boolean) => void;
  refreshAfterSelect?: boolean;
};

//TODO: 优化元数据的显示内容，根据不同的类型在此显示不同的内容
const renderCellText = (columnId: string, v: any) => {
  if (v === null || v === undefined) return '';

  if (typeof v === 'object') {
    if ('displayValue' in v && typeof (v as any).displayValue !== 'undefined') return (v as any).displayValue;
    if ('userName' in v && typeof (v as any).userName !== 'undefined') return (v as any).userName as any;
    return '';
  }

  if (columnId === SYSTEM_FIELD_CREATED_TIME || columnId === SYSTEM_FIELD_UPDATED_TIME) {
    return dayjs(v).format('YYYY-MM-DD HH:mm:ss');
  }

  return v as any;
};

const XTable = memo(
  (
    props: XTableConfig & {
      runtime?: boolean;
      preview?: boolean;
      showFromPageData?: Function;
      showAddBtn?: boolean;
      refresh?: number;
      xTableSelectProps?: XTableSelectProps;
    }
  ) => {
    useSignals();

    const { pageComponentSchemas: fromPageComponentSchemas, components } = useFormEditorSignal;

    const { curPage, setDrawerVisible, setDrawerPageId, setDetailPageViewId, setRowDataId, setFlows } =
      pagesRuntimeSignal;
    const { runtime = true, showFromPageData, showAddBtn = true, preview } = props;
    const hasOperationPermission = true;

    const {
      id,
      label,
      status,
      defaultValue,
      metaData,
      tableName,
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
      refresh,
      filterCondition
    } = props;

    const { curMenu } = menuSignal;
    const [tableForm] = Form.useForm();

    const [finalColumns, setFinalColumns] = useState<any[]>();
    // 实际查询用的参数
    let queryData: object = {};

    const [tableData, setTableData] = useState<any[]>([]);
    const [tableTotal, setTableTotal] = useState<number>(0);
    const [tablePageNo, setTablePageNo] = useState<number>(1);
    const [initialPageLoaded, setInitialPageLoaded] = useState(false);
    const [selectedRowId, setSelectedRowId] = useState<string | number | null>(
      props?.xTableSelectProps?.defaultSelectedId ?? null
    );

    const opearate: any = {
      title: '操作',
      dataIndex: 'op',
      fixed: null,
      width: '80px',
      headerCellStyle: { textAlign: 'center' },
      //TODO: zhoumingji ,基础组件上不要写这种样式，最好能放到样式文件里
      bodyCellStyle: { padding: '0 8px', textAlign: 'center' },
      // TODO: mickey, 这段重构，现在太长了
      render: (_: any, record: any) => {
        if (advancedButtonPermission === BUTTON_VALUES[BUTTON_OPTIONS.HIDDEN] && !hasOperationPermission) return;
        const isDisabled =
          advancedButtonPermission === BUTTON_VALUES[BUTTON_OPTIONS.DISABLED] && !hasOperationPermission;
        return (
          <Space size={4}>
            {operationButton?.map((opearate, index) => (
              <Tooltip content={!hasOperationPermission && '无操作权限'} key={index}>
                {opearate.type === TableOperationButton.EDIT && opearate.display && (
                  <Button
                    type="text"
                    size="small"
                    style={{ padding: '0 8px' }}
                    onClick={(event) => {
                      event.stopPropagation();
                      handleEdit(record.id, true);
                    }}
                    icon={
                      (operationButtonShowType === TableOperationButtonStyle.ICON ||
                        operationButtonShowType === TableOperationButtonStyle.ALL) && (
                        <DynamicIcon
                          IconComponent={iconMap[opearate.buttonIcon as keyof typeof iconMap]}
                          theme="outline"
                          size="16"
                          fill={opearate.iconColor}
                          style={{
                            marginRight: 2
                          }}
                        />
                      )
                    }
                  >
                    <>
                      {(operationButtonShowType === TableOperationButtonStyle.TEXT ||
                        operationButtonShowType === TableOperationButtonStyle.ALL) &&
                        opearate.buttonName}
                    </>
                  </Button>
                )}

                {opearate.type === TableOperationButton.DELETE && opearate.display && (
                  <div
                    style={{
                      whiteSpace: 'nowrap',
                      opacity: isDisabled ? 0.5 : 1,
                      cursor: isDisabled ? 'not-allowed' : 'pointer',
                      pointerEvents: isDisabled ? 'none' : 'auto',
                      zIndex: 10
                    }}
                    onClick={(event) => {
                      event.stopPropagation();
                    }}
                  >
                    <Popconfirm
                      focusLock
                      title="确认删除"
                      content={opearate.confirmText}
                      disabled={preview}
                      onOk={(event) => {
                        event.stopPropagation();
                        handleDelete(record.id);
                        // if (opearate.deletedAction === RedirectMethod.REFRESH) {
                        //   handlePage();
                        // } else if (opearate.deletedAction === RedirectMethod.PROMPT_JUMP) {
                        //   // todo
                        // }
                      }}
                    >
                      <Button
                        type="text"
                        size="small"
                        disabled={preview}
                        style={{ padding: '0 4px' }}
                        onClick={(event) => {
                          event.stopPropagation();
                          //   handleDelete(record.id);
                        }}
                        status={'danger'}
                        icon={
                          <DynamicIcon
                            IconComponent={iconMap[opearate.buttonIcon as keyof typeof iconMap]}
                            theme="outline"
                            size="16"
                            fill={opearate.iconColor}
                            style={{
                              marginRight: 2
                            }}
                          />
                        }
                      >
                        {(operationButtonShowType === TableOperationButtonStyle.TEXT ||
                          operationButtonShowType === TableOperationButtonStyle.ALL) &&
                          opearate.buttonName}
                      </Button>
                    </Popconfirm>
                  </div>
                )}
              </Tooltip>
            ))}
          </Space>
        );
      }
    };

    useEffect(() => {
      if (refresh) {
        handlePage();
      }
    }, [refresh]);

    useEffect(() => {
      getFinalColumns();
    }, [showOpearate, columns, fixedOpearate, selectedRowId]);

    useEffect(() => {
      if (finalColumns && metaData) {
        handlePage();
      }
      // finalColumns 改变不应该刷新
    }, [tablePageNo, metaData, sortByObject]);

    useEffect(() => {
      if (!initialPageLoaded && finalColumns && metaData) {
        handlePage();
        setInitialPageLoaded(true);
      }
    }, [finalColumns, metaData, initialPageLoaded]);

    const getFinalColumns = async () => {
      let newColumns: any[] = [];
      if (Object.keys(columns as any).length) {
        const mainMetaData = await getEntityFieldsWithChildren(metaData);
        newColumns = (columns || []).map((column) => {
          return {
            ...column,
            ellipsis: true,
            width: column.width + 'px',
            // TODO: zhoumingji ,基础组件上不要写这种样式，最好能放到样式文件里
            bodyCellStyle: { padding: '0 12px' },
            render: (_text: any, _record: any, index: number) => {
              const componentSchemasKeys = Object.keys(fromPageComponentSchemas.value || {});
              const columnId = column.dataIndex;

              const cpId = componentSchemasKeys.find((ele) => {
                return fromPageComponentSchemas.value[ele]?.config?.dataField?.includes(columnId);
              });

              // 表单配置
              if (cpId) {
                // 组件类型
                const cpType = components.value?.find((ele) => ele.id === cpId)?.type;

                // 当前组件配置
                const currentComponentSchemas = fromPageComponentSchemas.value[cpId];
                // 覆盖配置
                let dataField: string[] = [];
                if (Array.isArray(mainMetaData?.parentFields)) {
                  const dataFieldInfo = mainMetaData.parentFields.find(
                    (field: AppEntityField) => field.fieldName === columnId
                  );

                  if (dataFieldInfo && _record[dataFieldInfo.fieldName]) {
                    dataField = [mainMetaData.tableName, `${mainMetaData.tableName}.${index}.${dataFieldInfo.fieldName}`];
                  }
                }

                const componentConfig = {
                  ...currentComponentSchemas,
                  config: {
                    ...currentComponentSchemas.config,
                    dataField:
                      dataField?.length > 0
                        ? dataField
                        : [mainMetaData.tableName, `${mainMetaData.tableName}.${index}.${column.dataIndex}`],
                    label: {
                      display: false,
                      text: ''
                    },
                    status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
                    verify: { required: false },
                    tooltip: ''
                  }
                };
                if (!cpType) {
                  return <span>{renderCellText(columnId, _text)}</span>;
                }

                return (
                  <PreviewRender
                    cpId={columnId}
                    cpType={cpType}
                    detailMode={true}
                    pageComponentSchema={componentConfig}
                    runtime={true}
                    recordId={_record.id}
                  />
                );
              }

              return <span>{renderCellText(columnId, _text)}</span>;
            }
          };
        });
      }
      const indexColumn = {
        title: '序号',
        dataIndex: 'index',
        width: '62px',
        align: 'center',
        headerCellStyle: { textAlign: 'center' },
        bodyCellStyle: { padding: '0 4px', textAlign: 'center' },
        render: (_: any, __: any, idx: number) => {
          const size = pageSize || 10;
          return (tablePageNo - 1) * size + idx + 1;
        }
      };
      if (showOpearate) {
        opearate.fixed = fixedOpearate ? 'right' : null;
        newColumns.push(opearate);
      } else {
        newColumns = newColumns.filter((v) => v.dataIndex !== 'op');
      }

      if (props?.xTableSelectProps?.showSelect && runtime) {
        const checkboxColumnRender = {
          title: '',
          dataIndex: 'select',
          width: 48,
          render: (_: any, record: any) => (
            <Checkbox
              checked={String(selectedRowId ?? '') === String(record.id)}
              onChange={(checked: boolean, event) => {
                if (event && typeof event.stopPropagation === 'function') event.stopPropagation();
                setSelectedRowId(checked ? record.id : null);
                props?.xTableSelectProps?.onSelectedChange?.(checked ? record : null);
                if (props?.xTableSelectProps?.refreshAfterSelect) {
                  handlePage();
                }
              }}
            />
          )
        };
        newColumns = [checkboxColumnRender, indexColumn, ...newColumns];
      } else {
        newColumns = [indexColumn, ...newColumns];
      }

      console.log('newColumns: ', newColumns);
      setFinalColumns(newColumns);
    };

    const handleCreate = () => {
      console.log('点击新增');

      console.log('runtime: ', runtime);
      if (!runtime) {
        return;
      }

      setRowDataId('');
      showFromPageData?.(null, true);
    };

    // 查询
    const handleSearch = () => {
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
      if (!runtime || !metaData || !isRuntimeEnv()) {
        return;
      }

      queryData = form.getFieldsValue();

      // TODO(mickey): 后续调试
      // if (sortByObject?.fieldName) {
      //   req.sortField = sortByObject.fieldName;
      //   req.sortDirection = sortByObject.sortBy === 1 ? 'asc' : 'desc';
      // }

      // TODO(mickey): 考虑模糊查询和范围查询
      const conditions: any[] = [];
      Object.entries(queryData).forEach(([key, value]) => {
        if (typeof value === 'object') {
          if (value?.id == undefined || value?.id == null || value?.id == '') {
            return;
          }
        }

        if (value != undefined && value != null && value !== '') {
          conditions.push({
            nodeType: 'CONDITION',
            fieldName: key,
            operator: VALIDATION_TYPE.EQUALS,
            fieldValue: typeof value === 'object' ? [value.id] : [value]
          });
        }
      });

      const filters = {
        nodeType: 'GROUP',
        combinator: 'AND',
        children: conditions
      };

      const req: PageMethodV2Params = {
        pageNo: tablePageNo,
        pageSize: pageSize || 10,
        filters: filters
      };

      const res = await dataMethodPageV2(tableName, curMenu.value?.id, req);
      console.log('res: ', res);

      const mainMetaData = await getEntityFieldsWithChildren(metaData);

      console.log('mainMetaData: ', mainMetaData);

      const { list, total } = res;

      const newTableData = (list || []).map((item: any) => {
        const newItem = item;
        Object.entries(newItem).forEach(([key, value]) => {
          // 优化：减少重复查找，提升可读性和性能
          if (Array.isArray(mainMetaData?.parentFields)) {
            const dataField = mainMetaData.parentFields.find(
              (field: AppEntityField) => field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE.DATE.VALUE
            );
            if (dataField && newItem[key]) {
              // 仅当字段类型为日期且有值时格式化
              const dateValue = new Date(newItem[key]);
              if (!isNaN(dateValue.getTime())) {
                newItem[key] = dateValue.toLocaleDateString();
              }
            }

            // // 部门选择单选 TODO
            // const deptSelectField = mainMetaData.parentFields.find(
            //   (field: AppEntityField) =>
            //     field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE.DEPARTMENT.VALUE
            // );
            // if (deptSelectField && newItem[key]) {
            //   if (newItem[key]) {
            //     newItem[key] = newItem[key]?.deptName || '';
            //   }
            // }
          }
        });

        const rowId = (item && item.id) || (item?.data && item.data.id);
        return {
          id: rowId,
          ...newItem,
          key: rowId
        };
      });

      console.log('newTableData: ', newTableData);

      tableForm.setFieldsValue({ [mainMetaData.tableName]: newTableData });
      setTableData(newTableData);
      setTableTotal(total);
    };

    const handleDelete = async (id: string) => {
      if (!runtime) {
        return;
      }
      const curFormPage = curPage.value?.pages?.find((ele: any) => ele.pageType === CATEGORY_TYPE.LIST);
      console.log('curFormPage: ', curFormPage);
      const pageId = curFormPage?.id;

      const flowRes = pageId ? await queryFlowExecForm(pageId) : [];
      console.log('flowRes: ', flowRes);

      const deleteFlows = (flowRes || []).filter(
        (ele: any) => ele.recordTriggerEvents && ele.recordTriggerEvents.includes(TRIGGER_EVENTS.DELETE)
      );
      setFlows(deleteFlows);

      console.log('删除数据 id: ', id);

      const req: DeleteMethodV2Params = {
        id: id
      };

      const res = await dataMethodDeleteV2(tableName, curMenu.value?.id, req);

      if (res) {
        Message.success('删除成功');
      }

      handlePage();
    };

    const handleEdit = (id: string, toFormPage: boolean) => {
      if (!runtime) {
        return;
      }
      setRowDataId(id);
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
          {searchItems?.length ? (
            <div className="searchGroup">
              <Form form={form} layout="vertical" className="searchItems">
                <TableSearch
                  searchItems={searchItems}
                  labelColSpan={labelColSpan}
                  runtime={runtime}
                  onSearch={handleSearch}
                  onReset={handleReset}
                />
              </Form>
            </div>
          ) : null}
          <div className="headerActions">
            <div className="addButton">
              {showAddBtn && (
                <Button type="primary" onClick={handleCreate} icon={<IconPlus />}>
                  添加数据
                </Button>
              )}
            </div>
            <Button type="text" onClick={() => handlePage()} icon={<IconRefresh />}></Button>
          </div>
        </div>
        <div>
          <Form form={tableForm}>
            <Table
              scroll={{
                x: 'max-content'
              }}
              onRow={(record, index) => {
                return {
                  onClick: (event) => {
                    handleRowClick(record);
                  },
                  onDoubleClick: () => {
                    if (props?.xTableSelectProps?.showSelect) {
                      setSelectedRowId(record.id);
                      props?.xTableSelectProps?.onSelectedChange?.(record, true);
                      if (props?.xTableSelectProps?.refreshAfterSelect) {
                        handlePage();
                      }
                    }
                  }
                };
              }}
              rowClassName={() => 'tableRow'}
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
          </Form>
        </div>
      </div>
    );
  }
);

export default XTable;
