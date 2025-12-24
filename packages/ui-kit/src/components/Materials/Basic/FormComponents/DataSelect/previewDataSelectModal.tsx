import { Button, Modal } from '@arco-design/web-react';
import React, { useState } from 'react';

import XTable from '../../ListComponents/Table';
import { XTableConfig } from '../../ListComponents/Table/schema';
import './index.css';

interface PreviewDataSelectModalProps {
  visible: boolean;
  onCancel: any;
  tableConfig: XTableConfig;
  onSelect: (data: any) => void;
  defaultSelectedId?: string | number | null;
}

const PreviewDataSelectModal: React.FC<PreviewDataSelectModalProps> = ({
  visible,
  onCancel,
  tableConfig,
  onSelect,
  defaultSelectedId
}) => {
  const [selectedRow, setSelectedRow] = useState<any>(null);

  const handleSelectData = (record: any | null, fromDoubleClick?: boolean) => {
    const next = record || null;
    setSelectedRow(next);
    if (fromDoubleClick && next) {
      onSelect(next);
      onCancel();
    }
  };

  return (
    <Modal
      className="filterDataModal"
      style={{ top: 50, width: '60vw' }}
      title={<span className="modalTitleLeft">数据选择</span>}
      visible={visible}
      onCancel={onCancel}
      footer={
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 8 }}>
          <Button onClick={onCancel}>取消</Button>
          <Button
            type="primary"
            disabled={!selectedRow}
            onClick={() => {
              if (selectedRow) {
                onSelect(selectedRow);
              }
              onCancel();
            }}
          >
            确认
          </Button>
        </div>
      }
      maskClosable={false}
    >
      <div className="popupContainer">
        <div className="rightFlexTable">
          {visible && (
            <XTable
              {...tableConfig}
              showAddBtn={false}
              xTableSelectProps={{ showSelect: true, defaultSelectedId, onSelectedChange: handleSelectData, hiddenDraft: true }}
            />
          )}
        </div>
      </div>
    </Modal>
  );
};

export default PreviewDataSelectModal;
