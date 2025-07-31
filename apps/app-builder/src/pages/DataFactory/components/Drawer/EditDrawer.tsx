import React, { useState, useEffect } from 'react';
import { Drawer, Message } from '@arco-design/web-react';
import NodeEditForm from './EditForm';
import type { EntityNode } from '../../utils/interface';



const DetailDrawer: React.FC<{ 
  editingNode: EntityNode, 
  visible: boolean, 
  setVisible: (visible: boolean) => void, 
  onNodeEdit: (nodeId: string, data: Record<string, unknown>) => void, 
  setEditingNode: (node: EntityNode | null) => void
}> = ({ 
  editingNode, 
  visible, 
  setVisible, 
  onNodeEdit, 
  setEditingNode 
}) => {
  
  useEffect(() => {
  }, []);

  // 处理节点编辑
  const handleNodeEdit = (formData: Record<string, unknown>) => {
    if (editingNode && onNodeEdit) {
      onNodeEdit(editingNode.id, formData);
      setVisible(false);
      setEditingNode(null);
      Message.success('节点信息已更新');
    }
  };


  return (
    <Drawer
      title="编辑节点"
      visible={visible}
      onCancel={() => setVisible(false)}
      width={500}
    >
      {editingNode && (
          <NodeEditForm
            node={editingNode}
            onSave={handleNodeEdit}
            onCancel={() => setVisible(false)}
          />
        )}
    </Drawer>
  );
};

export default DetailDrawer;
