import { IconDelete } from '@arco-design/web-react/icon';
import { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';

import { getComponentSchema } from '@/components/Materials/schema';
import { ALL_COMPONENT_TYPES } from '@/constants/componentTypes';
import { usePageEditorStore } from '@/hooks/useStore';
import ComponentRender from '@/pages/Editor/components/render';
import { COMPONENT_GROUP_NAME } from '../const';
import { getComponentWidth } from '../utils';

import EmptyIcon from '@/assets/images/empty.svg';
import MobileIcon from '@/assets/images/mobile_icon.svg';
import MobileActiveIcon from '@/assets/images/mobile_icon_active.svg';
import PCIcon from '@/assets/images/pc_icon.svg';
import PCActiveIcon from '@/assets/images/pc_icon_active.svg';

import 'react-grid-layout/css/styles.css';
import styles from './index.module.less';

interface GridItem {
  id: string;
  type: string;
  displayName: string;
}

export default function EditorWorkspace() {
  const [showEmpty, setShowEmpty] = useState(true);

  const {
    curComponentID,
    setCurComponentID,
    clearCurComponentID,
    setCurComponentSchema,
    pageComponentSchemas,
    setPageComponentSchemas,
    delPageComponentSchemas,
    components,
    setComponents,
    delComponents,
    showDeleteButton,
    setShowDeleteButton
  } = usePageEditorStore();

  const [pageMode, setPageMode] = useState<string>('pc');

  useEffect(() => {
    if (components.length === 0) {
      setShowEmpty(true);
    } else {
      setShowEmpty(false);
    }
  }, [components]);

  // 删除组件
  const handleDeleteComponent = (componentId: string) => {
    // 从组件列表中移除
    delComponents(componentId);

    // 如果删除的是当前选中的组件，清除选中状态
    if (curComponentID === componentId) {
      delPageComponentSchemas(componentId);
      clearCurComponentID();
    }
  };

  return (
    <div className={styles.formEditorWorkspace}>
      <div className={styles.workspaceHeader}>
        {pageMode === 'pc' && (
          <>
            <img className={styles.pageModeIcon} src={PCActiveIcon} />
            <img
              className={styles.pageModeIcon}
              style={{
                cursor: 'pointer'
              }}
              src={MobileIcon}
              onClick={() => setPageMode('mobile')}
            />
          </>
        )}
        {pageMode === 'mobile' && (
          <>
            <img
              className={styles.pageModeIcon}
              src={PCIcon}
              style={{
                cursor: 'pointer'
              }}
              onClick={() => setPageMode('pc')}
            />
            <img className={styles.pageModeIcon} src={MobileActiveIcon} />
          </>
        )}
      </div>

      <div
        className={styles.workspaceBody}
        id="workspace-body"
        onMouseDown={(e: React.MouseEvent<HTMLDivElement>) => {
          // 点击空白区域取消选中

          const target = e.target as HTMLElement;
          if (target.id === 'workspace-content') {
            clearCurComponentID();
            setShowDeleteButton(false);
          }
        }}
      >
        <ReactSortable
          id="workspace-content"
          list={components}
          setList={(newList) => {
            // console.debug("setList", newList);
            setComponents(newList);
            // console.log("pageComponentSchemas", pageComponentSchemas);
          }}
          onAdd={(e) => {
            console.log('onAdd', e);

            let cpID = e.item.id || e.item.getAttribute('data-cp-id');
            const itemType = e.item.getAttribute('data-cp-type');
            const itemDisplayName = e.item.getAttribute('data-cp-displayname');

            console.log(`拖入组件 ${cpID} ${itemType}`);

            const schema = getComponentSchema(itemType as any);
            // console.log("schema", schema)
            schema.config.cpName = itemDisplayName;
            schema.config.id = cpID;

            const props = {
              id: cpID,
              type: itemType,
              ...schema
            };

            if (itemType === ALL_COMPONENT_TYPES.COLUMN_LAYOUT) {
              // 拖入布局组件，根据配置创建初始化
              console.log('拖入布局组件，根据配置创建初始化: ', cpID);
            }

            setPageComponentSchemas(cpID!, props);
            setCurComponentID(cpID!);

            setCurComponentSchema(props);
            setShowDeleteButton(false);
          }}
          group={{ name: COMPONENT_GROUP_NAME }}
          sort={true}
          forceFallback={true}
          className={styles.workspaceContent}
          onStart={(e) => {
            console.log('onStart', e);
            const cpID = e.item.getAttribute('data-cp-id') || '';
            setCurComponentID(cpID);
            const curComponentSchema = pageComponentSchemas.get(cpID) || {};
            setCurComponentSchema(curComponentSchema);
            setShowDeleteButton(true);
          }}
        >
          {components.map((cp: GridItem) => (
            <div
              key={cp.id}
              data-cp-type={cp.type}
              data-cp-displayname={cp.displayName}
              data-cp-id={cp.id}
              className={styles.componentItem}
              style={{
                width: getComponentWidth(pageComponentSchemas.get(cp.id), cp.type),
                borderColor: curComponentID === cp.id ? '#4FAE7B' : 'transparent'
              }}
              onClick={(e: React.MouseEvent<HTMLDivElement>) => {
                e.stopPropagation();
                console.log('点击组件: ', cp.id);
                setCurComponentID(cp.id);

                const curComponentSchema = pageComponentSchemas.get(cp.id);
                setCurComponentSchema(curComponentSchema);
                console.log('当前组件的配置: ', curComponentSchema);
                setShowDeleteButton(true);
              }}
            >
              <ComponentRender cpId={cp.id} cpType={cp.type} pageComponentSchema={pageComponentSchemas.get(cp.id)} />

              {/* 删除按钮 */}
              {/* TODO(mickey): 组件继续封装，和layout中的共用一套 */}
              {curComponentID === cp.id && showDeleteButton && (
                <div
                  className={styles.deleteButton}
                  onClick={(e) => {
                    e.stopPropagation();
                    console.log('删除组件: ', cp.id);
                    handleDeleteComponent(cp.id);
                  }}
                >
                  <IconDelete />
                </div>
              )}
            </div>
          ))}
        </ReactSortable>

        {showEmpty && (
          <div className={styles.formEmpty}>
            <div className={styles.formEmptyContent}>
              <img src={EmptyIcon} alt="页面无组件" />
              拖拽左侧面板里的组件到这里
              <br />
              开始使用吧！
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
