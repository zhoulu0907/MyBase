import { useState, useEffect, memo } from 'react';
import { Tabs, /* Divider */ } from '@arco-design/web-react';
import { ReactSortable } from 'react-sortablejs';
// import { v4 as uuidv4 } from 'uuid';

import {
  COMPONENT_GROUP_NAME,
  EditRender,
  getComponentSchema,
  getComponentWidth,
  getComponentConfig,
  type GridItem,
  usePageEditorSignal
} from '@/index';
import { useSignals } from '@preact/signals-react/runtime';
import CompDeleteIcon from '@/assets/images/app_delete.svg';
// import CompCopyIcon from '@/assets/images/copy_comp_icon.svg';
// import CompShowIcon from '@/assets/images/eye_off_icon.svg';
// import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XTabsConfig } from './schema';
import './index.css';

const TabPane = Tabs.TabPane;

const leftPanelWidth = 343;
const rightPanelWidth = 310;
const canvasPaddingWidth = 40 + 32 + 10;
const canvasMarginWidth = 10;
const componentMaxWidth = leftPanelWidth + rightPanelWidth + canvasPaddingWidth + canvasMarginWidth;

const XTabs = memo((props: XTabsConfig & { runtime?: boolean }) => {
  const { id, defaultValue = [], type, colCount, tabPosition, runtime = true } = props;
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
    setShowDeleteButton,
  } = usePageEditorSignal();

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

  // 取消隐藏组件
  // const handleShowComponent = (componentId: string) => {
  //   const schema = pageComponentSchemas[componentId];
  //   schema.config.status = STATUS_VALUES[STATUS_OPTIONS.DEFAULT];

  //   setPageComponentSchemas(componentId, schema);
  //   setCurComponentID(componentId);
  //   setCurComponentSchema(schema);
  //   setShowDeleteButton(false);
  // };

  // 复制组件
  // const handleCopyComponent = (comp: any, originId: string) => { };

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
    <Tabs activeTab={activeTab} type={type} tabPosition={tabPosition} onClickTab={(e) => setActiveTab(e)} style={{
      maxWidth: runtime ? '100%' : `calc(100vw - ${componentMaxWidth}px)`,
    }}>
      {
        defaultValue?.map((tab, index) => (
          <TabPane key={tab.key} title={tab.title} style={{ padding: 0 }}>
            <div className="item">

              <ReactSortable
                key={tab.key}
                id={`workspace-content-${id}`}
                className="content"
                list={colComponents[index]}
                setList={(newList) => {
                  colComponents[index] = newList;
                }}
                onAdd={(e) => {
                  // 允许拖入的组件
                  console.debug("onAdd", e.item.getAttribute('data-cp-type'));

                  let cpID = e.item.id || e.item.getAttribute('data-cp-id');
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
                {colComponents[index]?.map((cp: GridItem) => (
                  <div
                    key={cp.id}
                    data-cp-type={cp.type}
                    data-cp-displayname={cp.displayName}
                    data-cp-id={cp.id}
                    className='componentItem'
                    style={{
                      width: getComponentWidth(pageComponentSchemas[cp.id], cp.type),
                      borderColor: curComponentID === cp.id ? '#009E9E' : 'transparent',
                      borderStyle: curComponentID === cp.id ? 'solid' : 'dashed'
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
                      runtime={false}
                      pageComponentSchema={pageComponentSchemas[cp.id]}
                    />

                    {curComponentID === cp.id && showDeleteButton && (
                      <div className='operationArea'>
                        {/* {pageComponentSchemas[cp.id].config.status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
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
                            handleCopyComponent({ ...cp, id: `${cp.type}-${uuidv4()}` }, cp.id);
                          }}
                        >
                          <img src={CompCopyIcon} alt="component copy" />

                        </div>
                        <Divider className='divider' type="vertical" /> */}
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
          </TabPane>
        ))
      }
      /</Tabs>
  );
});

export default XTabs;
