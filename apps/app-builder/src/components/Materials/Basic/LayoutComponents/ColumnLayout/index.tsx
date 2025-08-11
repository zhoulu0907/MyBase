import { getComponentSchema } from '@/components/Materials/schema';
import { ALL_COMPONENT_TYPES } from '@/constants/componentTypes';
import { usePageEditorStore } from '@/hooks/useStore';
import EditRender from '@/pages/Editor/components/render/EditRender';
import { getComponentConfig, getComponentWidth } from '@/pages/Editor/utils/app_resource';
import { COMPONENT_GROUP_NAME, type GridItem } from '@/pages/Editor/utils/const';
import { Layout } from '@arco-design/web-react';
import { IconDelete } from '@arco-design/web-react/icon';
import { useEffect } from 'react';
import { ReactSortable } from 'react-sortablejs';
import styles from './index.module.less';
import { type XColumnLayoutConfig } from './schema';

const XColumnLayout = (props: XColumnLayoutConfig) => {
  const { colCount, id } = props;

  const {
    curComponentID,
    setCurComponentID,
    clearCurComponentID,
    setCurComponentSchema,
    pageComponentSchemas,
    setPageComponentSchemas,
    delPageComponentSchemas,
    showDeleteButton,
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

  const handleDeleteComponent = (componentId: string) => {
    // 从组件列表中移除
    // 遍历二维数组的每一列，过滤掉 id 匹配的组件
    const updatedColumns = colComponents.map((col) => col.filter((cp) => cp.id !== componentId));
    setColComponentsMap(id, updatedColumns);

    // 如果删除的是当前选中的组件，清除选中状态
    if (curComponentID === componentId) {
      delPageComponentSchemas(componentId);
      clearCurComponentID();
    }
  };

  return (
    <Layout className={styles.XColumnLayout}>
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
            onAdd={(e) => {
              // console.log("onAdd", e);

              // console.log("e.item.id", e.item.getAttribute('data-cp-id'))
              // console.log("e.item.getAttribute('data-cp-type')", e.item.getAttribute('data-cp-type'))
              // console.log("e.item.getAttribute('data-cp-displayname')", e.item.getAttribute('data-cp-displayname'))

              const cpID = e.item.id || e.item.getAttribute('data-cp-id');
              console.log(`拖入组件${id}内， 索引为${index}， 拖入组件为 ${cpID}`);
              const itemType = e.item.getAttribute('data-cp-type');
              const itemDisplayName = e.item.getAttribute('data-cp-displayname');

              const schemaConfig = getComponentConfig(pageComponentSchemas.get(cpID!), itemType!);

              const schema = getComponentSchema(itemType as any);

              schema.config = schemaConfig;
              schema.config.cpName = itemDisplayName;
              schema.config.id = cpID;

              const props = {
                id: cpID,
                type: itemType,
                ...schema
              };

              if (itemType === ALL_COMPONENT_TYPES.COLUMN_LAYOUT) {
                console.log('创建布局组件: ', cpID);
              }

              setPageComponentSchemas(cpID!, props);

              setCurComponentID(cpID!);
              setCurComponentSchema(props);

              setShowDeleteButton(false);
            }}
            onRemove={(e) => {
              const cpID = e.item.getAttribute('data-cp-id');
              console.log(`删除组件${id}内， 索引为${index}， 删除组件为 ${cpID}`);
            }}
            group={{
              name: COMPONENT_GROUP_NAME
            }}
            sort={true}
            forceFallback={true}
            animation={150}
            fallbackOnBody={true}
            swapThreshold={0.65}
            className={styles.content}
            onStart={(e) => {
              console.log('onStart', e);
              const cpID = e.item.getAttribute('data-id') || '';
              setCurComponentID(cpID);
              const curComponentSchema = pageComponentSchemas.get(cpID) || {};
              setCurComponentSchema(curComponentSchema);
              setShowDeleteButton(true);
            }}
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
                    width: getComponentWidth(pageComponentSchemas.get(cp.id), cp.type),
                    borderColor: curComponentID === cp.id ? '#4FAE7B' : 'transparent'
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
                  <EditRender cpId={cp.id} cpType={cp.type} pageComponentSchema={pageComponentSchemas.get(cp.id)} />

                  {/* 删除按钮 */}
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
        </div>
      ))}
    </Layout>
  );
};

export default XColumnLayout;
