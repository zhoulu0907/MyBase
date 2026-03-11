import { Button, Form, List, Card, Empty, Message, Tooltip, Popconfirm, Spin } from '@arco-design/web-react';
import { IconPlus, IconRefresh, IconEdit, IconDelete } from '@arco-design/web-react/icon';
import { memo, useEffect, useState } from 'react';
import { useSignals } from '@preact/signals-react/runtime';
import { isRuntimeEnv, menuPermissionSignal, pagesRuntimeSignal } from '@onebase/common';
import {
  dataMethodPageV2,
  getEntityFieldsWithChildren,
  getFormDataPage,
  deleteFormDataPage,
  DeleteMethodV2Params,
  dataMethodDeleteV2,
  attachmentDownload,
  menuSignal,
  PageMethodV2Params,
  queryFlowExecForm,
  PageType,
  VALIDATION_TYPE,
  CATEGORY_TYPE,
  TRIGGER_EVENTS,
  type AppEntityField
} from '@onebase/app';
import { useFormEditorSignal } from 'src/signals/page_editor';
import { COMPONENT_MAP } from '../../../componentsMap';
import { getComponentSchema } from '../../../schema';
import type { XCardConfig } from './schema';
import {
  STATUS_OPTIONS,
  STATUS_VALUES,
  WIDTH_OPTIONS,
  WIDTH_VALUES,
  RedirectMethod,
  TableOperationButton
} from '../../../constants';
import { ENTITY_FIELD_TYPE } from '../../../../DataFactory/const';
import PreviewRender from 'src/components/render/PreviewRender';
import CardSearch from './cardSerach';
import './index.css';

type XCardSelectProps = {
  showSelect: boolean;
  defaultSelectedId?: string | number | null;
  onSelectedChange?: (value: any | null, fromDoubleClick?: boolean) => void;
  refreshAfterSelect?: boolean;
  //   隐藏草稿箱
  hiddenDraft?: boolean;
};

const XCard = memo(
  (
    props: XCardConfig & {
      runtime?: boolean;
      preview?: boolean;
      showFromPageData?: Function;
      showAddBtn?: boolean;
      refresh?: number;
      xTableSelectProps?: XCardSelectProps;
      pageSetType?: number;
    }
  ) => {
    useSignals();

    const { pageComponentSchemas: fromPageComponentSchemas, components } = useFormEditorSignal;
    const { menuPermission, canCreate, canEdit, canDelete } = menuPermissionSignal;
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
    const { curMenu } = menuSignal;

    const {
      status,
      runtime = true,
      preview,
      label,
      metaData,
      tableName,
      coverField,
      imageFill,
      titleField,
      showFields,
      columns,
      layout,
      filterCondition,
      paginationConfig,
      showAddBtn = true,
      searchItems,
      pageSetType,
      cardWidth,
      showFromPageData,
      refresh,
      sortBy,
      advancedRowRedirect,
      redirectPageId,
      redirectMethod,
      operationButton
    } = props;
    const hasOperationPermission = true;

    const [form] = Form.useForm();
    const [cardForm] = Form.useForm();
    // 实际查询用的参数
    let queryData: object = {};

    const [cardData, setCardData] = useState<any[]>([]);
    const [cardTotal, setCardTotal] = useState<number>(0);
    const [cardPageNo, setCardPageNo] = useState<number>(1);

    const [mainMetaData, setMainMetaData] = useState<any>({});

    useEffect(() => {
      if (refresh) {
        setCardPageNo(1);
      }
    }, [refresh]);

    useEffect(() => {
      if (metaData) {
        getMainMetaData();
        if (!runtime) {
          setCardData([{}, {}, {}]);
          setCardTotal(3);
        }
      }
    }, [metaData]);

    useEffect(() => {
      if (metaData) {
        handlePage();
      }
    }, [cardPageNo]);

    const getMainMetaData = async () => {
      const res = await getEntityFieldsWithChildren(metaData);
      setMainMetaData(res);
    };

    // 新增
    const handleCreate = () => {
      if (!runtime) {
        return;
      }
      setRowDataId('');
      showFromPageData?.(null, true);
    };
    // 查询
    const handleSearch = () => {
      setCardPageNo(1);
    };

    // 重置
    const handleReset = () => {
      form.resetFields();
      queryData = {};
      setCardPageNo(1);
    };

    const handlePage = async (reset?: boolean) => {
      if (!runtime || !metaData || !isRuntimeEnv()) {
        return;
      }

      queryData = form.getFieldsValue();

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
            fieldValue: typeof value === 'object' ? [value?.id] : [value]
          });
        }
      });

      const filters = {
        nodeType: 'GROUP',
        combinator: 'AND',
        children: conditions
      };

      const req: PageMethodV2Params = {
        pageNo: cardPageNo,
        pageSize: paginationConfig?.pageSize || 20,
        filters: filterCondition && Object.keys(filterCondition).length > 0 ? filterCondition : filters
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

      let newCardData: any[] = [];
      for (let item of list || []) {
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
          }
        });

        const rowId = (item && item.id) || (item?.data && item.data.id);

        let coverFieldValue = null;
        if (coverField) {
          const fileId = item[coverField]?.[0]?.id;
          if (fileId) {
            coverFieldValue = await attachmentDownload(tableName, {
              menuId: curMenu.value.id,
              id: rowId,
              fieldName: coverField,
              fileId: fileId
            });
          }
        }
        newCardData.push({
          id: rowId,
          ...newItem,
          key: rowId,
          [coverField]: coverFieldValue
        });
      }
      if (paginationConfig?.display || reset || cardPageNo === 1) {
        cardForm.setFieldsValue({ [mainMetaData.tableName]: newCardData });
        setCardData(newCardData);
      } else {
        setCardData((prev) => {
          // 去重
          newCardData = newCardData.filter((ele) => !prev.some((e) => e.id === ele.id));
          const newData = prev.concat(...newCardData);
          cardForm.setFieldsValue({ [mainMetaData.tableName]: newData });
          return newData;
        });
      }
      setCardTotal(total);
    };

    const getSpan = () => {
      if (cardWidth === WIDTH_VALUES[WIDTH_OPTIONS.THIRD]) {
        return 8;
      }
      if (cardWidth === WIDTH_VALUES[WIDTH_OPTIONS.HALF]) {
        return 12;
      }
      if (cardWidth === WIDTH_VALUES[WIDTH_OPTIONS.FULL]) {
        return 24;
      }
      return 6;
    };

    const renderListItem = (item: any, index: number) => {
      return (
        <List.Item key={index} className="listItem" style={{ padding: 0 }}>
          <Card
            className="card"
            bordered={false}
            onClick={() => handleRowClick(item)}
            cover={
              coverField ? (
                <img
                  style={{ width: '100%', height: '128px', objectFit: imageFill || 'fill' }}
                  src={item[coverField]}
                  alt=""
                />
              ) : undefined
            }
          >
            <Card.Meta
              title={titleField ? renderItem(item, titleField, index, true) : undefined}
              description={
                showFields ? (
                  <>
                    {columns?.map((ele, i) => (
                      <div key={`${index}-${i}`}>{renderItem(item, ele.dataIndex, index, false, ele)}</div>
                    ))}
                  </>
                ) : undefined
              }
            />
          </Card>
          <div className="cardExtra">
            {operationButton?.map((opearate, index) => (
              <Tooltip content={!hasOperationPermission && '无操作权限'} key={index}>
                {opearate.type === TableOperationButton.EDIT && opearate.display && canEdit.value && (
                  <Button
                    type="text"
                    size="small"
                    style={{ padding: '0 8px' }}
                    onClick={(event) => {
                      event.stopPropagation();
                      handleEdit(item.id, true, item);
                    }}
                    icon={<IconEdit />}
                  ></Button>
                )}

                {opearate.type === TableOperationButton.DELETE && opearate.display && canDelete.value && (
                  <div onClick={(event) => event.stopPropagation()}>
                    <Popconfirm
                      focusLock
                      title="确认删除"
                      content={opearate.confirmText}
                      disabled={preview}
                      onOk={(event) => {
                        event.stopPropagation();
                        handleDelete(item.id);
                      }}
                    >
                      <Button
                        type="text"
                        size="small"
                        disabled={preview}
                        style={{ padding: '0 4px' }}
                        onClick={(event) => {
                          event.stopPropagation();
                        }}
                        status={'danger'}
                        icon={<IconDelete />}
                      ></Button>
                    </Popconfirm>
                  </div>
                )}
              </Tooltip>
            ))}
          </div>
        </List.Item>
      );
    };

    const renderItem = (_record: any, fieldName: string, index: number, isTitle: boolean, column?: any) => {
      if (!runtime && isTitle) {
        const dataFieldInfo = mainMetaData.parentFields?.find((field: AppEntityField) => field.fieldName === fieldName);
        return <span>{dataFieldInfo?.displayName || fieldName}</span>;
      }
      const componentSchemasKeys = Object.keys(fromPageComponentSchemas.value || {});
      if (!mainMetaData?.parentFields) {
        return <span>{_record[fieldName]}</span>;
      }
      // 表单配置
      const cpId = componentSchemasKeys.find((ele) => {
        return fromPageComponentSchemas.value[ele]?.config?.dataField?.includes(fieldName);
      });
      if (cpId) {
        // 当前组件配置
        const currentComponentSchemas = fromPageComponentSchemas.value[cpId];
        // 组件类型
        const cpType = currentComponentSchemas.type;
        if (!cpType) {
          return <span>{_record[fieldName]}</span>;
        }
        // 覆盖配置
        let dataField: string[] = [];
        if (Array.isArray(mainMetaData?.parentFields)) {
          const dataFieldInfo = mainMetaData.parentFields.find(
            (field: AppEntityField) => field.fieldName === fieldName
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
                : [mainMetaData.tableName, `${mainMetaData.tableName}.${index}.${fieldName}`],
            label: {
              display: !isTitle,
              text: column?.title || currentComponentSchemas.config?.label?.text
            },
            layout: layout,
            status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
            verify: { required: false },
            tooltip: ''
          }
        };

        return (
          <PreviewRender
            cpId={fieldName}
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
        const dataFieldInfo = mainMetaData.parentFields.find((field: AppEntityField) => field.fieldName === fieldName);
        const cpType = dataFieldInfo?.fieldType ? COMPONENT_MAP[dataFieldInfo.fieldType] : null;
        if (cpType) {
          const basicConfig = getComponentSchema(cpType as any);
          const componentConfig = {
            ...basicConfig,
            config: {
              ...basicConfig.config,
              dataField: [mainMetaData.tableName, `${mainMetaData.tableName}.${index}.${dataFieldInfo.fieldName}`],
              label: {
                display: !isTitle,
                text: column?.title || dataFieldInfo.displayName || fieldName
              },
              verify: { required: false },
              tooltip: '',
              layout: layout
            }
          };

          return (
            <PreviewRender
              cpId={fieldName}
              cpType={cpType}
              detailMode={true}
              pageComponentSchema={componentConfig}
              runtime={true}
              recordId={_record.id}
            />
          );
        }
      }

      return <span>{_record[fieldName]}</span>;
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

    // 编辑
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

    // 删除
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

      handlePage(true);
    };

    const getPaginationPositionClass = () => {
      if (!paginationConfig?.display) {
        return '';
      }
      if (paginationConfig.pagePosition === 'tl') {
        return 'reverse list-left';
      } else if (paginationConfig.pagePosition === 'topCenter') {
        return 'reverse list-center';
      } else if (paginationConfig.pagePosition === 'tr') {
        return 'reverse list-right';
      } else if (paginationConfig.pagePosition === 'bl') {
        return 'list-left';
      } else if (paginationConfig.pagePosition === 'bottomCenter') {
        return 'list-center';
      } else if (paginationConfig.pagePosition === 'br') {
        return 'list-right';
      }
      return 'list-right';
    };

    return (
      <div
        style={{
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
          display: runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 'none' : 'unset'
        }}
      >
        <div className="cardHeader">
          {searchItems?.length ? (
            <div className="searchGroup">
              <Form form={form} layout="vertical" className="searchItems" labelAlign="left">
                <CardSearch
                  searchItems={searchItems}
                  labelColSpan={100}
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

              {/* todo 草稿 */}
            </div>
            <Button type="text" onClick={() => setCardPageNo(1)} icon={<IconRefresh />}></Button>
          </div>
        </div>
        <div className="cardContent">
          {label?.display && <div>{label.text}</div>}
          <Form
            form={cardForm}
            labelCol={layout === 'horizontal' ? { span: 10 } : {}}
            wrapperCol={layout === 'horizontal' ? { span: 14 } : {}}
            className={`cardListForm ${getPaginationPositionClass()}`}
          >
            {paginationConfig?.display ? (
              // 分页
              <List
                bordered={false}
                className="pageList"
                dataSource={cardData}
                grid={{ span: getSpan(), gutter: [20, 20] }}
                noDataElement={
                  <div style={{ padding: '10px 0 20px' }}>
                    <Empty />
                  </div>
                }
                render={renderListItem}
                pagination={{
                  pageSize: paginationConfig.pageSize,
                  showTotal: true,
                  current: cardPageNo,
                  total: cardTotal,
                  onChange: (pageNo: number) => {
                    setCardPageNo(pageNo);
                  }
                }}
              ></List>
            ) : (
              // 滚动加载
              <List
                bordered={false}
                dataSource={cardData}
                className={!runtime ? 'pageList' : undefined}
                scrollLoading={cardData.length < Number(cardTotal) ? <Spin loading={true} /> : undefined}
                grid={{ span: getSpan(), gutter: [20, 20] }}
                noDataElement={
                  <div style={{ padding: '10px 0 20px' }}>
                    <Empty />
                  </div>
                }
                render={renderListItem}
                style={{ maxHeight: 'calc(100vh - 180px)' }}
                onListScroll={(element) => {
                  if (paginationConfig?.display || cardData.length >= Number(cardTotal)) {
                    return;
                  }
                  if (element.scrollTop) {
                    // 小于12+scrollLoading的height(64)
                    if (element.scrollHeight - element.scrollTop - element.clientHeight <= 76) {
                      setCardPageNo((prev) => prev + 1);
                    }
                  }
                }}
              ></List>
            )}
          </Form>
        </div>
      </div>
    );
  }
);

export default XCard;
