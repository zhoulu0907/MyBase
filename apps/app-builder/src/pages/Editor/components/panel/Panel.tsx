import IconLayoutActive from '@/assets/images/edit_layout_active_icon.svg';
import IconLayout from '@/assets/images/edit_layout_icon.svg';
import IconEntity from '@/assets/images/entity_icon.svg';
import IconEntityActive from '@/assets/images/entity_icon_active.svg';
import { Tabs, Tooltip } from '@arco-design/web-react';
import { EDITOR_TYPES, type EditorType } from '@onebase/ui-kit';
import { useEffect, useState } from 'react';
import MaterialContainer from './components/material';
import MetadataContainer from './components/metadata';
import styles from './index.module.less';

// 定义类型
const PANNEL_KEYS = ['material', 'metadata'] as const;
type PannelKey = (typeof PANNEL_KEYS)[number];

export default function EditorPanel() {
  const [activeTab, setActiveTab] = useState<EditorType>(EDITOR_TYPES.FORM_EDITOR);
  const [activeLeftTabKey, setActiveLeftTabKey] = useState<PannelKey>('material');
  const [childCollapsed, setChildCollapsed] = useState<PannelKey | string | undefined>('material');

  useEffect(() => {
    // 根据当前 URL 动态设置 activeTab
    const hash = window.location.hash;
    if (hash.includes(EDITOR_TYPES.FORM_EDITOR)) {
      setActiveTab(EDITOR_TYPES.FORM_EDITOR);
    } else if (hash.includes(EDITOR_TYPES.LIST_EDITOR)) {
      setActiveTab(EDITOR_TYPES.LIST_EDITOR);
    } else if (hash.includes(EDITOR_TYPES.PAGE_SETTING)) {
      setActiveTab(EDITOR_TYPES.PAGE_SETTING);
    } else if (hash.includes(EDITOR_TYPES.METADATA_MANAGE)) {
      setActiveTab(EDITOR_TYPES.METADATA_MANAGE);
    } else if (hash.includes(EDITOR_TYPES.WORKBENCH_EDITOR)) {
      setActiveTab(EDITOR_TYPES.WORKBENCH_EDITOR);
    }
  }, []);

  return (
    <div
      className={styles.editorPanel}
      style={{
        transition: 'width 0.3s cubic-bezier(0.4, 0, 0.4, 1)'
      }}
    >
      {/* <Button
        size="mini"
        className={styles.drawerButton}
        icon={showDrawer ? <IconLeft /> : <IconRight />}
        type="dashed"
        onClick={() => setShowDrawer(!showDrawer)}
      /> */}
      <div
        className={styles.left}
        // style={{
        //   width: showDrawer ? '48px' : '0px'
        // }}
      >
        <Tabs
          type="text"
          activeTab={activeLeftTabKey}
          onChange={(key) => {
            setActiveLeftTabKey(key as PannelKey);
          }}
          size="large"
          direction="vertical"
          onClickTab={(key: string) => {
            setChildCollapsed((prev) => (prev === key ? undefined : key));
          }}
        >
          <Tabs.TabPane
            key={'material'}
            title={
              <div className={styles.tabButton}>
                <Tooltip mini content="组件库" position="right">
                  <img
                    src={
                      childCollapsed === 'material' && activeLeftTabKey === 'material' ? IconLayoutActive : IconLayout
                    }
                  />
                </Tooltip>
              </div>
            }
          />
          {activeTab === EDITOR_TYPES.FORM_EDITOR && (
            <Tabs.TabPane
              key={'metadata'}
              title={
                <div className={styles.tabButton}>
                  <Tooltip mini content="业务实体" position="right">
                    <img
                      src={
                        childCollapsed === 'metadata' && activeLeftTabKey === 'metadata' ? IconEntityActive : IconEntity
                      }
                    />
                  </Tooltip>
                </div>
              }
            />
          )}
        </Tabs>
      </div>

      <div
        className={styles.right}
        // style={{
        //   width: showDrawer ? '100%' : '0px'
        // }}
      >
        {activeLeftTabKey === 'material' && (
          <MaterialContainer
            activeTab={activeTab}
            childCollapsed={childCollapsed}
            setChildCollapsed={() => setChildCollapsed(undefined)}
          />
        )}
        {activeLeftTabKey === 'metadata' && (
          <MetadataContainer childCollapsed={childCollapsed} setChildCollapsed={() => setChildCollapsed(undefined)} />
        )}
      </div>
    </div>
  );
}
