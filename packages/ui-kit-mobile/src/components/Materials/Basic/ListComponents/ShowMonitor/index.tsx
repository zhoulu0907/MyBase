import {
  Form,
  Input,
  Toast,
  ShowMonitor,
  Button,
} from '@arco-design/mobile-react';
import { useForm } from '@arco-design/mobile-react/esm/form';
import { memo, useEffect, useState } from 'react';
import {
  BUTTON_OPTIONS,
  BUTTON_VALUES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  ENTITY_FIELD_TYPE,
  TableOperationButton,
  TableOperationButtonStyle,
  iconMap,
  RedirectMethod,
} from '@onebase/ui-kit';

// import DynamicIcon from '@/components/DynamicIcon';
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
import type { XShowMonitorConfig } from './schema';
import './index.css';

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

const XShowMonitor = memo(
  (
    props: XShowMonitorConfig & {
      runtime?: boolean;
      showFromPageData?: Function;
      showAddBtn?: boolean;
      refresh?: number;
      xTableSelectProps?: XTableSelectProps;
    }
  ) => {
    useSignals();

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
      refresh
    } = props;

    const { curMenu } = menuSignal;

    const [finalColumns, setFinalColumns] = useState<any[]>();
    // 实际查询用的参数
    let queryData: object = {};

    const [tableData, setTableData] = useState<any[]>([]);
    const [tableTotal, setTableTotal] = useState<number>(0);
    const [tablePageNo, setTablePageNo] = useState<number>(1);

    const onReachBottom = () => {
      console.log('onReachBottom', tablePageNo);
      setTablePageNo(prevPageNo => prevPageNo + 1);
    }

    const opearate: any = {
      title: '操作',
      dataIndex: 'op',
      fixed: null,
      width: '110px',
      render: (_: any, record: any) => {
        if (advancedButtonPermission === BUTTON_VALUES[BUTTON_OPTIONS.HIDDEN] && !hasOperationPermission) return;
        const isDisabled =
          advancedButtonPermission === BUTTON_VALUES[BUTTON_OPTIONS.DISABLED] && !hasOperationPermission;
        return (
          <div>11
          </div>
        );
      }
    };

    useEffect(() => {
      if (refresh) {
        handlePage();
      }
    }, [refresh]);

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

      // if (props?.xTableSelectProps?.showSelect && runtime) {
      //   const checkboxColumnRender = {
      //     title: '',
      //     dataIndex: 'select',
      //     width: 48,
      //     render: (_: any, record: any) => (
      //       <Checkbox
      //         checked={props?.xTableSelectProps?.selectedDataId === record.id}
      //         onChange={(checked: boolean) => {
      //           props?.xTableSelectProps?.setSelectData(checked ? record : null);
      //         }}
      //       />
      //     )
      //   };
      //   setFinalColumns([checkboxColumnRender, ...(columns as any)]);
      // }
    }, [showOpearate, columns, fixedOpearate, props?.xTableSelectProps?.selectedDataId]);

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
      if (!runtime || (tablePageNo - 1) * pageSize >= (tableTotal || Number.MAX_SAFE_INTEGER)) {
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
                newItem[key] = newItem[key].userName;
              }
            }
          }
        });

        return {
          ...newItem,
          key: item.data.id
        };
      });
      console.log('newTableData: ', newTableData);

      setTableData([...tableData, ...newTableData]);
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
        Toast.success('删除成功');
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

    const [form] = useForm();

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
            {showAddBtn && (
              <Button type="primary" onClick={handleCreate}>
                新增
              </Button>
            )}
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
          <div
            className="tableFormItem"
            style={{
              width: '100%',
              maxWidth: runtime ? '100%' : `calc(100vw - ${componentMaxWidth}px)`,
              pointerEvents: status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'unset'
            }}
          >
            <div style={{ width: '100%' }}>
              <div style={{ minHeight: '900px', backgroundColor: 'red' }}>
                {
                  tableData.map((item) => (
                    <div key={item.key} className="show-monitor-content">
                      <div>-----------------------</div>
                      {finalColumns?.map((col) => {
                        return <div key={col.dataIndex}>{`${col.title}：${item[col.dataIndex]}`}</div>
                      })}
                    </div>
                  ))
                }
              </div>
              {
                tablePageNo * pageSize >= (tableTotal || Number.MAX_SAFE_INTEGER) ? null : <ShowMonitor
                  onVisibleChange={onReachBottom}
                  threshold={0.1}
                >
                  <div className="show-monitor-content" onClick={onReachBottom}>正在加载。。。</div>
                </ShowMonitor>
              }
            </div>
          </div>
        </div>
      </div>
    );
  }
);

export default XShowMonitor;
