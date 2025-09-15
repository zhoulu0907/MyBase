import { Layout } from '@arco-design/web-react';
import { IconDelete } from '@arco-design/web-react/icon';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { getComponentConfig, getComponentWidth } from 'src/components/Materials/schema';
import EditRender from 'src/components/render/EditRender';
import { usePageEditorSignal } from 'src/hooks/useSignal';
import { COMPONENT_GROUP_NAME, type GridItem } from 'src/utils/const';
import { ALL_COMPONENT_TYPES } from '../../../componentTypes';
import { getComponentSchema } from '../../../schema';
import './index.css';
import { type XColumnLayoutConfig } from './schema';

const XColumnLayout = (props: XColumnLayoutConfig & { runtime?: boolean }) => {
  const { colCount, id, runtime = true } = props;

  useSignals();

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

    layoutSubComponents,
    setLayoutSubComponents
  } = usePageEditorSignal();

  // 从 store 中获取当前组件的列数据，如果不存在则初始化为空数组
  const colComponents = layoutSubComponents[id] || Array.from({ length: colCount }, () => []);
  //   console.log('colComponents', colComponents);

  // 如果列数变了，就重新初始化列
  useEffect(() => {
    console.log('layoutSubComponents:  ', colComponents);

    const currentColumns = layoutSubComponents[id];
    if (!currentColumns || currentColumns.length !== colCount) {
      console.log('id', id, 'colCount', colCount);
      const newColumns = Array.from({ length: colCount }, () => []);
      setLayoutSubComponents(id, newColumns);
    }
  }, [colCount, id, colComponents]);

  const handleDeleteComponent = (componentId: string) => {
    // 从组件列表中移除
    // 遍历二维数组的每一列，过滤掉 id 匹配的组件
    const updatedColumns = colComponents.map((col) => col.filter((cp) => cp.id !== componentId));
    setLayoutSubComponents(id, updatedColumns);

    // 如果删除的是当前选中的组件，清除选中状态
    if (curComponentID === componentId) {
      delPageComponentSchemas(componentId);
      clearCurComponentID();
    }
  };

  return (
    <Layout className="XColumnLayout">
      {colComponents.map((_colComponents, index) => (
        <div key={index} className="item">
          <ReactSortable
            id={`workspace-content-${id}-${index}`}
            list={colComponents[index]}
            setList={(newList) => {
              // 使用函数式更新确保状态更新的原子性
              //   setColComponentsMap(id, (prevColumns: any[][]) => {
              //     const updatedColumns = [...(prevColumns || [])];
              //     updatedColumns[index] = newList;
              //     return updatedColumns;
              //   });

              //   const updatecolComponents = colComponents;
              //   updatecolComponents[index] = newList;
              //   setLayoutSubComponents(id, updatecolComponents);
              colComponents[index] = newList;
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

              const schemaConfig = getComponentConfig(pageComponentSchemas[cpID!], itemType!);

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
            className="content"
            onStart={(e) => {
              console.log('onStart', e);
              const cpID = e.item.getAttribute('data-id') || '';
              setCurComponentID(cpID);
              const curComponentSchema = pageComponentSchemas[cpID] || {};
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
                  className="componentItem"
                  style={{
                    width: getComponentWidth(pageComponentSchemas[cp.id], cp.type),
                    borderColor: curComponentID === cp.id ? '#4FAE7B' : 'transparent'
                  }}
                  onClick={(e: React.MouseEvent<HTMLDivElement>) => {
                    e.stopPropagation();
                    console.log('点击组件: ', cp.id);
                    setCurComponentID(cp.id);

                    const curComponentSchema = pageComponentSchemas[cp.id];
                    setCurComponentSchema(curComponentSchema);
                    setShowDeleteButton(true);
                  }}
                >
                  <EditRender runtime={runtime} cpId={cp.id} cpType={cp.type} pageComponentSchema={pageComponentSchemas[cp.id]} />

                  {/* 删除按钮 */}
                  {curComponentID === cp.id && showDeleteButton && (
                    <div
                      className="deleteButton"
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
