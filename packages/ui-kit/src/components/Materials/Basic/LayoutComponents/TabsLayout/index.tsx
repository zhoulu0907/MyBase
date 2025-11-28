import { Tabs } from '@arco-design/web-react';
import { memo, useEffect, useState } from 'react';
import { COMPONENT_GROUP_NAME, usePageEditorSignal } from '@/index';
import { useSignals } from '@preact/signals-react/runtime';
import './index.css';
import type { XTabsLayoutConfig } from './schema';
import LayoutReactSortable from '../components/layoutReactSortable';

const TabPane = Tabs.TabPane;

const leftPanelWidth = 318;
const rightPanelWidth = 310;
const canvasPaddingWidth = 40;
const componentMaxWidth = leftPanelWidth + rightPanelWidth + canvasPaddingWidth;

const XTabsLayout = memo((props: XTabsLayoutConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { id, defaultValue = [], type, colCount, tabPosition, runtime = true } = props;
  useSignals();

  const { layoutSubComponents, setLayoutSubComponents } = usePageEditorSignal();

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
      className="XTabsLayout"
      activeTab={activeTab}
      type={type}
      tabPosition={tabPosition}
      onClickTab={(e) => setActiveTab(e)}
      style={{
        maxWidth: runtime ? '100%' : `calc(100vw - ${componentMaxWidth}px)`
      }}
    >
      {defaultValue?.map((tab, index) => (
        <TabPane key={tab.key} title={tab.title} style={{ padding: 0 }}>
          <div className="item" key={index}>
            <LayoutReactSortable
              id={id}
              sortableId={`workspace-content-${id}-${index}`}
              colComponents={colComponents}
              groupName={COMPONENT_GROUP_NAME}
              index={index}
              runtime={runtime}
            ></LayoutReactSortable>
          </div>
        </TabPane>
      ))}
      /
    </Tabs>
  );
});

export default XTabsLayout;
