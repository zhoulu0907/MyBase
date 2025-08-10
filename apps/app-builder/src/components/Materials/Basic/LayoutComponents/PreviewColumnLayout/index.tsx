import { usePageEditorStore } from '@/hooks/useStore';
import PreviewRender from '@/pages/Editor/components/render/PreviewRender';
import { getComponentWidth } from '@/pages/Editor/utils/app_resource';
import { COMPONENT_GROUP_NAME, type GridItem } from '@/pages/Editor/utils/const';
import { Layout } from '@arco-design/web-react';
import { useEffect } from 'react';
import { ReactSortable } from 'react-sortablejs';
import styles from './index.module.less';
import { type XColumnLayoutConfig } from './schema';

const XPreviewColumnLayout = (props: XColumnLayoutConfig) => {
  const { colCount, id } = props;

  const {
    curComponentID,
    setCurComponentID,
    clearCurComponentID,
    setCurComponentSchema,
    pageComponentSchemas,
    delPageComponentSchemas,
    setShowDeleteButton,
    colComponentsMap,
    setColComponentsMap
  } = usePageEditorStore();

  // 从 store 中获取当前组件的列数据，如果不存在则初始化为空数组
  const colComponents = colComponentsMap.colComponents.get(id) || Array.from({ length: colCount }, () => []);
  //   console.log('colComponents', colComponents);

  // 如果列数变了，就重新初始化列
  useEffect(() => {
    const currentColumns = colComponentsMap.colComponents.get(id);
    if (!currentColumns || currentColumns.length !== colCount) {
      setColComponentsMap(
        id,
        Array.from({ length: colCount }, () => [])
      );
    }
  }, [colCount, id, colComponentsMap.colComponents, setColComponentsMap]);

  return (
    <Layout className={styles.XPreviewColumnLayout}>
      {colComponents.map((_colComponents, index) => (
        <div key={index} className={styles.item}>
          <ReactSortable
            id={`workspace-content-${id}-${index}`}
            list={colComponents[index]}
            setList={(newList) => {
              // 使用函数式更新确保状态更新的原子性
              setColComponentsMap(id, (prevColumns: any[][]) => {
                const updatedColumns = [...(prevColumns || [])];
                updatedColumns[index] = newList;
                return updatedColumns;
              });
            }}
            group={{
              name: COMPONENT_GROUP_NAME
            }}
            animation={150}
            className={styles.content}
          >
            {colComponents[index] &&
              colComponents[index].map((cp: GridItem) => (
                <div
                  key={cp.id}
                  data-cp-type={cp.type}
                  data-cp-displayname={cp.displayName}
                  data-cp-id={cp.id}
                  className={styles.componentItem}
                  style={{
                    width: getComponentWidth(pageComponentSchemas.get(cp.id), cp.type)
                  }}
                  onClick={(e: React.MouseEvent<HTMLDivElement>) => {
                    e.stopPropagation();
                    console.log('点击组件: ', cp.id);
                    setCurComponentID(cp.id);

                    const curComponentSchema = pageComponentSchemas.get(cp.id);
                    setCurComponentSchema(curComponentSchema);
                    setShowDeleteButton(true);
                  }}
                >
                  <PreviewRender cpId={cp.id} cpType={cp.type} pageComponentSchema={pageComponentSchemas.get(cp.id)} />
                </div>
              ))}
          </ReactSortable>
        </div>
      ))}
    </Layout>
  );
};

export default XPreviewColumnLayout;
