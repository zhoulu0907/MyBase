import { STATUS_OPTIONS, STATUS_VALUES } from '@/components/Materials/constants';
import { COMPONENT_GROUP_NAME, EditRender, getComponentWidth, usePageEditorSignal, type GridItem } from '@/index';
import { Tabs } from '@arco-design/web-react';
import { useSignals } from '@preact/signals-react/runtime';
import { Fragment, memo, useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import './index.css';
import type { XTabsLayoutConfig } from './schema';

const TabPane = Tabs.TabPane;

const XPreviewTabsLayout = memo((props: XTabsLayoutConfig & { detailMode?: boolean }) => {
  const { id, defaultValue = [], type, colCount, tabPosition, pageType, detailMode } = props;
  useSignals();

  const {
    setCurComponentID,
    setCurComponentSchema,
    pageComponentSchemas,
    layoutSubComponents,
    setLayoutSubComponents,
    setShowDeleteButton
  } = usePageEditorSignal(pageType);

  const [activeTab, setActiveTab] = useState('1');
  const colComponents = layoutSubComponents[id] || Array.from({ length: colCount }, () => []);

  useEffect(() => {
    // 1. 从 props/state 中获取当前列的最新数据。
    const currentColumns = layoutSubComponents[id] || [];
    const newLength = defaultValue.length;

    // 2. 【核心保护逻辑】
    //    这是避免不必要更新和潜在无限循环的关键。
    //    只有当数组长度确实需要改变时，才继续执行。
    if (currentColumns.length !== newLength) {
      let updatedColumns;

      // 根据新旧长度的比较，决定是增加还是删减
      if (newLength > currentColumns.length) {
        const diff = newLength - currentColumns.length;
        const newEmptyArrays = Array.from({ length: diff }, () => []);
        updatedColumns = [...currentColumns, ...newEmptyArrays];
      } else {
        updatedColumns = currentColumns.slice(0, newLength);
      }

      setLayoutSubComponents(id, updatedColumns);
    }
  }, [defaultValue, id, layoutSubComponents, setLayoutSubComponents]);

  return (
    <Tabs
      className="XPreviewTabsLayout"
      activeTab={activeTab}
      type={type}
      tabPosition={tabPosition}
      onClickTab={(e) => setActiveTab(e)}
    >
      {defaultValue?.map((tab, index) => (
        <TabPane key={tab.key} title={tab.title} style={{ padding: 0 }}>
          <div className="item" key={index}>
            <ReactSortable
              id={`workspace-content-${id}`}
              className="content"
              list={colComponents[index]}
              setList={(newList) => {
                colComponents[index] = newList;
              }}
              group={{
                name: COMPONENT_GROUP_NAME
              }}
              animation={150}
            >
              {colComponents[index] &&
                colComponents[index]?.map((cp: GridItem) => (
                  <Fragment key={cp.id}>
                    {pageComponentSchemas[cp.id].config.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                      <div
                        key={cp.id}
                        data-cp-type={cp.type}
                        data-cp-displayname={cp.displayName}
                        data-cp-id={cp.id}
                        className="componentItem"
                        style={{
                          width: `calc(${getComponentWidth(pageComponentSchemas[cp.id], cp.type)} - 8px)`,
                          margin: '4px'
                        }}
                        onClick={(e: React.MouseEvent<HTMLDivElement>) => {
                          e.stopPropagation();
                          setCurComponentID(cp.id);
                          const curComponentSchema = pageComponentSchemas[cp.id];
                          setCurComponentSchema(curComponentSchema);
                          setShowDeleteButton(true);
                        }}
                      >
                        <EditRender
                          cpId={cp.id}
                          cpType={cp.type}
                          runtime={true}
                          pageComponentSchema={pageComponentSchemas[cp.id]}
                        />
                      </div>
                    )}
                  </Fragment>
                ))}
            </ReactSortable>
          </div>
        </TabPane>
      ))}
      /
    </Tabs>
  );
});

export default XPreviewTabsLayout;
