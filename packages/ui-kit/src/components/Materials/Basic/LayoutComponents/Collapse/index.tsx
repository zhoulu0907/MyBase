import { useEffect, useState, memo } from 'react';
// import { v4 as uuidv4 } from 'uuid';
import { ReactSortable } from 'react-sortablejs';
import { Collapse, /* Divider */ } from '@arco-design/web-react';
import { useSignals } from '@preact/signals-react/runtime';

import {
  COMPONENT_GROUP_NAME,
  EditRender,
  getComponentSchema,
  getComponentWidth,
  getComponentConfig,
  type GridItem,
  usePageEditorSignal
} from '@/index';

import type { XCollapseConfig } from './schema';
import { STATUS_OPTIONS, STATUS_VALUES, COLLAPSED_VALUES, COLLAPSED_OPTIONS } from '../../../constants';
import CompDeleteIcon from '@/assets/images/app_delete.svg';
// import CompCopyIcon from '@/assets/images/copy_comp_icon.svg';
// import CompShowIcon from '@/assets/images/eye_off_icon.svg';
import IconCollapsedDown from '@/assets/images/collapse_down_icon.svg';

const CollapseItem = Collapse.Item;

const XCollapse = memo((props: XCollapseConfig & { runtime?: boolean }) => {
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
    setShowDeleteButton,
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
    <Collapse
      bordered={false}
      activeKey={activeKey}
      expandIconPosition='right'
      expandIcon={<img src={IconCollapsedDown} alt='' />}
      onChange={(_, key) => setActiveKey(key)}
      style={{ border: '1px solid #d4d4d4', opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1, pointerEvents: runtime && status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? 'none' : 'auto' }}
    >
      <CollapseItem header={label.text} name='1' contentStyle={{ backgroundColor: '#fff', paddingLeft: 13, paddingTop: 20, borderTop: '1px solid #ccc' }}>
        {colComponents.map((_colComponents, index) => (
          <ReactSortable
            key={index}
            id={`workspace-content-${id}-${index}`}
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
                    runtime={runtime}
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
        ))}
      </CollapseItem>
    </Collapse>
  );
});

export default XCollapse;
