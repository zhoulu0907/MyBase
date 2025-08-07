import { Button, Drawer, Message } from "@arco-design/web-react";
import { IconCaretRight } from "@arco-design/web-react/icon";
import React, { useEffect, useState } from "react";
import type { EntityNode } from "../../../../utils/interface";
import styles from "./EditEntityDrawer.module.less";
import NodeEditForm from "./EditForm";

const DetailDrawer: React.FC<{
  editingNode: EntityNode;
  visible: boolean;
  setVisible: (visible: boolean) => void;
  onNodeEdit: (data: EntityNode) => void;
  setEditingNode: (node: EntityNode | null) => void;
  successCallback?: () => void;
}> = ({
  editingNode,
  visible,
  setVisible,
  onNodeEdit,
  setEditingNode,
  successCallback,
}) => {
  const [isCollapsed, setIsCollapsed] = useState(false);

  useEffect(() => {
    // 当抽屉关闭时，重置收起状态
    if (!visible) {
      setIsCollapsed(false);
    }
  }, [visible]);

  // 处理节点编辑
  const handleNodeEdit = (formData: EntityNode) => {
    if (editingNode && onNodeEdit) {
      onNodeEdit(formData);
      setVisible(false);
      setEditingNode(null);
      Message.success("节点信息已更新");
      if (successCallback) {
        successCallback();
      }
    }
  };

  // 切换抽屉展开/收起状态
  const toggleCollapse = () => {
    setIsCollapsed(!isCollapsed);
  };

  // 关闭抽屉
  const handleClose = () => {
    setVisible(false);
    setIsCollapsed(false);
  };

  return (
    <>
      {/* 抽屉把手按钮 - 只在抽屉可见时显示 */}
      {visible && (
        // <Tooltip content={isCollapsed ? '展开抽屉' : '收起抽屉'}>
        <Button
          type="primary"
          shape="circle"
          size="large"
          icon={
            <IconCaretRight
              className={`${styles.icon} ${isCollapsed ? styles.collapsed : styles.expanded}`}
            />
          }
          onClick={toggleCollapse}
          className={`${styles.drawerHandleButton} ${isCollapsed ? styles.collapsed : styles.expanded}`}
        />
        // </Tooltip>
      )}

      <Drawer
        title={null}
        visible={visible && !isCollapsed}
        onCancel={handleClose}
        width={500}
        style={{
          transition: "transform 0.3s ease",
        }}
        mask={false}
        placement="right"
        className={styles["edit-entity-drawer"]}
      >
        {editingNode && (
          <NodeEditForm
            node={editingNode}
            onSave={handleNodeEdit}
            onCancel={handleClose}
            successCallback={successCallback || (() => {})}
          />
        )}
      </Drawer>
    </>
  );
};

export default DetailDrawer;
