import { type EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Drawer } from '@arco-design/web-react';
import { FIELD_TYPE } from '@onebase/ui-kit';
import React, { useEffect } from 'react';

const DetailDrawer: React.FC<{
  selectedNode: EntityNode;
  visible: boolean;
  setVisible: (visible: boolean) => void;
}> = ({ selectedNode, visible, setVisible }) => {
  useEffect(() => {}, []);

  return (
    <Drawer title={selectedNode?.entityName} visible={visible} onCancel={() => setVisible(false)} width={400}>
      {selectedNode && (
        <div className="node-details">
          <div className="detail-item">
            <label>业务对象名称:</label>
            <span>{selectedNode.entityName}</span>
          </div>
          <div className="detail-item">
            <label>业务对象编码:</label>
            <span>boc_code_{selectedNode.entityId}</span>
          </div>
          <div className="detail-item">
            <label>业务对象类型:</label>
            <span>自定义对象</span>
          </div>
          <div className="detail-item">
            <label>字段数量:</label>
            <span>{selectedNode?.fields?.length}</span>
          </div>
          <div className="fields-list">
            <h4>字段列表:</h4>
            {selectedNode?.fields?.length &&
              selectedNode.fields.map((field, index) => (
                <div key={`field-${index}`} className="field-item">
                  <span className="field-name">{field.fieldName}</span>
                  <span className="field-type">{field.fieldType}</span>
                  {field.isSystemField === FIELD_TYPE.SYSTEM && <span className="system-tag">系统</span>}
                </div>
              ))}
          </div>
        </div>
      )}
    </Drawer>
  );
};

export default DetailDrawer;
