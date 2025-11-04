import { Collapse, Divider, Tooltip } from '@arco-design/web-react';
import { useSignals } from '@preact/signals-react/runtime';
import { cloneDeep } from 'lodash-es';
import { memo, useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { v4 as uuidv4 } from 'uuid';

import {
  COMPONENT_GROUP_NAME,
  EditRender,
  getComponentConfig,
  getComponentSchema,
  getComponentWidth,
  type GridItem,
  usePageEditorSignal
} from '@/index';

import CompDeleteIcon from '@/assets/images/app_delete.svg';
import IconCollapsedDown from '@/assets/images/collapse_down_icon.svg';
import CompCopyIcon from '@/assets/images/copy_comp_icon.svg';
import CompShowIcon from '@/assets/images/eye_off_icon.svg';
import { COLLAPSED_OPTIONS, COLLAPSED_VALUES, STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import './index.css';
import type { XCollapseLayoutConfig } from './schema';

const CollapseItem = Collapse.Item;

const XCollapseLayout = memo((props: XCollapseLayoutConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { id, label, colCount = 1, status, collapsed, runtime = true } = props;
  useSignals();

  const {
    curComponentID,
    setCurComponentID,
    clearCurComponentID,
    setCurComponentSchema,
    pageComponentSchemas,
    setPageComponentSchemas,
    delPageComponentSchemas,
    layoutSubComponents,
    setLayoutSubComponents,
    showDeleteButton,
    setShowDeleteButton
  } = usePageEditorSignal();

  const colComponents = layoutSubComponents[id] || Array.from({ length: colCount }, () => []);
  const [activeKey, setActiveKey] = useState<string[]>([]);

  useEffect(() => {
    const currentColumns = layoutSubComponents[id];
    if (!currentColumns || currentColumns.length !== colCount) {
      const newColumns = Array.from({ length: colCount }, () => []);
      setLayoutSubComponents(id, newColumns);
    }
  }, [colCount, id, colComponents]);

  useEffect(() => {
    setActiveKey(collapsed === COLLAPSED_VALUES[COLLAPSED_OPTIONS.EXPOSED] ? ['1'] : []);
  }, [collapsed]);

  // 组件状态同步到子组件
  useEffect(() => {
    if (colComponents[0]) {
      colComponents[0].forEach((comp) => {
        const schema = pageComponentSchemas[comp.id];
        schema.config.status = status;
        setPageComponentSchemas(comp.id, schema);
      });
    }
  }, [status]);

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
  const handleCopyComponent = (comp: any, originId: string, index: number = 0) => {
    // ID 映射表，记录旧 ID 到新 ID 的映射
    const idMap = new Map<string, string>();
    idMap.set(originId, comp.id);

    let rootComponentProps = null;

    // 递归复制组件及其子组件
    function copyComponentRecursive(oldId: string, newId: string) {
      // 1. 复制组件配置
      const originalComp = pageComponentSchemas[oldId];
      if (!originalComp) return;

      // 深拷贝组件配置
      const schemaConfig = cloneDeep(getComponentConfig(pageComponentSchemas[oldId], comp.type));
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
        const newSubComponents = layoutSubComponents[oldId].map((row) =>
          row.map((item) => {
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
        type: comp.type
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
    const updatedColumns = colComponents.map((col) => col.filter((cp) => cp.id !== componentId));
    setLayoutSubComponents(id, updatedColumns);

    // 如果删除的是当前选中的组件，清除选中状态
    if (curComponentID === componentId) {
      delPageComponentSchemas(componentId);
      clearCurComponentID();
    }
  };

  return (
    <Collapse
      className="XCollapseLayout"
      bordered={false}
      activeKey={activeKey}
      expandIconPosition="right"
      expandIcon={<img src={IconCollapsedDown} alt="" />}
      onChange={(_, key) => setActiveKey(key)}
      style={{ opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1 }}
    >
      <CollapseItem
        header={
          <Tooltip content={label.text}>
            <div className="collapse-title-ellipsis">{label.text}</div>
          </Tooltip>
        }
        name="1"
        contentStyle={{ backgroundColor: '#fff', paddingLeft: 13, paddingTop: 5, borderTop: '1px solid #ccc' }}
      >
        {colComponents.map((_colComponents, index) => (
          <div className="item" key={index}>
            <ReactSortable
              id={`workspace-content-${id}-${index}`}
              className="content"
              list={colComponents[index]}
              setList={(newList) => {
                colComponents[index] = newList;
              }}
              onAdd={(e) => {
                // 允许拖入的组件
                console.debug('onAdd', e.item.getAttribute('data-cp-type'));

                let cpID = e.item.id || e.item.getAttribute('data-cp-id');
                const itemType = e.item.getAttribute('data-cp-type');
                const itemDisplayName = e.item.getAttribute('data-cp-displayname');

                const schemaConfig = getComponentConfig(pageComponentSchemas[cpID!], itemType!);
                const schema = getComponentSchema(itemType as any);

                schema.config = schemaConfig;
                schema.config.cpName = itemDisplayName;
                schema.config.id = cpID;
                schema.config.status = status;

                const props = {
                  id: cpID,
                  type: itemType,
                  ...schema
                };

                setCurComponentID(cpID!);
                setCurComponentSchema(props);
                setPageComponentSchemas(cpID!, props);
                setShowDeleteButton(false);
              }}
              onRemove={(e) => {
                const cpID = e.item.getAttribute('data-cp-id');
                console.log(`删除组件${id}内， 索引为， 删除组件为 ${cpID}`);
              }}
              group={{
                name: COMPONENT_GROUP_NAME
              }}
              sort={true}
              forceFallback={true}
              animation={150}
              fallbackOnBody={true}
              swapThreshold={0.65}
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
                      width: `calc(${getComponentWidth(pageComponentSchemas[cp.id], cp.type)} - 8px)`,
                      borderColor: curComponentID === cp.id ? '#009E9E' : 'transparent',
                      borderStyle: curComponentID === cp.id ? 'solid' : 'dashed',
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
                      runtime={runtime}
                      pageComponentSchema={pageComponentSchemas[cp.id]}
                    />

                    {/* 操作按钮 */}
                    {curComponentID === cp.id && showDeleteButton && (
                      <div className="operationArea">
                        {pageComponentSchemas[cp.id].config.status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                          <>
                            <div
                              className="copyButton"
                              onClick={(e) => {
                                e.stopPropagation();
                                console.debug('取消隐藏组件: ', cp);
                                handleShowComponent(cp.id);
                              }}
                            >
                              <img src={CompShowIcon} alt="component show" />
                            </div>
                            <Divider className="divider" type="vertical" />
                          </>
                        )}

                        <div
                          className="copyButton"
                          onClick={(e) => {
                            e.stopPropagation();
                            console.log('复制组件: ', cp);
                            handleCopyComponent({ ...cp, id: `${cp.type}-${uuidv4()}` }, cp.id);
                          }}
                        >
                          <img src={CompCopyIcon} alt="component copy" />
                        </div>
                        <Divider className="divider" type="vertical" />

                        <div
                          className="deleteButton"
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
      </CollapseItem>
    </Collapse>
  );
});

export default XCollapseLayout;
