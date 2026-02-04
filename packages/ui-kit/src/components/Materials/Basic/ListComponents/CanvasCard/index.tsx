import { Card, Button } from '@arco-design/web-react';
import { IconPlus, IconRefresh } from '@arco-design/web-react/icon';
import { memo, useEffect, useState } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { isRuntimeEnv, menuPermissionSignal, pagesRuntimeSignal } from '@onebase/common';
import { dataMethodPageV2, getEntityFieldsWithChildren, menuSignal, PageType, getFormDataPage, getEntityFields } from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
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
  hiddenDraft?: boolean; 
  showAddBtn?: boolean; 
  refresh?: number;
  displayFields?: DisplayFieldsConfig;
  fieldList?: Array<{ fieldName: string; displayName: string }>;
  preview?: boolean;
}) => {
  useSignals();
  const { menuPermission, canCreate } = menuPermissionSignal;
  const { setRowDataId } = pagesRuntimeSignal;
  const { curMenu } = menuSignal;
  const { status, runtime = true, componentName = 'CanvasCardType1', showFromPageData, hiddenDraft, showAddBtn = true, tableName, metaData, displayFields, refresh, fieldList: propFieldList, preview } = props;

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

    return (
      <div className="canvas-card-list">
        {cardData.map((record) => (
          <Card key={record.id as string} className="XCanvasCard">
            {renderComponent(record)}
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
            {showAddBtn && canCreate.value && (
              <Button type="primary" onClick={handleCreate} icon={<IconPlus />}>
                添加数据
              </Button>
            )}

            {!hiddenDraft && canCreate.value && (
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
