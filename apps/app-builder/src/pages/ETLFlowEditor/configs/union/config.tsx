import { IconPlusCircleFill } from '@arco-design/web-react/icon';
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import styles from './index.module.less';

/**
 * Union 节点的配置主界面
 * 初始化页面，渲染 UnionNodeConfig 组件
 */
const UnionConfig: React.FC = () => {
  const [data, setData] = useState([
    {
      tableId: '签约表-北京',
      columns: [
        { id: 1, name: '签约日期' },
        { id: 2, name: '合同总价' },
        { id: 3, name: '销售单价' },
        { id: 4, name: '' },
        { id: 5, name: '' },
        { id: 6, name: '' },
        { id: 7, name: '' },
        { id: 8, name: '' },
        { id: 9, name: '' }
      ]
    },
    {
      tableId: '签约表-上海',
      columns: [
        { id: 1, name: '' },
        { id: 2, name: '' },
        { id: 3, name: '' },
        { id: 4, name: '签约日期' },
        { id: 5, name: '合同总价' },
        { id: 6, name: '销售单价' },
        { id: 7, name: '' },
        { id: 8, name: '' },
        { id: 9, name: '' }
      ]
    },
    {
      tableId: '签约表-广州',
      columns: [
        { id: 1, name: '' },
        { id: 2, name: '' },
        { id: 3, name: '' },
        { id: 4, name: '' },
        { id: 5, name: '' },
        { id: 6, name: '' },
        { id: 7, name: '签约日期' },
        { id: 8, name: '合同总价(人民币)' },
        { id: 9, name: '销售单价' }
      ]
    }
  ]);

  useEffect(() => {
    console.log(data);
  }, [data]);

  // 计算所有 columns 字段的去重并集
  const [colTitles, setColTitles] = useState(
    data
      .flatMap((item) => item.columns)
      .filter((col) => col.name && col.name.trim() !== '')
      .map((col) => ({
        id: col.id,
        name: col.name
      }))
  );

  return (
    <div className={styles.dataConfig}>
      <div className={styles.rowHeader}>
        <div className={styles.rowTitle}>合并结果</div>
        {colTitles.map((col: any, index) => (
          <div key={col.id} className={styles.colTitle}>
            {col.name}
          </div>
        ))}
      </div>

      {data.map((row, rowIndex) => {
        return (
          <div key={row.tableId} className={styles.row}>
            <div className={styles.rowTitle}>
              <div>{row.tableId}</div>
              <IconPlusCircleFill style={{ fontSize: 16, color: 'rgba(var(--primary-6))' }} onClick={() => {}} />
            </div>

            {colTitles.map((col: any) => {
              return (
                <ReactSortable
                  key={`${row.tableId}-${col.id}`}
                  className={styles.colTitle}
                  swap // enables swap
                  list={row.columns.filter((c: any) => c.id === col.id)}
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
                    // console.log('row: ', rowIndex, 'col: ', col.id, ' setList: ', newRow);

                    if (newRow.length > 1) {
                      if (newRow[0].name != '' && newRow[1].name != '') {
                        return;
                      }

                      let targetId = 0;
                      let targetName = '';
                      let sourceId = 0;
                      for (const item of newRow) {
                        if (item.name != '') {
                          sourceId = item.id;
                          targetName = item.name;
                        }
                        if (item.name == '') {
                          targetId = item.id;
                        }
                      }

                      const newData = data.map((item) => {
                        if (item.tableId == row.tableId) {
                          return {
                            ...item,
                            columns: item.columns.map((c: any) =>
                              c.id === targetId
                                ? { ...c, name: targetName }
                                : c.id === sourceId
                                  ? { ...c, name: '' }
                                  : c
                            )
                          };
                        }
                        return item;
                      });

                      setData(newData);
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
                  {row.columns.find((c: any) => c.id === col.id)?.name && (
                    <div
                      key={`row-${rowIndex}-col-${col.id}`}
                      className={styles.colItem}
                      data-row-id={row.tableId}
                      data-col-id={col.id}
                    >
                      {row.columns.find((c: any) => c.id === col.id)?.name || ''}
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
