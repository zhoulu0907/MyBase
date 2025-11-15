import { Button, Checkbox, Form, Message, Popconfirm, Space, Table, Tooltip } from '@arco-design/web-react';
import { memo, useEffect, useState } from 'react';
import {
  BUTTON_OPTIONS,
  BUTTON_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  TableOperationButton,
  TableOperationButtonStyle
} from '../../../constants';

import DynamicIcon from '@/components/DynamicIcon';
import { iconMap } from '@/utils/const';
import { IconPlus, IconRefresh } from '@arco-design/web-react/icon';
import {
  dataMethodDelete,
  dataMethodPage,
  getEntityFieldsWithChildren,
  menuSignal,
  type AppEntityField,
  type DeleteMethodParam,
  type PageMethodParam
} from '@onebase/app';
import { pagesRuntimeSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import PreviewRender from 'src/components/render/PreviewRender';
import { useFormEditorSignal } from 'src/signals/page_editor';
import { ENTITY_FIELD_TYPE } from '../../../../DataFactory/const';
import { RedirectMethod } from '../../../constants';
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
  selectedDataId: string | null;
  setSelectData: (value: any) => void;
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

    const { setDrawerVisible, setDrawerPageId, setDetailPageViewId } = pagesRuntimeSignal;
    const { runtime = true, showFromPageData, showAddBtn = true, preview } = props;
    const hasOperationPermission = true;

    const {
      id,
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
      refresh
    } = props;

    const { curMenu } = menuSignal;
    const [tableForm] = Form.useForm();

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
      width: '80px',
      headerCellStyle: { textAlign: 'center' },
      //TODO: zhoumingji ,基础组件上不要写这种样式，最好能放到样式文件里
      bodyCellStyle: { padding: '0 8px', textAlign: 'center' },
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
    }, [showOpearate, columns, fixedOpearate, props?.xTableSelectProps?.selectedDataId]);

    useEffect(() => {
      if (finalColumns && metaData) {
        handlePage();
      }
    }, [finalColumns, tablePageNo, metaData, sortByObject]);

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
            render: (_text: string, _record: any, index: number) => {
              const componentSchemasKeys = Object.keys(fromPageComponentSchemas.value || {});
              const cpId = componentSchemasKeys.find((ele) => {
                return fromPageComponentSchemas.value[ele]?.config?.dataField?.includes(column.id);
              });
              if (cpId) {
                // 组件类型
                const cpType = components.value?.find((ele) => ele.id === cpId)?.type;
                // 当前组件配置
                const currentComponentSchemas = fromPageComponentSchemas.value[cpId];
                // 覆盖配置
                let dataField: string[] = [];
                if (Array.isArray(mainMetaData?.parentFields)) {
                  const dataFieldInfo = mainMetaData.parentFields.find(
                    (field: AppEntityField) => field.fieldId === column.id
                  );
                  if (dataFieldInfo && _record[dataFieldInfo.fieldName]) {
                    dataField = [`${id}.${index}.${dataFieldInfo.fieldName}`];
                  }
                }
                const componentConfig = {
                  ...currentComponentSchemas,
                  config: {
                    ...currentComponentSchemas.config,
                    dataField: dataField?.length > 0 ? dataField : [`${id}.${index}.${column.id}`],
                    label: {
                      display: false,
                      text: ''
                    },
                    verify: { required: false },
                    tooltip: ''
                  }
                };
                if (!cpType) {
                  return <span>{_text}</span>;
                }

                return (
                  <PreviewRender
                    cpId={column.id}
                    cpType={cpType}
                    detailMode={true}
                    pageComponentSchema={componentConfig}
                    runtime={true}
                  />
                );
              }
              return <span>{_text}</span>;
            }
          };
        });
      }
      const indexColumn = {
        title: '序号',
        dataIndex: 'index',
        width: '56px',
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
              checked={props?.xTableSelectProps?.selectedDataId === record.id}
              onChange={(checked: boolean) => {
                props?.xTableSelectProps?.setSelectData(checked ? record : null);
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
      if (!runtime || !metaData) {
        return;
      }

      const req: PageMethodParam = {
        menuId: curMenu.value?.id,
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
        const newItem = item.data;
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

            // 多选字段回显 逗号分割
            const multiSelectField = mainMetaData.parentFields.find(
              (field: AppEntityField) =>
                field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE
            );
            if (multiSelectField && newItem[key]) {
              if (Array.isArray(newItem[key])) {
                newItem[key] =
                  newItem[key].length > 1 ? newItem[key].map((item: string) => item).join(', ') : newItem[key];
              }
            }

            // 人员选择单选 TODO
            const userSelectField = mainMetaData.parentFields.find(
              (field: AppEntityField) => field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE.USER.VALUE
            );
            if (userSelectField && newItem[key]) {
              if (newItem[key]) {
                newItem[key] = newItem[key]?.userName || '';
              }
            }

            // 部门选择单选 TODO
            const deptSelectField = mainMetaData.parentFields.find(
              (field: AppEntityField) =>
                field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE.DEPARTMENT.VALUE
            );
            if (deptSelectField && newItem[key]) {
              if (newItem[key]) {
                newItem[key] = newItem[key]?.deptName || '';
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
      tableForm.setFieldsValue({ [id]: newTableData });
      setTableTotal(total);
    };

    const handleDelete = async (id: string) => {
      if (!runtime) {
        return;
      }
      console.log('删除数据 id: ', id);
      const req: DeleteMethodParam = {
        menuId: curMenu.value?.id,
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
                    if (props?.xTableSelectProps?.showSelect && props?.xTableSelectProps?.setSelectData) {
                      props.xTableSelectProps.setSelectData(record);
                    }
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
          </Form>
        </div>
      </div>
    );
  }
);

export default XTable;
