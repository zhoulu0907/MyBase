import { Card, Button } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { isRuntimeEnv, menuPermissionSignal, pagesRuntimeSignal } from '@onebase/common';
import { menuSignal } from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import type { XCanvasCardConfig } from './schema';
import CanvasCardType1 from './components/CanvasCardType1';
import CanvasCardType2 from './components/CanvasCardType2';
import { CanvasCardDraftBox } from './DraftBox';
import './index.css';

const XCanvasCard = memo((props: XCanvasCardConfig & { runtime?: boolean; detailMode?: boolean; componentName?: string; showFromPageData?: Function; hiddenDraft?: boolean; showAddBtn?: boolean; refresh?: number }) => {
  useSignals();
  const { menuPermission, canCreate } = menuPermissionSignal;
  const { setRowDataId } = pagesRuntimeSignal;
  const { curMenu } = menuSignal;
  const { status, runtime = true, config, componentName = 'CanvasCardType1', showFromPageData, hiddenDraft, showAddBtn = true, tableName, metaData, refresh } = props;

  const handleCreate = () => {
    console.log('点击新增');

    if (!runtime || !isRuntimeEnv()) {
      return;
    }

    setRowDataId('');
    showFromPageData?.(null, true);
  };

  const renderComponent = () => {
    switch (componentName) {
      case 'CanvasCardType1':
        return <CanvasCardType1 {...props} />;
      case 'CanvasCardType2':
        return <CanvasCardType2 {...props} />;
      default:
        return <CanvasCardType1 {...props} />;
    }
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
                tableName={tableName}
                refresh={refresh}
              />
            )}
          </div>
        </div>
      </div>
      <Card className="XCanvasCard">
        {renderComponent()}
      </Card>
    </div>
  );
});

export default XCanvasCard;