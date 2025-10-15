import React from 'react'
import { Modal, Pagination, Tree } from '@arco-design/web-react';
import { IconCaretDown } from '@arco-design/web-react/icon';

import './index.css';
import XTable from '../../ListComponents/Table';
import { XTableConfig } from '../../ListComponents/Table/schema';

interface PreviewDataSelectModalProps {
  visible: boolean;
  onCancel: any;
  tableConfig: XTableConfig;
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

const PreviewDataSelectModal: React.FC<PreviewDataSelectModalProps> = ({ visible, onCancel, tableConfig }) => {
  return (
    <Modal
        className="filterDataModal"
        getPopupContainer={() => document.querySelector('[class*="previewPage"]') || document.body}
        style={{top: 50, width: '900px'}}
        title={<span className="modalTitleLeft">选择数据</span>} 
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
                    <XTable {...tableConfig} isConfig={true}/>
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