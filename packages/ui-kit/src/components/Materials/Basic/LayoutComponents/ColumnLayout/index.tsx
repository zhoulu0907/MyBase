import { useEffect } from 'react';
import { v4 as uuidv4 } from 'uuid';
import { cloneDeep } from 'lodash-es';
import { Layout, Divider } from '@arco-design/web-react';
import { useSignals } from '@preact/signals-react/runtime';
import { ReactSortable } from 'react-sortablejs';
import { getComponentConfig, getComponentWidth } from 'src/components/Materials/schema';
import EditRender from 'src/components/render/EditRender';
import { usePageEditorSignal } from 'src/hooks/useSignal';
import { ALL_COMPONENT_TYPES } from '../../../componentTypes';
import { COMPONENT_GROUP_NAME, type GridItem } from 'src/utils/const';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import { getComponentSchema } from '../../../schema';
import { type XColumnLayoutConfig } from './schema';
import CompDeleteIcon from '@/assets/images/app_delete.svg';
import CompCopyIcon from '@/assets/images/copy_comp_icon.svg';
import CompShowIcon from '@/assets/images/eye_off_icon.svg';
import './index.css';

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
    const currentColumns = layoutSubComponents[id];
    if (!currentColumns || currentColumns.length !== colCount) {
      console.log('id', id, 'colCount', colCount);
      const newColumns = Array.from({ length: colCount }, () => []);
      setLayoutSubComponents(id, newColumns);
    }
  }, [colCount, id, colComponents]);


  // 取消隐藏组件
  const handleShowComponent = (componentId: string) => {
    const schema = pageComponentSchemas[componentId];
    schema.config.status = STATUS_VALUES[STATUS_OPTIONS.DEFAULT];

    setPageComponentSchemas(componentId, schema);
    setCurComponentID(componentId);
    setCurComponentSchema(schema);
    setShowDeleteButton(false);
  };

  // 复制组件
  const handleCopyComponent = (comp: any, originId: string, index: number) => {

    // ID 映射表，记录旧 ID 到新 ID 的映射
    const idMap = new Map<string, string>();
    idMap.set(originId, comp.id);

    let rootComponentProps = null; // 保存根组件的 props

    // 递归复制组件及其子组件
    function copyComponentRecursive(oldId: string, newId: string) {
      // 1. 复制组件配置
      const originalComp = pageComponentSchemas[oldId];
      if (!originalComp) return;

      // 深拷贝组件配置
      const schemaConfig = cloneDeep(
        getComponentConfig(pageComponentSchemas[oldId], comp.type)
      );
      const schema = getComponentSchema(comp.type);

      schema.config = schemaConfig;
      schema.config.cpName = comp.displayName || '';
      schema.config.id = newId;

      const newProps = {
        id: newId,
        type: comp.type,
        ...schema
      };

      // 如果是根组件，保存 props
      if (newId === comp.id) {
        rootComponentProps = newProps;
      }

      // 保存新组件配置
      setPageComponentSchemas(newId, newProps);

      // 2. 复制子组件结构
      if (layoutSubComponents[oldId]) {
        const newSubComponents = layoutSubComponents[oldId].map(row =>
          row.map(item => {
            // 为每个子组件创建新 ID
            const childNewId = idMap.get(item.id) || `${item.type}-${uuidv4()}`;

            // 记录子组件 ID 映射
            if (!idMap.has(item.id)) {
              idMap.set(item.id, childNewId);
              // 递归复制子组件
              copyComponentRecursive(item.id, childNewId);
            }

            // 返回更新了 ID 的子组件引用
            return {
              ...item,
              id: childNewId
            };
          })
        );

        // 保存子组件结构
        setLayoutSubComponents(newId, newSubComponents);
      }
    }

    // 开始递归复制
    copyComponentRecursive(originId, comp.id);

    // 3. 将复制的组件添加到当前布局组件的 layoutSubComponents 中
    if (layoutSubComponents[id]) {
      // 获取当前布局组件的子组件结构
      const currentLayoutSubComponents = layoutSubComponents[id];

      // 创建新的子组件引用
      const newSubComponentRef = {
        id: comp.id,
        type: comp.type,
      };

      // 添加到布局组件的对应列中
      const updatedLayoutSubComponents = [...currentLayoutSubComponents];
      if (updatedLayoutSubComponents[index]) {
        updatedLayoutSubComponents[index] = [...updatedLayoutSubComponents[index], newSubComponentRef];
      } else {
        updatedLayoutSubComponents[index] = [newSubComponentRef];
      }

      // 更新布局组件的子组件结构
      setLayoutSubComponents(id, updatedLayoutSubComponents);
    }

    // 设置当前组件
    setCurComponentID(comp.id!);
    setCurComponentSchema(rootComponentProps);
    setShowDeleteButton(false);

    console.log('布局内复制完成，ID 映射:', Object.fromEntries(idMap));
  };

  // 删除组件
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

                  {/* 操作按钮 */}
                  {curComponentID === cp.id && showDeleteButton && (
                    <div className='operationArea'>
                      {pageComponentSchemas[cp.id].config.status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                        <>
                          <div
                            className='copyButton'
                            onClick={(e) => {
                              e.stopPropagation();
                              console.debug('取消隐藏组件: ', cp);
                              handleShowComponent(cp.id);
                            }}
                          >
                            <img src={CompShowIcon} alt="component show" />
                          </div>
                          <Divider className='divider' type="vertical" />
                        </>
                      )}

                      <div
                        className='copyButton'
                        onClick={(e) => {
                          e.stopPropagation();
                          console.log('复制组件: ', cp);
                          handleCopyComponent({ ...cp, id: `${cp.type}-${uuidv4()}` }, cp.id, index);
                        }}
                      >
                        <img src={CompCopyIcon} alt="component copy" />
                      </div>
                      <Divider className='divider' type="vertical" />

                      <div
                        className='deleteButton'
                        onClick={(e) => {
                          e.stopPropagation();
                          console.log('删除组件: ', cp.id);
                          handleDeleteComponent(cp.id);
                        }}
                      >
                        <img src={CompDeleteIcon} alt="component delete" />
                      </div>
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
