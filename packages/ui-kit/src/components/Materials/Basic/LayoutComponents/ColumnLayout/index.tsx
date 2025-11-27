import { Layout } from '@arco-design/web-react';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect } from 'react';
import { usePageEditorSignal } from 'src/hooks/useSignal';
import { COMPONENT_GROUP_NAME } from 'src/utils/const';
import './index.css';
import { type XColumnLayoutConfig } from './schema';
import LayoutReactSortable from '../components/layoutReactSortable';

const XColumnLayout = (props: XColumnLayoutConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { colCount, id, runtime = true } = props;

  useSignals();

  const { layoutSubComponents, setLayoutSubComponents } = usePageEditorSignal();

  // 从 store 中获取当前组件的列数据，如果不存在则初始化为空数组
  const colComponents = layoutSubComponents[id] || Array.from({ length: colCount }, () => []);
  //   console.log('colComponents', colComponents);

  // 如果列数变了，就重新初始化列
  useEffect(() => {
    const currentColumns = layoutSubComponents[id];
    if (!currentColumns || currentColumns.length !== colCount) {
      console.log('id', id, 'colCount', colCount);
      const newColumns = Array.from({ length: colCount }, () => []);
      setLayoutSubComponents(id, newColumns);
    }
  }, [colCount, id, colComponents]);

  return (
    <Layout className="XColumnLayout">
      {colComponents.map((_colComponents, index) => (
        <div key={index} className="item">
          <LayoutReactSortable
            id={id}
            sortableId={`workspace-content-${id}-${index}`}
            colComponents={colComponents}
            groupName={COMPONENT_GROUP_NAME}
            index={index}
            runtime={runtime}
          ></LayoutReactSortable>
        </div>
      ))}
    </Layout>
  );
};

export default XColumnLayout;
