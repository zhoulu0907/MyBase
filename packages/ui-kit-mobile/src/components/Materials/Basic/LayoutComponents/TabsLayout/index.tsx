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
    const currentColumns = layoutSubComponents[id];
    if (!currentColumns || currentColumns.length !== defaultValue.length) {
      setLayoutSubComponents(
        id,
        Array.from({ length: defaultValue.length }, () => [])
      );
    }
  }, [defaultValue, id, colComponents, setLayoutSubComponents]);

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
