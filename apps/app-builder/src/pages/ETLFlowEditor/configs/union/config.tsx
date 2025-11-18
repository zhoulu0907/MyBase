import React, { useState } from 'react';
import { ReactSortable } from 'react-sortablejs';

/**
 * Union 节点的配置主界面
 * 初始化页面，渲染 UnionNodeConfig 组件
 */
const UnionConfig: React.FC = () => {
  const [state, setState] = useState([
    { id: 1, name: '字段1', hasCol: true },
    { id: 2, name: '字段2', hasCol: false },
    { id: 3, name: '字段3', hasCol: true }
  ]);

  return (
    <div>
      {state.map((item) => (
        <div style={{ width: '120px', height: '120px', backgroundColor: 'blue', margin: '10px' }} key={item.id}>
          <ReactSortable
            swap // enables swap
            list={state}
            group="shared"
            setList={(newState) => {
              //   console.log('setList: ', newState);
              //   setState(newState);
            }}
            onAdd={(e) => {
              console.log('onAdd: ', e.item.getAttribute('data-id'), 'curId: ', item.id);
              const newState = [...state];

              const sourceIdx = newState.findIndex((item) => item.id == Number(e.item.getAttribute('data-id')));
              const targetIdx = newState.findIndex((itm) => itm.id == item.id);

              newState[targetIdx] = {
                ...newState[targetIdx],
                hasCol: true,
                name: newState[Number(sourceIdx)]?.name || ''
              };

              newState[Number(sourceIdx)] = {
                ...newState[Number(sourceIdx)],
                hasCol: false,
                name: ''
              };

              console.log('newState: ', newState);
              setState(newState);
            }}
            onRemove={(e) => {
              console.log('onRemove: ', e);
            }}
          >
            {/* {state.map((item) => (
              <div style={{ width: '100px', height: '100px', backgroundColor: 'red', margin: '10px' }} key={item.id}>
                {item.name}
              </div>
            ))} */}
            {item.hasCol && (
              <div
                style={{ width: '100px', height: '100px', backgroundColor: 'red', margin: '10px' }}
                key={item.id}
                data-id={item.id}
              >
                {item.name}
              </div>
            )}
          </ReactSortable>
        </div>
      ))}
    </div>
  );
};

export default UnionConfig;
