import React, { useEffect } from 'react';
import { Drawer, Message } from '@arco-design/web-react';
import NodeEditForm from './EditForm';
import type { EntityNode } from '../../utils/interface';

interface FormValues {
  code: string;
  name: string;
  description: string;
  systemFields: {
    creator: boolean;
    updater: boolean;
    createTime: boolean;
    updateTime: boolean;
    dataOwner: boolean;
    dataDepartment: boolean;
  };
}

const DetailDrawer: React.FC<{ 
  editingNode: EntityNode, 
  visible: boolean, 
  setVisible: (visible: boolean) => void, 
  onNodeEdit: (data: EntityNode) => void, 
  setEditingNode: (node: EntityNode | null) => void,
  successCallback: () => void
}> = ({ 
  editingNode, 
  visible, 
  setVisible, 
  onNodeEdit, 
  setEditingNode,
  successCallback
}) => {
  
  useEffect(() => {
  }, []);

  // 处理节点编辑
  const handleNodeEdit = (formData: Partial<FormValues>) => {
    if (editingNode && onNodeEdit) {
      // onNodeEdit(formData);
      const { nodes } = JSON.parse(localStorage.getItem('entityFormValues') || JSON.stringify({ nodes: [] }));
      const nodeData = nodes.find((n: EntityNode) => n.id === editingNode.id);
      if (nodeData) {
        nodeData.id = editingNode.id;
        nodeData.code = formData.code || nodeData.code;
        nodeData.title = formData.name || nodeData.title;
        nodeData.description = formData.description || nodeData.description;
        nodeData.fields = nodeData.fields || [];
        if (formData.systemFields) {
          Object.keys(formData.systemFields).forEach((key: string) => {
            if (formData.systemFields && formData.systemFields[key as keyof typeof formData.systemFields]) {
              nodeData.fields.push({
                id: key,
                name: key,
                type: 'TEXT',
                isSystem: true,
              });
            }
          });
        }
      }
      onNodeEdit(nodeData);
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
            onSave={(data: Partial<FormValues>) => handleNodeEdit(data)}
            onCancel={() => setVisible(false)}
            successCallback={successCallback}
          />
        )}
    </Drawer>
  );
};

export default DetailDrawer;
