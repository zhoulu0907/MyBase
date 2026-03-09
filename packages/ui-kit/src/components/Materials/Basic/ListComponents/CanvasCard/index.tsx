import { Card, Button, Menu, Popover, Message, Popconfirm } from '@arco-design/web-react';
import { IconMoreVertical, IconPlus, IconRefresh } from '@arco-design/web-react/icon';
import { memo, useEffect, useState } from 'react';
import { RedirectMethod, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { isRuntimeEnv, menuPermissionSignal, pagesRuntimeSignal } from '@onebase/common';
import {
  CATEGORY_TYPE,
  dataMethodDeleteV2,
  dataMethodPageV2,
  deleteFormDataPage,
  DeleteMethodV2Params,
  getEntityFieldsWithChildren,
  getFormDataPage,
  getEntityFields,
  menuSignal,
  PageType,
  queryFlowExecForm,
  TRIGGER_EVENTS
} from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import editIcon from '@/assets/images/edit_icon.svg';
import deleteIcon from '@/assets/images/app_delete.svg';
import type { XCanvasCardConfig, DisplayFieldsConfig } from './schema';
import CanvasCardType1 from './components/CanvasCardType1';
import CanvasCardType2 from './components/CanvasCardType2';
import { CanvasCardDraftBox } from './DraftBox';
import './index.css';

export type { DisplayFieldsConfig };

const XCanvasCard = memo((props: XCanvasCardConfig & { 
  runtime?: boolean; 
  detailMode?: boolean; 
  componentName?: string; 
  showFromPageData?: Function; 
  refresh?: number;
  displayFields?: DisplayFieldsConfig;
  fieldList?: Array<{ fieldName: string; displayName: string }>;
  preview?: boolean;
}) => {
  useSignals();
  const { canCreate, canEdit, canDelete } = menuPermissionSignal;
  const { curPage, setRowDataId, setFlows, setBpmInstanceId, setRowDataType, setDrawerVisible,
    setDetailPageViewId } = pagesRuntimeSignal;
  const { curMenu } = menuSignal;
  const { status, runtime = true, componentName = 'CanvasCardType1', showFromPageData, tableName, metaData, displayFields, refresh, fieldList: propFieldList, preview } = props;

  const [cardData, setCardData] = useState<Record<string, unknown>[]>([]);
  const [loading, setLoading] = useState(false);
  const [fieldList, setFieldList] = useState<Array<{ fieldName: string; displayName: string }>>(propFieldList || []);

  useEffect(() => {
    if (runtime && isRuntimeEnv() && metaData && tableName) {
      handleFetchData();
      handleFetchFieldList();
    }
  }, [runtime, metaData, tableName, refresh]);

  useEffect(() => {
    if ((!runtime || preview) && metaData) {
      handleFetchFieldList();
    }
  }, [runtime, metaData, preview]);

  const handleFetchFieldList = async () => {
    if (!metaData) {
      return;
    }

    try {
      const entityWithChildren = await getEntityFieldsWithChildren(metaData);
      const fields = (entityWithChildren.parentFields || []).map((item: any) => ({
        fieldName: item.fieldName,
        displayName: item.displayName || item.fieldName
      }));
      setFieldList(fields);
    } catch (error) {
      console.error('获取字段列表失败:', error);
    }
  };

  const handleFetchData = async () => {
    if (!runtime || !metaData || !tableName || !isRuntimeEnv()) {
      return;
    }

    setLoading(true);
    try {
      const req = {
        pageNo: 1,
        pageSize: 20,
        filters: {
          nodeType: 'GROUP',
          combinator: 'AND',
          children: []
        }
      };

      let res: any;
      if (props?.pageSetType === PageType.BPM) {
        res = await getFormDataPage({
          menuId: curMenu.value?.id,
          tableName,
          ...req
        });
      } else {
        res = await dataMethodPageV2(tableName, curMenu.value?.id, req);
      }

      const { list } = res || {};
      const newCardData = (list || []).map((item: any) => {
        const rowId = (item && item.id) || (item?.data && item.data.id);
        return {
          id: rowId,
          ...item
        };
      });

      setCardData(newCardData);
    } catch (error) {
      console.error('Failed to fetch card data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    console.log('点击新增');

    if (!runtime || !isRuntimeEnv()) {
      return;
    }

    setRowDataId('');
    showFromPageData?.(null, true);
  };

  // 同Table组件编辑逻辑
  const handleEdit = (record: Record<string, unknown>) => {
    if (!runtime || !isRuntimeEnv()) {
      return;
    }

    const anyRecord = record as any;
    const id = anyRecord?.id as string;

    if (!id) {
      return;
    }

    if (anyRecord.bpm_instance_id) {
      setRowDataType(PageType.BPM);
      setBpmInstanceId(anyRecord.bpm_instance_id);
    } else {
      setRowDataType(PageType.NORMAL);
      setBpmInstanceId('');
    }

    setRowDataId(id);
    showFromPageData?.(id, true);
  };

  // 同Table组件删除逻辑
  const handleDelete = async (id: string) => {
    if (!runtime || !isRuntimeEnv()) {
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
      id
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
      res = await dataMethodDeleteV2(tableName as string, curMenu.value?.id, req);
    }

    if (res) {
      Message.success('删除成功');
    }

    handleFetchData();
  };

  // 同Table组件行点击查看表单详情逻辑
  const handleRowClick = (record: any) => {
    if (!runtime) {
      return;
    }

    if (!record) {
      return;
    }

    // 从 curPage.pages 中查找表单详情页面uuid
    const formPage = curPage.value?.pages?.find((ele: any) => ele.pageType === CATEGORY_TYPE.FORM);
    const detailPageId = formPage?.pageUuid;

    if (!detailPageId) {
      console.warn('未找到表单详情页面');
      return;
    }

    setDrawerVisible(true);
    
    if (record.bpm_instance_id) {
      setRowDataType(PageType.BPM);
      setBpmInstanceId(record.bpm_instance_id);
    } else {
      setRowDataType(PageType.NORMAL);
      setBpmInstanceId('');
    }
    
    // 设置详情页面ID
    setDetailPageViewId(detailPageId);
    
    // 设置行数据ID并加载数据
    setRowDataId(record.id);
    showFromPageData?.(record.id, false);
  };

  const renderComponent = (record?: Record<string, unknown>) => {
    const cardProps = {
      ...props,
      record,
      displayFields,
      fieldList
    };

    switch (componentName) {
      case 'CanvasCardType1':
        return <CanvasCardType1 {...cardProps} />;
      case 'CanvasCardType2':
        return <CanvasCardType2 {...cardProps} />;
      default:
        return <CanvasCardType1 {...cardProps} />;
    }
  };

  const renderCardList = () => {
    if (!runtime || !isRuntimeEnv()) {
      return renderComponent(undefined);
    }

    if (cardData.length === 0) {
      return (
        <div className="canvas-card-empty">
          {loading ? (
            <span>加载中...</span>
          ) : (
            <span>暂无数据</span>
          )}
        </div>
      );
    }

  const renderOperateBtns = (record: Record<string, unknown>) => {
    if (!runtime || !isRuntimeEnv()) {
      return;
    }

    return (
      <Popover position='rt' trigger={['hover', 'click']} className='operate-popover' content={
        <>
          {canEdit.value && (
            <div
              key="edit"
              className="operate-more-btn-item"
              onClick={(e) => {
                e.stopPropagation();
                handleEdit(record);
              }}
            >
              <img src={editIcon} alt="edit" />
              编辑
            </div>
          )}
          {canDelete.value && (
            <Popconfirm
              focusLock
              title="确认删除"
              content="确认删除该数据？"
              disabled={preview}
              onOk={(event) => {
                event?.stopPropagation?.();
                if (record.id) {
                  handleDelete(record.id as string);
                }
              }}
            >
              <div
                key="delete"
                className="operate-more-btn-item delete-text"
                onClick={(e) => {
                  e.stopPropagation();
                }}
              >
                <img src={deleteIcon} alt="delete" />
                删除
              </div>
            </Popconfirm>
          )}
        </>
      }>
        <div className='operate-more-btn' onClick={(e) => e.stopPropagation()}>
          <IconMoreVertical />
        </div>
      </Popover>
    );
  };

    return (
      <div className="canvas-card-list">
        {cardData.map((record) => (
          <Card key={record.id as string} className="XCanvasCard" onClick={() => handleRowClick(record)}>
            {renderComponent(record)}
            {renderOperateBtns(record)}
          </Card>
        ))}
      </div>
    );
  };

  return (
    <div className="canvas-card-container" style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
        display: runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 'none' : 'block'
      }}>
      <div className="canvas-card-header">
        <div className="headerActions">
          <div className="addButton">
            {(!runtime || canCreate.value) && (
              <Button type="primary" onClick={handleCreate} icon={<IconPlus />}>
                添加数据
              </Button>
            )}

            {(!runtime || canCreate.value) && (
              <CanvasCardDraftBox
                showFromPageData={showFromPageData}
                menuId={curMenu.value?.id}
                tableName={tableName || ''}
                refresh={refresh}
              />
            )}
          </div>
          <Button type="text" onClick={() => handleFetchData()} icon={<IconRefresh />} loading={loading}></Button>
        </div>
      </div>
      {renderCardList()}
    </div>
  );
});

export default XCanvasCard;
