import { useEffect } from 'react';
import { useSignals } from '@preact/signals-react/runtime';
import LayoutReactSortable from '../components/layoutReactSortable';
import { COMPONENT_GROUP_NAME, LayoutSchema, usePageEditorSignal } from '@onebase/ui-kit';
import './index.css';

type XColumnLayoutConfig = typeof LayoutSchema.XPreviewColumnLayoutSchema.config;

const XColumnLayout = (props: XColumnLayoutConfig & { runtime?: boolean; detailMode?: boolean; editPreview?: boolean; useStoreSignals?: any; }) => {
  const { colCount, id, runtime = true, editPreview, useStoreSignals } = props;
  useSignals();

  const { layoutSubComponents, setLayoutSubComponents } = runtime && !editPreview ? usePageEditorSignal() : useStoreSignals;

  // 从 store 中获取当前组件的列数据，如果不存在则初始化为空数组
  const colComponents = layoutSubComponents[id] || Array.from({ length: colCount }, () => []);

  // 如果列数变了，就重新初始化列
  useEffect(() => {
    const currentColumns = layoutSubComponents[id];
    if (!currentColumns || currentColumns.length !== colCount) {
      const newColumns = Array.from({ length: colCount }, () => []);
      setLayoutSubComponents(id, newColumns);
    }
  }, [colCount, id, colComponents]);

  return (
    <div className="XColumnLayout">
      {colComponents.map((_colComponents: any, index: number) => (
        <div key={index} className="item">
          <LayoutReactSortable
            id={id}
            sortableId={`workspace-content-${id}-${index}`}
            colComponents={colComponents}
            groupName={COMPONENT_GROUP_NAME}
            index={index}
            runtime={runtime}
            editPreview={editPreview}
            useStoreSignals={useStoreSignals}
          ></LayoutReactSortable>
        </div>
      ))}
    </div>
  );
};

export default XColumnLayout;
