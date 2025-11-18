import React, { useEffect, useState } from 'react'
import { FormInstance, Modal, Pagination, Tree } from '@arco-design/web-react';
import { IconCaretDown } from '@arco-design/web-react/icon';

import './index.css';
import XTable from '../../ListComponents/Table';
import { XTableConfig } from '../../ListComponents/Table/schema';

interface PreviewDataSelectModalProps {
  visible: boolean;
  onCancel: any;
  tableConfig: XTableConfig;
  displayFields: any;
  form: FormInstance;
  fieldName: string;
  initialSelectedId: string;
}

// mock up
const treeData = [
  {
    title: '全部',
    key: '0-0',
    // selectable: false, // 父节点不可选
    children: [
      {
        key: 'ciki',
        title: 'Ciki',
        icon: <IconCaretDown />
      }
    ]
  }
];

const fastFilters = [1];

const PreviewDataSelectModal: React.FC<PreviewDataSelectModalProps> = ({ visible, onCancel, tableConfig,displayFields,form ,fieldName,initialSelectedId }) => {
  //数据选择runtime下的单选数据功能
  const [selectedId, setSelectedId] = useState<string | null>('');

  useEffect(() => {
    setSelectedId(initialSelectedId);
  }, [initialSelectedId]);

  const handleSelectData = (data: any) => {
    setSelectedId(data ? data.id : null);
    const fieldsWithValue = (displayFields || []).map((field: any) => ({
      ...field,
      dataValue: data ? data[field.value] : null
    }));
    const lastKey = (displayFields || []).length ? displayFields[displayFields.length - 1]?.value : undefined;
    const raw = lastKey ? data?.[lastKey] : '';
    form.setFieldValue(fieldName, data ? {selectID: data.id, dataFields: fieldsWithValue, displayValue: raw} : '');
    if(data) onCancel();
  };
    
  return (
    <Modal
        className="filterDataModal"
        getPopupContainer={() => document.querySelector('[class*="previewPage"]') || document.body}
        style={{top: 50, width: '900px'}}
        title={<span className="modalTitleLeft">数据选择</span>} 
        visible={visible}
        onCancel={onCancel}
        footer={null}
        autoFocus={false}
        focusLock={true}
        escToExit={false}
        maskClosable={false}
      >
        <div className="popupContainer">
            <div className="content">
                {/* {fastFilters.length > 0 && (
                    <div className="leftTree">
                        <Tree treeData={treeData}></Tree>
                    </div>
                )} */}
                <div className="rightFlexTable">
                    <XTable {...tableConfig} showAddBtn={false}
                      xTableSelectProps={{showSelect: true, selectedDataId: selectedId, setSelectData: handleSelectData}}/>
                </div>
            </div>
             {/* <div className='bottomDiv'>
                <Pagination
                    total={3}
                    current={1}
                    pageSize={20}
                    showTotal
                    sizeCanChange
                    showJumper={false}
                    pageSizeChangeResetCurrent={false}
                    style={{ justifyContent: 'flex-end' }}
                    />
            </div> */}
        </div>
      </Modal>
  )
}

export default PreviewDataSelectModal