import React, { useState } from 'react'
import { Modal } from '@arco-design/web-react';

import './index.css';
import XTable from '../../ListComponents/Table';
import { XTableConfig } from '../../ListComponents/Table/schema';

interface PreviewDataSelectModalProps {
  visible: boolean;
  onCancel: any;
  tableConfig: XTableConfig;
  onSelect: (data: any) => void;
}

const PreviewDataSelectModal: React.FC<PreviewDataSelectModalProps> = ({ visible, onCancel, tableConfig, onSelect }) => {
  const [selectedId, setSelectedId] = useState<string | null>('');

  const handleSelectData = (data: any) => {
    setSelectedId(data ? data.id : null);
    onSelect(data);
    if (data) onCancel();
  };

  return (
    <Modal
      className="filterDataModal"
      style={{ top: 50, width: '900px' }}
      title={<span className="modalTitleLeft">数据选择</span>}
      visible={visible}
      onCancel={onCancel}
      footer={null}
      maskClosable={false}
    >
      <div className="popupContainer">
        <div className="rightFlexTable">
          <XTable
            {...tableConfig}
            showAddBtn={false}
            xTableSelectProps={{ showSelect: true, selectedDataId: selectedId, setSelectData: handleSelectData }}
          />
        </div>
      </div>
    </Modal>
  )
}

export default PreviewDataSelectModal