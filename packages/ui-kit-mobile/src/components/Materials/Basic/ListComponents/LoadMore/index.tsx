import {
    Dialog,
    Sticky,
    Ellipsis,
    Input,
    SearchBar,
    Toast,
    Button,
    LoadMore
} from '@arco-design/mobile-react';
import { useForm } from '@arco-design/mobile-react/esm/form';
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
import filterIcon from '@/assets/images/filter.svg';
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
import { ENTITY_FIELD_TYPE } from '../../../../DataFactory/const';
import { RedirectMethod } from '../../../constants';
import './index.css';
import type { XLoadMoreConfig } from './schema';
import { IconHandle } from '@douyinfe/semi-icons';

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

const XLoadMore = memo(
  (
    props: XLoadMoreConfig & {
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
      refresh,
      manuClick
    } = props;

    const { curMenu } = menuSignal;

    const [finalColumns, setFinalColumns] = useState<any[]>();
    // 实际查询用的参数
    let queryData: object = {};

    const [tableData, setTableData] = useState<any[]>([]);
    const [tableTotal, setTableTotal] = useState<number>(0);
    const [tablePageNo, setTablePageNo] = useState<number>(1);
    const [loading, setLoading] = useState<boolean>(false);

    const onReachBottom = (cb: Function) => {
      console.log('onReachBottom', tablePageNo);
      if (!tableData.length) return;
      setTablePageNo(prevPageNo => prevPageNo + 1);
      cb('prepare');
    }

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
      setFinalColumns(() => columns?.filter((v) => v.dataIndex !== 'op'));

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

      setLoading(true);
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
      setLoading(false);
      setTableData([...tableData, ...newTableData]);
      setTableTotal(total);
    };

    const handleDeleteAction = (id: string) => {
      window.modalInstance = Dialog.confirm({
        title: '删除确认',
        children: '确定删除？删除后不可恢复！',
        platform: 'ios',
        okText: '删除',
        cancelText: '取消',
        onOk: () => {
          handleDelete(id);
        },
      });
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

    const noEdit = advancedButtonPermission === BUTTON_VALUES[BUTTON_OPTIONS.HIDDEN] && !hasOperationPermission
        || advancedButtonPermission === BUTTON_VALUES[BUTTON_OPTIONS.DISABLED] && !hasOperationPermission;
    const getItemBtns = (item: any) => {
      if (noEdit) return;
      return (
        <div className="list-body-item-btns">
          <Button color="#1D2129" borderColor="#86909C" type="ghost" size="mini" className="list-body-item-btn" onClick={() => handleDeleteAction(item.id)}>
            删除
          </Button>
          <Button type="primary" size="mini" className="list-body-item-btn" onClick={() => handleEdit(item.id, true)}>
            编辑
          </Button>
        </div>
      )
    }

    return (
      <div className="loadmore-list-wrapper">
        <Sticky topOffset={0} className="list-search-header">
          {searchItems?.length ? (
              <SearchBar actionButton={null} placeholder={`请输入${searchItems[0].label}`} />
            ) : null}
            <img className="filter-icon" src={filterIcon} alt="" />
        </Sticky>
        {showAddBtn && (
              <div className="list-create-btn" onClick={handleCreate}>
              </div>
            )}
            <div className="list-body-wrapper">
                {
                  tableData.map((item) => (
                    <div key={item.key} className="list-body-item-wrapper" onClick={() => handleRowClick(item)}>
                      {finalColumns?.map((col, index) => {
                        return <div className="list-body-item-element" key={col.dataIndex}>
                          <Ellipsis className="list-body-item-title" text={col.title} />
                          { index ? '' : '：'}
                          <Ellipsis className="list-body-item-content" text={item[col.dataIndex]} />
                        </div>
                      })}
                      {getItemBtns(item)}
                    </div>
                  ))
                }
              {
                loading || tablePageNo * pageSize >= (tableTotal || Number.MAX_SAFE_INTEGER) ? null : <LoadMore
                  getData={onReachBottom}
                  getDataAtFirst={false}
                  threshold={200}
                  blockWhenLoading={false}
                  trigger={manuClick ? 'click' : 'scroll'}
                  throttle={300}
                />
              }
            </div>
      </div>
    );
  }
);

export default XLoadMore;
