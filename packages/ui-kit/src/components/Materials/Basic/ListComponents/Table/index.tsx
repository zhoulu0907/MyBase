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
  deleteFormDataPage,
  DeleteMethodV2Params,
  getEntityFieldsWithChildren,
  getFormDataPage,
  menuSignal,
  PageMethodV2Params,
  PageType,
  queryFlowExecForm,
  TRIGGER_EVENTS,
  VALIDATION_TYPE,
  type AppEntityField
} from '@onebase/app';
import { isRuntimeEnv, menuPermissionSignal, pagesRuntimeSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import PreviewRender from 'src/components/render/PreviewRender';
import { useFormEditorSignal } from 'src/signals/page_editor';
import { ENTITY_FIELD_TYPE } from '../../../../DataFactory/const';
import { COMPONENT_MAP } from '../../../componentsMap';
import { getComponentSchema } from '../../../schema';
import { DraftBox } from './DraftBox';
import './index.css';
import { renderCellText } from './renderCellText';
import type { XTableConfig } from './schema';
import TableSearch from './tableSerach';

type XTableSelectProps = {
  showSelect: boolean;
  defaultSelectedId?: string | number | null;
  onSelectedChange?: (value: any | null, fromDoubleClick?: boolean) => void;
  refreshAfterSelect?: boolean;
  //   隐藏草稿箱
  hiddenDraft?: boolean;
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
      pageSetType?: number;
    }
  ) => {
    useSignals();

    const { pageComponentSchemas: fromPageComponentSchemas } = useFormEditorSignal;
    const { canCreate, canEdit, canDelete } = menuPermissionSignal;

    const {
      curPage,
      setDrawerVisible,
      setDrawerPageId,
      setDetailPageViewId,
      setRowDataId,
      setFlows,
      setBpmInstanceId,
      setRowDataType
    } = pagesRuntimeSignal;
    const { runtime = true, showFromPageData, showAddBtn = true, preview, pageSetType } = props;
    const hasOperationPermission = true;

    const {
      status,
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
                {opearate.type === TableOperationButton.EDIT && opearate.display && canEdit.value && (
                  <Button
                    type="text"
                    size="small"
                    style={{ padding: '0 8px' }}
                    onClick={(event) => {
                      event.stopPropagation();
                      handleEdit(record.id, true, record);
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

                {opearate.type === TableOperationButton.DELETE && opearate.display && canDelete.value && (
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
    }, [showOpearate, columns, fixedOpearate, selectedRowId, tablePageNo]);

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
          // 数据标题
          if (column.dataIndex?.indexOf('-') !== -1) {
            return {
              ...column,
              dataIndex: 'dataTitle',
              ellipsis: true,
              width: column.width + 'px',
              bodyCellStyle: { padding: '0 12px' },
              render: (_text: any, _record: any, index: number) => {
                const _index = column.dataIndex.indexOf('-');
                const dataTitleType = column.dataIndex.slice(0, _index);
                const dataTitle = column.dataIndex.slice(_index + 1);
                // 1默认标题  2自定义标题
                if (dataTitleType == '1') {
                  // 发起人发起的页面名称
                  return <span>{`${_record.creator?.name}发起的${curMenu.value?.menuName}`}</span>;
                } else {
                  // 自定义标题  "11{{创建人ID}}22{{常规文本}}33{{邮箱}}44"
                  const dataTitleArr = dataTitle.match(/\{\{(.+?)\}\}/g)
                  mainMetaData?.parentFields?.length
                  if (dataTitleArr && dataTitleArr.length > 0 && mainMetaData?.parentFields?.length) {
                    let dataTitleStr = dataTitle;
                    dataTitleArr.forEach((e:string)=>{
                      const _fieldNameArr = e.match(/\{\{(.+?)\}\}/)
                      const _fieldName = _fieldNameArr?.[1] || '';
                      const fieldObj = mainMetaData.parentFields.find((ele:any)=>_fieldName && ele.displayName === _fieldName);
                      const fieldValue = _record?.[fieldObj.fieldName]?.name || _record?.[fieldObj.fieldName] || '';
                      dataTitleStr = dataTitleStr.replace(e,fieldValue)
                    })
                    return <span>{dataTitleStr}</span>
                  }
                  return <span>{dataTitle}</span>
                }
              }
            }
          }
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
                // 当前组件配置
                const currentComponentSchemas = fromPageComponentSchemas.value[cpId];
                // 组件类型
                const cpType = currentComponentSchemas.type;
                // 覆盖配置
                let dataField: string[] = [];
                if (Array.isArray(mainMetaData?.parentFields)) {
                  const dataFieldInfo = mainMetaData.parentFields.find(
                    (field: AppEntityField) => field.fieldName === columnId
                  );

                  if (dataFieldInfo && _record[dataFieldInfo.fieldName]) {
                    dataField = [
                      mainMetaData.tableName,
                      `${mainMetaData.tableName}.${index}.${dataFieldInfo.fieldName}`
                    ];
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
                    key={`table-${index}-${columnId}`}
                    cpId={columnId}
                    cpType={cpType}
                    detailMode={true}
                    pageComponentSchema={componentConfig}
                    runtime={true}
                    recordId={_record.id}
                  />
                );
              }

              // 系统字段 表单配置里没有就根据字段类型获取默认配置
              if (mainMetaData?.parentFields?.length) {
                const dataFieldInfo = mainMetaData.parentFields.find(
                  (field: AppEntityField) => field.fieldName === columnId
                );
                const cpType = dataFieldInfo?.fieldType ? COMPONENT_MAP[dataFieldInfo.fieldType] : null;
                if (cpType) {
                  const basicConfig = getComponentSchema(cpType as any);
                  const componentConfig = {
                    ...basicConfig,
                    config: {
                      ...basicConfig.config,
                      dataField: [
                        mainMetaData.tableName,
                        `${mainMetaData.tableName}.${index}.${dataFieldInfo.fieldName}`
                      ],
                      label: {
                        display: false,
                        text: ''
                      },
                      verify: { required: false },
                      tooltip: ''
                    }
                  };
                  return (
                    <PreviewRender
                      key={`table-${index}-${columnId}`}
                      cpId={columnId}
                      cpType={cpType}
                      detailMode={true}
                      pageComponentSchema={componentConfig}
                      runtime={true}
                      recordId={_record.id}
                    />
                  );
                }
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
        if (canEdit.value || canDelete.value) {
          newColumns.push(opearate);
        }
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

      setFinalColumns(newColumns);
    };

    const handleCreate = () => {
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

      // 数据排序
      if (sortByObject?.length) {
        const sortBy = sortByObject.map(ele => ({
          field: ele.fieldName,
          direction: ele.sortBy
        }))
        queryData = { ...queryData, sortBy }
      }

      // 考虑模糊查询和范围查询
      const conditions: any[] = [];
      Object.entries(queryData).forEach(([key, value]) => {
        if (typeof value === 'object' && !Array.isArray(value)) {
          if (value?.id == undefined || value?.id == null || value?.id == '') {
            return;
          }
        }

        if (value != undefined && value != null && value !== '') {
          // 日期范围选择器提交 [start, end]，使用 RANGE 条件；结束日取 23:59:59 以包含当天全天（兼容精确到秒的日期时间字段）
          const isDateRange = Array.isArray(value) && value.length === 2;
          if (isDateRange) {
            const toDayjs = (v: any) =>
              v && typeof v === 'object' && typeof v.startOf === 'function' ? v : null;
            const start = toDayjs(value[0]);
            const end = toDayjs(value[1]);
            const startStr = start ? start.startOf('day').format('YYYY-MM-DD HH:mm:ss') : value[0];
            const endStr = end ? end.endOf('day').format('YYYY-MM-DD HH:mm:ss') : value[1];
            conditions.push({
              nodeType: 'CONDITION',
              fieldName: key,
              operator: VALIDATION_TYPE.RANGE,
              fieldValue: [startStr, endStr]
            });
          } else {
            conditions.push({
              nodeType: 'CONDITION',
              fieldName: key,
              operator: VALIDATION_TYPE.EQUALS,
              fieldValue: Array.isArray(value) ? value : (typeof value === 'object' ? [value?.id] : [value])
            });
          };
        }
      });
      // 数据过滤 filterCondition
      if (filterCondition && filterCondition.length > 0) {
        conditions.push.apply(conditions, filterCondition)
      }

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
      let res: any;
      if (props?.pageSetType === PageType.BPM) {
        const params = {
          menuId: curMenu.value?.id,
          tableName,
          ...req
        };
        res = await getFormDataPage(params);
      } else {
        res = await dataMethodPageV2(tableName, curMenu.value?.id, req);
      }

      const mainMetaData = await getEntityFieldsWithChildren(metaData);

      const { list, total } = res;

      const newTableData = (list || []).map((item: any) => {
        const newItem = item;
        Object.entries(newItem).forEach(([key, _value]) => {
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
          }
        });

        const rowId = (item && item.id) || (item?.data && item.data.id);
        return {
          id: rowId,
          ...newItem,
          key: rowId
        };
      });

      tableForm.setFieldsValue({ [mainMetaData.tableName]: newTableData });
      setTableData(newTableData);
      setTableTotal(total);
    };

    const handleDelete = async (id: string) => {
      if (!runtime) {
        return;
      }

      const curFormPage = curPage.value?.pages?.find((ele: any) => ele.pageType === CATEGORY_TYPE.LIST);
      const pageId = curFormPage?.id;

      const flowRes = pageId ? await queryFlowExecForm(pageId) : [];

      const deleteFlows = (flowRes || []).filter(
        (ele: any) => ele.recordTriggerEvents && ele.recordTriggerEvents.includes(TRIGGER_EVENTS.DELETE)
      );
      setFlows(deleteFlows);

      const req: DeleteMethodV2Params = {
        id: id
      };
      let res: any;
      if (props?.pageSetType === PageType.BPM) {
        const params = {
          menuId: curMenu.value?.id,
          tableName,
          ...req
        };
        res = await deleteFormDataPage(params);
      } else {
        res = await dataMethodDeleteV2(tableName, curMenu.value?.id, req);
      }

      if (res) {
        Message.success('删除成功');
      }

      handlePage();
    };

    const handleEdit = (id: string, toFormPage: boolean, record?: any) => {
      if (!runtime) {
        return;
      }
      if (record) {
        if (record.bpm_instance_id) {
          setRowDataType(PageType.BPM);
          setBpmInstanceId(record.bpm_instance_id);
        } else {
          setRowDataType(PageType.NORMAL);
          setBpmInstanceId('');
        }
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
          if (record.bpm_instance_id) {
            setRowDataType(PageType.BPM);
            setBpmInstanceId(record.bpm_instance_id);
          } else {
            setRowDataType(PageType.NORMAL);
            setBpmInstanceId('');
          }
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
                  tableName={tableName}
                  labelColSpan={labelColSpan}
                  runtime={runtime}
                  onSearch={handleSearch}
                  onReset={handleReset}
                  pageSetType={pageSetType}
                />
              </Form>
            </div>
          ) : null}
          <div className="headerActions">
            <div className="addButton">
              {showAddBtn && canCreate.value && (
                <Button type="primary" onClick={handleCreate} icon={<IconPlus />}>
                  添加数据
                </Button>
              )}

              {!props?.xTableSelectProps?.hiddenDraft && canCreate.value && (
                <DraftBox
                  showFromPageData={showFromPageData}
                  tableColumns={finalColumns}
                  menuId={curMenu.value?.id}
                  tableName={tableName}
                  refresh={refresh}
                />
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
              onRow={(record) => {
                return {
                  onClick: () => {
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
