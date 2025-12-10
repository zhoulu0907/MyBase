import filterIcon from '@/assets/images/filter.svg';
import { Button, Dialog, Dropdown, Ellipsis, LoadMore, SearchBar, Sticky, Toast } from '@arco-design/mobile-react';
import { useForm } from '@arco-design/mobile-react/esm/form';
import {
  dataMethodDeleteV2,
  dataMethodPageV2,
  DeleteMethodV2Params,
  getEntityFieldsWithChildren,
  menuSignal,
  PageMethodV2Params,
  type AppEntityField,
} from '@onebase/app';
import { pagesRuntimeSignal } from '@onebase/common';
import { BUTTON_OPTIONS, BUTTON_VALUES, ENTITY_FIELD_TYPE, RedirectMethod, useFormEditorSignal } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { memo, useEffect, useState } from 'react';
import './index.css';
import type { XLoadMoreConfig } from './schema';

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
      editMode?: boolean;
    }
  ) => {
    useSignals();

    const { pageComponentSchemas: fromPageComponentSchemas } = useFormEditorSignal;
    const { setDrawerVisible, setDrawerPageId, setDetailPageViewId } = pagesRuntimeSignal;
    const { runtime = true, showFromPageData, showAddBtn = true } = props;
    const hasOperationPermission = true;

    const {
      editMode = false,
      metaData,
      tableName,
      searchItems,
      columns,
      pageSize = 10,
      showOpearate,
      fixedOpearate,
      sortByObject,
      advancedRowRedirect,
      redirectPageId,
      redirectMethod,
      advancedButtonPermission,
      // operationButtonCollpaseNumber,
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
    const [showDropdown, setShowDropdown] = useState(false);

    const onReachBottom = (cb: Function) => {
      if (!tableData.length) return;
      setTablePageNo((prevPageNo) => prevPageNo + 1);
      cb('prepare');
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
            render: (item: any, index: number) => {
              const dataFieldInfo = mainMetaData.parentFields.find(
                (field: AppEntityField) => field.fieldId === column.id
              );
              const result = item[dataFieldInfo?.fieldName] || '';
              if (!result) return '';
              if (typeof result === 'object') {
                return JSON.stringify(result);
              }
              // if (['EMAIL', 'LONG_TEXT', 'TEXT', 'PHONE', 'NUMBER', 'DATE', 'DATETIME', 'URL', 'ADDRESS', 'AUTO_CODE', 'USER']
              //   .includes(dataFieldInfo.fieldType)) {
              //   return result || '-';
              // }
              const componentSchemasKeys = Object.keys(fromPageComponentSchemas.value || {});
              const cpId = componentSchemasKeys.find((ele) => {
                return fromPageComponentSchemas.value[ele]?.config?.dataField?.includes(column.id);
              });
              if (!cpId) {
                return result;
              }
              // const currentComponentSchemas = fromPageComponentSchemas.value[cpId];
              // if (['SELECT', 'MULTI_SELECT'].includes(dataFieldInfo.fieldType)) {
              //   const arrayResult = Array.isArray(result) ? result : result.split(',').map((cItem: string) => cItem.trim())
              //   const array = currentComponentSchemas.config.defaultOptionsConfig?.defaultOptions || []
              //   const tmpR = array.map((dItem: any) => {
              //     if (arrayResult.includes(dItem.value)) {
              //       return dItem.label
              //     }
              //     return ''
              //   }).filter((eItem: string) => eItem !== '')
              //   return tmpR.join(',')
              // }
              // if (['BOOLEAN'].includes(dataFieldInfo.fieldType)) {
              //   return result ? '是' : '否';
              // }
              // if (['DEPARTMENT'].includes(dataFieldInfo.fieldType)) {
              //   return result?.deptName || '-';
              // }
              return result || '-';
            }
          };
        });
      }
      newColumns = newColumns.filter((v) => v.dataIndex !== 'op');
      setFinalColumns(newColumns);
    };

    const handleCreate = () => {
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

      // TODO: 后续调试（同步ui-kit/Table组件）
      // if (sortByObject?.fieldName) {
      //   req.sortField = sortByObject.fieldName;
      //   req.sortDirection = sortByObject.sortBy === 1 ? 'asc' : 'desc';
      // }

      const req: PageMethodV2Params = {
        pageNo: tablePageNo,
        pageSize: pageSize || 10
      };
      
      const res = await dataMethodPageV2(tableName, curMenu.value?.id, req);

      const mainMetaData = await getEntityFieldsWithChildren(metaData);

      const { list = [], total = 0 } = res;

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

            // 多选字段回显 逗号分割
            const multiSelectField = mainMetaData.parentFields.find(
              (field: AppEntityField) =>
                field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE
            );
            if (multiSelectField && newItem[key]) {
              if (Array.isArray(newItem[key])) {
                newItem[key] = newItem[key].map((item: any) => item?.id).join(', ');
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

            // 部门
            const departmentField = mainMetaData.parentFields.find(
              (field: AppEntityField) =>
                field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE.DEPARTMENT.VALUE
            );
            if (departmentField && newItem[key]) {
              newItem[key] = newItem[key].deptName || '-';
            }

            // 开关
            const switchField = mainMetaData.parentFields.find(
              (field: AppEntityField) =>
                field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE.BOOLEAN.VALUE
            );
            if (switchField && typeof newItem[key] === 'boolean') {
              newItem[key] = newItem[key] ? '是' : '否'
            }

            // 单选列表
            const selectField = mainMetaData.parentFields.find(
              (field: AppEntityField) =>
                field.fieldName === key && field.fieldType === ENTITY_FIELD_TYPE.SELECT.VALUE
            );
            if (selectField) {
              newItem[key] = newItem[key].name || '-'
            }
          }
        });

        return {
          ...newItem,
          key: item.id
        };
      });
      setLoading(false);
      setTableData(req.pageNo === 1 ? newTableData : [...tableData, ...newTableData]);
      setTableTotal(total);
    };

    const handleDeleteAction = (id: string) => {
      if (!runtime || !showFromPageData) {
        return;
      }
      window.modalInstance = Dialog.confirm({
        title: '删除确认',
        children: '确定删除？删除后不可恢复！',
        platform: 'ios',
        okText: '删除',
        cancelText: '取消',
        onOk: () => {
          handleDelete(id);
        }
      });
    };
    const handleDelete = async (id: string) => {
      if (!runtime || !showFromPageData) {
        return;
      }

      const req: DeleteMethodV2Params = {
        id: id
      };
      
      const res = await dataMethodDeleteV2(tableName, curMenu.value?.id, req);
      if (res) {
        Toast.success('删除成功');
      }
      handleSearch();
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

    const noEdit =
      (advancedButtonPermission === BUTTON_VALUES[BUTTON_OPTIONS.HIDDEN] && !hasOperationPermission) ||
      (advancedButtonPermission === BUTTON_VALUES[BUTTON_OPTIONS.DISABLED] && !hasOperationPermission);
    const getItemBtns = (item: any) => {
      if (noEdit) return;
      return (
        <div className="list-body-item-btns">
          <Button
            color="#1D2129"
            borderColor="#86909C"
            type="ghost"
            size="mini"
            className="list-body-item-btn"
            onClick={() => handleDeleteAction(item.id)}
          >
            删除
          </Button>
          <Button type="primary" size="mini" className="list-body-item-btn" onClick={() => handleEdit(item.id, true)}>
            编辑
          </Button>
        </div>
      );
    };

    const [value, setValue] = useState([]);

    const filterDropdown = () => {
      return (
        <Dropdown showDropdown={showDropdown} onCancel={() => setShowDropdown(false)}>
          <div style={{ padding: '0.32rem' }}>
            <div className="demo-dropdown-option-desc">Group 1</div>
            <Dropdown.Options
              useColumn={3}
              multiple={true}
              selectedValue={value[0] || []}
              onOptionClick={() => {
                console.info('click 1');
              }}
              onOptionChange={(val, item) => {
                console.info('change 1', val, item);
                setValue((oldValue) => {
                  oldValue[0] = val;
                  return [...oldValue];
                });
              }}
              options={[
                {
                  label: 'Option 1',
                  value: 0,
                  disabled: false
                },
                {
                  label: 'Option 2',
                  value: 1
                },
                {
                  label: 'Option 3',
                  value: 2,
                  disabled: true
                },
                {
                  label: 'Option 4',
                  value: 3
                }
              ]}
            ></Dropdown.Options>
            <div className="demo-dropdown-option-desc">Group 2</div>
            <Dropdown.Options
              useColumn={3}
              multiple={true}
              selectedValue={value[1] || []}
              onOptionClick={() => {
                console.info('click 2');
              }}
              onOptionChange={(val, item) => {
                console.info('change 2', val, item);
                setValue((oldValue) => {
                  oldValue[1] = val;
                  return [...oldValue];
                });
              }}
              options={[
                {
                  label: 'Option 5',
                  value: 0,
                  disabled: false
                },
                {
                  label: 'Option 6',
                  value: 1
                }
              ]}
            ></Dropdown.Options>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <Button type="ghost" style={{ marginRight: '0.16rem', flex: 1 }}>
                重置
              </Button>
              <Button style={{ flex: 1 }}>确定</Button>
            </div>
          </div>
        </Dropdown>
      );
    };

    const getBottomBar = () => {
      if (editMode) {
        return null;
      }
      if (!loading && !tableData.length && tableTotal == 0) {
        return <div className="no-data">暂无数据</div>;
      }
      if (loading || tablePageNo * pageSize >= (tableTotal || Number.MAX_SAFE_INTEGER)) {
        return tableTotal ? <div className="total-data">共{tableTotal}条数据</div> : null;
      }
      return (
        <LoadMore
          getData={onReachBottom}
          getDataAtFirst={false}
          threshold={200}
          blockWhenLoading={false}
          trigger={manuClick ? 'click' : 'scroll'}
          throttle={300}
        />
      );
    };

    return (
      <div className="loadmore-list-wrapper-OBMobile">
        {searchItems?.length ? (
          <Sticky topOffset={0.88 * window.ROOT_FONT_SIZE} className="list-search-header">
            {searchItems?.length ? (
              <SearchBar actionButton={null} placeholder={`请输入${searchItems[0].label}`} />
            ) : (
              <div className="filter-title">筛选过滤</div>
            )}
            <img className="filter-icon" src={filterIcon} alt="" onClick={() => setShowDropdown(true)} />
            {filterDropdown()}
          </Sticky>
        ) : null}
        {showAddBtn && <div className="list-create-btn" onClick={handleCreate}></div>}
        <div className="list-body-wrapper">
          {(editMode ? [{}] : tableData).map((item, index) => (
            <div key={index} className="list-body-item-wrapper" onClick={() => handleRowClick(item)}>
              {(finalColumns?.length ? finalColumns : [{}, {}])?.map((col, index) => {
                return (
                  <div className="list-body-item-element" key={index}>
                    <Ellipsis className="list-body-item-title" text={(col.title || '') + '：'} />
                    <Ellipsis className="list-body-item-content" text={col.render?.(item, index)} />
                  </div>
                );
              })}
              {getItemBtns(item)}
            </div>
          ))}
          {getBottomBar()}
        </div>
      </div>
    );
  }
);

export default XLoadMore;
