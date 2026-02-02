import type { EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Button, Drawer, Tabs } from '@arco-design/web-react';
import { IconCaretRight } from '@arco-design/web-react/icon';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import NodeEditForm from './tabs/NodeEditForm';
import DataRules from './tabs/DataRules';
import DataMethods from './tabs/DataMethods';
import Relations from './tabs/Relations';
import { ReactSVG } from 'react-svg';
import TabFirstSelectBgSVG from '@/assets/data_factory/tab_first.svg';
import TabFirstBgSVG from '@/assets/data_factory/white_tab_first.svg';
import TabLastSelectBgSVG from '@/assets/data_factory/tab_last.svg';
import TabLastBgSVG from '@/assets/data_factory/white_tab_last.svg';
import TabMiddleSelectBgSVG from '@/assets/data_factory/tab_middle.svg';
import TabMiddleBgSVG from '@/assets/data_factory/white_tab_middle.svg';
import styles from './index.module.less';

const EditEntityDrawer: React.FC<{
  editingNode: EntityNode;
  visible: boolean;
  setVisible: (visible: boolean) => void;
  onNodeEdit: (data: Partial<EntityNode>) => void;
  setEditingNode: (node: EntityNode | null) => void;
  successCallback?: () => void;
  onlyShowEntity?: boolean;
}> = ({ editingNode, visible, setVisible, onNodeEdit, setEditingNode, successCallback, onlyShowEntity = false }) => {
  const [isCollapsed, setIsCollapsed] = useState(false);
  const [activeTab, setActiveTab] = useState('entity');

  useEffect(() => {
    if (!visible) {
      setIsCollapsed(false);
    }
  }, [visible]);

  // 处理节点编辑
  const handleNodeEdit = (formData: Partial<EntityNode>) => {
    if (editingNode && onNodeEdit) {
      onNodeEdit(formData);
      setVisible(false);
      setEditingNode(null);
    }
  };

  // 切换抽屉展开/收起状态
  const toggleCollapse = () => {
    setIsCollapsed(!isCollapsed);
  };

  // 关闭抽屉
  const handleClose = useCallback(() => {
    setVisible(false);
    setIsCollapsed(false);
  }, [setVisible]);

  const changeTab = useCallback((key: string) => {
    setActiveTab(key);
  }, []);

  const renderTabContent = useMemo(() => {
    if (!editingNode) return null;

    switch (activeTab) {
      case 'entity':
        return (
          <NodeEditForm
            node={editingNode}
            onSave={handleNodeEdit}
            onCancel={handleClose}
            successCallback={successCallback || (() => {})}
          />
        );
      case 'relation':
        return <Relations node={editingNode} />;
      case 'rule':
        return <DataRules node={editingNode} />;
      case 'method':
        return <DataMethods node={editingNode} />;
      default:
        return null;
    }
  }, [activeTab, editingNode, handleNodeEdit, handleClose]);

  return (
    <>
      {/* 抽屉把手按钮 - 只在抽屉可见时显示 */}
      {visible && !onlyShowEntity && (
        // <Tooltip content={isCollapsed ? '展开抽屉' : '收起抽屉'}>
        <Button
          type="primary"
          shape="circle"
          size="large"
          icon={<IconCaretRight className={`${styles.icon} ${isCollapsed ? styles.collapsed : styles.expanded}`} />}
          onClick={toggleCollapse}
          className={`${styles.drawerHandleButton} ${isCollapsed ? styles.collapsed : styles.expanded}`}
        />
        // </Tooltip>
      )}

      {/* 左侧Tab导航 */}
      {!isCollapsed && visible && !onlyShowEntity && (
        <div className={styles['tab-sidebar']}>
          <Tabs
            activeTab={activeTab}
            onChange={changeTab}
            direction="vertical"
            className={styles['vertical-tabs']}
            renderTabTitle={(tabTitle, info) => {
              const tabBg = () => {
                if (info.isActive) {
                  if (info.key === 'entity') {
                    return TabFirstSelectBgSVG;
                  } else if (info.key === 'method') {
                    return TabLastSelectBgSVG;
                  } else {
                    return TabMiddleSelectBgSVG;
                  }
                } else {
                  if (info.key === 'entity') {
                    return TabFirstBgSVG;
                  } else if (info.key === 'method') {
                    return TabLastBgSVG;
                  } else {
                    return TabMiddleBgSVG;
                  }
                }
              };
              return (
                <span
                  style={{
                    position: 'relative'
                  }}
                >
                  {tabTitle}
                  <ReactSVG
                    style={{
                      position: 'absolute',
                      top: 'calc(50% + 2px)',
                      left: '50%',
                      transform: 'translate(-50%, -50%)',
                      zIndex: -1
                    }}
                    src={tabBg()}
                    beforeInjection={(svg) => {
                      const fillColor = 'rgb(var(--primary-6))';
                      svg.querySelectorAll('*').forEach((el) => {
                        if (el.getAttribute('fill') !== 'white' && el.getAttribute('fill') !== '#F7F8FA') {
                          el.setAttribute('fill', fillColor);
                        }
                      });
                    }}
                  />
                </span>
              );
            }}
          >
            <Tabs.TabPane key="entity" title="数据资产" />
            <Tabs.TabPane key="relation" title="关联关系" />
            <Tabs.TabPane key="rule" title="数据规则" />
            <Tabs.TabPane key="method" title="数据方法" />
          </Tabs>
        </div>
      )}

      <Drawer
        title={null}
        visible={visible && !isCollapsed}
        onCancel={handleClose}
        width={500}
        mask={false}
        placement="right"
        className={styles['edit-entity-drawer']}
        footer={null}
      >
        {editingNode && (
          <div className={styles['drawer-container']}>
            {/* 右侧内容区域 */}
            {renderTabContent}
          </div>
        )}
      </Drawer>
    </>
  );
};

export default EditEntityDrawer;
