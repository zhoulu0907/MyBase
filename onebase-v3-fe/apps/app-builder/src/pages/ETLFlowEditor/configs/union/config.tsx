import { Button } from '@arco-design/web-react';
import { IconClose, IconPlusCircleFill } from '@arco-design/web-react/icon';
import { etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import { cloneDeep } from 'lodash-es';
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { getSourceNodeIdsByTarget, setNodeDataAndResetDownstream } from '../utils';
import styles from './index.module.less';

type UnionConfigProps = {
  onRegisterSave?: (fn: () => void) => void;
  newPayload: any;
  setNewPayload: (payload: any) => void;
};

/**
 * Union 节点的配置主界面
 * 初始化页面，渲染 UnionNodeConfig 组件
 */
const UnionConfig: React.FC<UnionConfigProps> = ({ onRegisterSave, newPayload, setNewPayload }) => {
  useSignals();

  const { setNodeData, curNode, nodeData, graphData } = etlEditorSignal;

  // 优先从 newPayload 恢复状态，如果没有则从 nodeData 初始化
  const [data, setData] = useState<any[]>(
    cloneDeep(newPayload?.config?.data || nodeData.value[curNode.value.id].config?.data) || []
  );

  // 计算所有 columns 字段的去重并集
  const [colTitles, setColTitles] = useState<any[]>(
    cloneDeep(newPayload?.config?.colTitles || nodeData.value[curNode.value.id].config?.colTitles) || []
  );
  const [emptyColumn, setEmptyColumn] = useState<string[]>([]);

  //   const [newPayload, setNewPayload] = useState<any>(cloneDeep(nodeData.value[curNode.value.id]));

  useEffect(() => {
    onRegisterSave?.(handleSaveInner);
  }, [onRegisterSave]);

  useEffect(() => {
    const tmpData = [];
    const tmpColumns = [];

    let sourceNodeIds = getSourceNodeIdsByTarget(graphData.value, curNode.value.id);
    if (sourceNodeIds && sourceNodeIds.length > 0) {
      for (const sourceNodeId of sourceNodeIds) {
        const sourceNodeData = nodeData.value[sourceNodeId];

        // console.log('sourceNodeData: ', sourceNodeData);
        // console.log(sourceNodeData.output?.fields);

        if (sourceNodeData?.output) {
          tmpColumns.push(...sourceNodeData.output?.fields);
        }
      }
      for (const sourceNodeId of sourceNodeIds) {
        const sourceNodeData = nodeData.value[sourceNodeId];
        tmpData.push({
          tableId: sourceNodeId,
          tableName: sourceNodeData.title,
          columns: tmpColumns.map((col) => {
            // 判断当前columns是否存在col.fieldFqn（对齐Union逻辑）
            const sourceCols = sourceNodeData.output?.fields || [];
            const exists = sourceCols.some((field: any) => field.fieldFqn === col.fieldFqn);

            if (exists) {
              // 如果当前 source 有这个字段，保持 name 和 fieldFqn
              return { ...col };
            } else {
              // 否则，name 清空，fieldFqn 也清空
              return {
                ...col,
                fieldName: ''
              };
            }
          })
        });
      }
    }

    const newColTitles = tmpData
      .flatMap((item) => item.columns)
      .filter((col) => col.fieldName && col.fieldName.trim() !== '')
      .map((col) => col);

    // 优先使用 newPayload 中的配置数据，这样可以保留用户在切换标签页时的修改
    // 只有在当前 data 为空且 newPayload 有配置时，才从 newPayload 恢复
    // 或者在没有 payload 配置且当前 data 为空或长度不匹配时才重置
    const hasPayloadConfig = newPayload?.config?.data && newPayload.config.data.length > 0;
    if (hasPayloadConfig && data.length === 0) {
      // 如果 newPayload 有配置且当前 data 为空，恢复状态（用于组件重新挂载时恢复用户修改）
      setData(cloneDeep(newPayload.config.data));
      if (newPayload.config.colTitles && newPayload.config.colTitles.length > 0) {
        setColTitles(cloneDeep(newPayload.config.colTitles));
      }
    } else if (!hasPayloadConfig && (data.length == 0 || data.length != tmpData.length)) {
      // 只有在没有 payload 配置且当前 data 为空或长度不匹配时才重置
      setData(tmpData);
      setColTitles(newColTitles);

      console.log('tmpData: ', tmpData);
      console.log('newColTitles: ', newColTitles);
    }
  }, [nodeData, graphData, newPayload]);

  const handleSaveInner = () => {
    setNodeDataAndResetDownstream(newPayload, curNode.value.id, graphData.value, nodeData.value);
  };
  useEffect(() => {
    console.log(data);
    const empty = getEmptyFieldFqns(colTitles, data);
    setEmptyColumn(empty);
    const payload = newPayload;

    payload.config = {
      data: data,
      colTitles: colTitles
    };
    payload.output = {
      verified: true,
      fields: colTitles.map((col: any) => ({
        fieldFqn: `${curNode.value.id}.${col.fieldName}`,
        fieldName: col.fieldName,
        fieldType: col.fieldType,
        displayName: col.displayName
      }))
    };
    console.log(payload);
    setNewPayload(payload);
  }, [data, colTitles]);

  const getEmptyFieldFqns = (colTitles: any[], rows: any[]): string[] => {
    return colTitles
      .map((c) => c.fieldFqn)
      .filter((fieldFqn) =>
        // 对于每个 fieldFqn，检查所有 row
        rows.every((row) => {
          const col = row.columns.find((c: any) => c.fieldFqn === fieldFqn);
          // 没有该列 或 fieldName 为空 => 视为这一行在该列没数据
          return !col || !col.fieldName;
        })
      );
  };

  const handleRemoveColumn = (index: number, fieldFqn: string) => {
    setColTitles([...colTitles.slice(0, index), ...colTitles.slice(index + 1)]);

    const idx = emptyColumn.indexOf(fieldFqn);
    setEmptyColumn([...emptyColumn.slice(0, idx), ...emptyColumn.slice(idx + 1)]);
  };

  return (
    <div className={styles.dataConfig}>
      <div className={styles.rowHeader}>
        <div className={styles.rowTitle}>合并结果</div>
        {colTitles.map((col: any, index) => (
          <div key={col.id} className={styles.colTitle}>
            {emptyColumn.includes(col.fieldFqn) && (
              <Button
                type="text"
                size="mini"
                shape="circle"
                onClick={() => handleRemoveColumn(index, col.fieldFqn)}
                icon={<IconClose />}
              />
            )}
            {col.fieldName}
          </div>
        ))}
      </div>
      {data.map((row, rowIndex) => {
        return (
          <div key={row.tableId} className={styles.row}>
            <div className={styles.rowTitle}>
              <div>{row.tableName}</div>
              <IconPlusCircleFill style={{ fontSize: 16, color: 'rgba(var(--primary-6))' }} onClick={() => {}} />
            </div>

            {colTitles.map((col: any) => {
              return (
                <ReactSortable
                  key={`${row.tableId}-${col.fieldFqn}`}
                  className={styles.colTitle}
                  swap // enables swap
                  list={row.columns.filter((c: any) => c.fieldFqn === col.fieldFqn)}
                  group={{
                    name: row.tableId,
                    put: (to, from, target) => {
                      // 1. 只允许在当前行拖拽
                      // 2. 当前行内的列如果有元素，不允许拖拽
                      if (from.el.childNodes.length > 0) {
                        if (row.tableId != (from.el.childNodes[0] as any).dataset.rowId) {
                          return false;
                        }
                      }

                      if (to.el.childNodes.length > 0) {
                        return false;
                      }

                      if (target.getAttribute('data-row-id') != row.tableId) {
                        return false;
                      }

                      return true;
                    }
                  }}
                  setList={(newRow) => {
                    // 只记录变化，不立即更新状态
                    // console.log('row: ', rowIndex, 'col: ', col.fieldFqn, ' setList: ', newRow);

                    if (newRow.length > 1) {
                      if (newRow[0].fieldName != '' && newRow[1].fieldName != '') {
                        return;
                      }

                      let targetId = '';
                      let targetName = '';
                      let sourceId = '';

                      for (const item of newRow) {
                        if (item.fieldName != '') {
                          sourceId = item.fieldFqn;
                          targetName = item.fieldName;
                        }
                        if (item.fieldName == '') {
                          targetId = item.fieldFqn;
                        }
                      }

                      const newData = data.map((item) => {
                        if (item.tableId == row.tableId) {
                          return {
                            ...item,
                            columns: item.columns.map((c: any) =>
                              c.fieldFqn === targetId
                                ? { ...c, fieldName: targetName }
                                : c.fieldFqn === sourceId
                                  ? { ...c, fieldName: '' }
                                  : c
                            )
                          };
                        }
                        return item;
                      });

                      console.log('newData: ', newData);
                      setData(newData);

                      const empty = getEmptyFieldFqns(colTitles, newData);
                      setEmptyColumn(empty);
                    }
                  }}
                  //   onEnd={() => {}}
                  //   onAdd={(e) => {
                  //     console.log('onAdd: ', rowIndex, 'col: ', col.id, e.item.id);
                  //   }}
                  //   onRemove={(e) => {
                  //     console.log('onRemove: ', rowIndex, 'col: ', col.id, e.item.id);
                  //   }}
                >
                  {row.columns.find((c: any) => c.fieldFqn == col.fieldFqn)?.fieldName !== '' && (
                    <div
                      key={`row-${rowIndex}-col-${col.fieldFqn}`}
                      className={styles.colItem}
                      data-row-id={row.tableId}
                      data-col-id={col.fieldFqn}
                    >
                      {row.columns.find((c: any) => c.fieldFqn === col.fieldFqn)?.fieldName || ''}
                    </div>
                  )}
                </ReactSortable>
              );
            })}
          </div>
        );
      })}
    </div>
  );
};

export default UnionConfig;
