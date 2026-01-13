import { memo, useEffect } from 'react';
import { Tabs } from '@arco-design/mobile-react';
import { useSignals } from '@preact/signals-react/runtime';
import { COMPONENT_GROUP_NAME, LayoutSchema, usePageEditorSignal } from '@onebase/ui-kit';
import LayoutReactSortable from '../components/layoutReactSortable';
import './index.css';

type TabsType = 'line' | 'card' | 'tag' | 'line-divide' | 'tag-divide';
type XTabsLayoutConfig = typeof LayoutSchema.XTabsLayoutSchema.config;

const mobileTypes = ["line", "card", "line-divide", "tag", "tag-divide"];

const XTabsLayout = memo((props: XTabsLayoutConfig & { runtime?: boolean; detailMode?: boolean; useStoreSignals?: any; editPreview?: boolean; }) => {
  const { id, defaultValue = [], type = '', colCount, tabPosition, runtime = true, useStoreSignals, editPreview } = props;
  useSignals();

  const { layoutSubComponents, setLayoutSubComponents } = runtime && !editPreview ? usePageEditorSignal() : useStoreSignals;

  const colComponents = layoutSubComponents[id] || Array.from({ length: colCount }, () => []);

  const finalType: TabsType =
    mobileTypes.includes(type as TabsType)
      ? (type as TabsType)
      : 'line';

  useEffect(() => {
    // 1. 从 props/state 中获取当前列的最新数据。
    const currentColumns = layoutSubComponents[id] || [];
    const newLength = defaultValue.length;

      console.log(currentColumns, newLength, 'newLength')
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
      tabs={defaultValue?.map((tab: any) => ({ title: tab.title }))}
      type={finalType}
      tabBarPosition={tabPosition}
      defaultActiveTab={0}
      tabBarHasDivider={false}
    >
      {defaultValue?.map((_tab: any, index: number) => (
        <div className="item" key={index}>
          <LayoutReactSortable
            id={id}
            sortableId={`workspace-content-${id}-${index}`}
            colComponents={colComponents}
            groupName={COMPONENT_GROUP_NAME}
            index={index}
            runtime={runtime}
            editPreview={editPreview}
            useStoreSignals={useStoreSignals}
          />
        </div>
      ))}
    </Tabs>
  );
});

export default XTabsLayout;
