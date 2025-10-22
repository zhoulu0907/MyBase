import { useEffect, useState, useRef } from 'react';
import { useSignals } from '@preact/signals-react/runtime';
import {
  flexRender,
  getCoreRowModel,
  useReactTable,
} from '@tanstack/react-table';
import { arrayMoveImmutable } from 'array-move';
import { Button, Popconfirm } from '@arco-design/web-react';
import { getComponentConfig } from 'src/components/Materials/schema';
import EditRender from 'src/components/render/EditRender';
import { usePageEditorSignal } from 'src/hooks/useSignal';
import { getComponentSchema } from '../../../schema';
import { TRadioDefaultType } from '@/components/Materials/types';
import { TStatusSelectKeyType } from '@/components/Materials/common';
import './index.css';

interface IProps {
  id: string;
  columns: any[]; // 列数据
  data: any; // table数据
  status?: TRadioDefaultType<TStatusSelectKeyType>;
  runtime?: boolean;
  setColumns: (data: any) => void;
}

const DragableTable = (props: IProps) => {
  const { id, columns = [], data = [], status, runtime = true, setColumns } = props;

  useSignals();

  const {
    curComponentID,
    setCurComponentID,
    setCurComponentSchema,
    pageComponentSchemas,
    setPageComponentSchemas,
    setLayoutSubComponents
  } = usePageEditorSignal();

  const [selectedColumnId, setSelectedColumnId] = useState<string | null>(null); // 选中的列
  const [columnPositions, setColumnPositions] = useState<number[]>([]);
  const [columnWidths, setColumnWidths] = useState<number[]>([]);
  const [draggedIndex, setDraggedIndex] = useState<number | null>(null);
  const [hoveredColumnId, setHoveredColumnId] = useState<string | null>(null);
  const [dropIndex, setDropIndex] = useState<number | null>(null);
  const [showRightStickyShadow, setShowRightStickyShadow] = useState(false);
  // 通过拖拽方向控制插入位置（内部用于计算，不直接读取显示）
  const [, setDragToRight] = useState<boolean | null>(null);

  const tableRef = useRef<HTMLTableElement>(null);
  const dragImageRef = useRef<HTMLDivElement | null>(null);
  const scrollRef = useRef<HTMLDivElement>(null);

  // 首末列锁定：禁止点击与拖拽
  const isLockedIndex = (index: number) => index === 0 || index === columns.length - 1;

  // 创建渲染单元格的函数
  const renderCell = (info: any, col: any) => {
    if (col.id === 'index') {
      const index = info.row.index + 1;
      return (
        <div
          style={{
            padding: '8px 12px',
            minHeight: '40px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          {index}
        </div>
      );
    }
    if (col.id === 'operation') {
      return (
        <div style={{ display: 'flex', gap: '8px', justifyContent: 'center' }}>
          <Button size="small" type="text" disabled={!runtime}>
            复制
          </Button>
          <Popconfirm
            title="确认删除吗?"
            disabled={data.length === 1 || !runtime}
          >
            <Button size="small" type="text" status="danger" disabled={data.length === 1 || !runtime}>
              删除
            </Button>
          </Popconfirm>
        </div>
      );
    }

    const schame = {
      ...pageComponentSchemas[col.id],
      config: {
        ...pageComponentSchemas[col.id].config,
        status,
      }
    }

    return (
      <div
        style={{
          padding: '8px 12px',
          minHeight: '40px',
          display: 'flex',
          alignItems: 'center',
        }}
      >
        <EditRender runtime={runtime} cpId={col.id} cpType={col.type} pageComponentSchema={schame} />
      </div>
    );
  };

  const tableColumns = columns.map(col => ({
    id: col.id,
    header: col.title,
    accessorKey: col.dataIndex,
    cell: (info: any) => renderCell(info, col)
  }));

  // table配置
  const table = useReactTable({
    data,
    columns: tableColumns,
    getCoreRowModel: getCoreRowModel(),
  });

  // 计算每列的实际位置
  useEffect(() => {
    if (tableRef.current) {
      const headerCells = tableRef.current.querySelectorAll('thead th');
      const positions: number[] = [];
      const widths: number[] = [];
      const containerRect = tableRef.current.getBoundingClientRect();
      headerCells.forEach(cell => {
        const el = cell as HTMLElement;
        const rect = el.getBoundingClientRect();
        // 可视区域内的相对 left，避免横向滚动导致的错位
        positions.push(rect.left - containerRect.left);
        // 使用 offsetWidth 获取真实列宽，避免部分遮挡时宽度变小
        widths.push(el.offsetWidth);
      });
      setColumnWidths(widths);
      setColumnPositions(positions);
    }
  }, [selectedColumnId, columns]);

  // 更新右侧 sticky 阴影显示
  const updateRightStickyShadow = () => {
    const el = scrollRef.current;
    if (!el) return;
    const hasOverflow = el.scrollWidth > el.clientWidth + 1;
    const atRight = el.scrollLeft + el.clientWidth >= el.scrollWidth - 1;
    setShowRightStickyShadow(hasOverflow && !atRight);
  };

  useEffect(() => {
    updateRightStickyShadow();
    const handler = () => updateRightStickyShadow();
    window.addEventListener('resize', handler);
    return () => window.removeEventListener('resize', handler);
  }, [columns]);

  // 横向滚动时需要重算列位置（left），以确保覆盖层宽度/位置不受可视遮挡影响
  useEffect(() => {
    const el = scrollRef.current;
    if (!el) return;
    const onScroll = () => {
      updateRightStickyShadow();
      // 重新测量列位置
      if (tableRef.current) {
        const headerCells = tableRef.current.querySelectorAll('thead th');
        const positions: number[] = [];
        const containerRect = tableRef.current.getBoundingClientRect();
        headerCells.forEach(cell => {
          const rect = (cell as HTMLElement).getBoundingClientRect();
          positions.push(rect.left - containerRect.left);
        });
        setColumnPositions(positions);
      }
    };
    el.addEventListener('scroll', onScroll);
    return () => el.removeEventListener('scroll', onScroll);
  }, []);

  // 点击表格列
  const handleColumnClick = (e: any, comp: any) => {
    e.stopPropagation();

    const { id: cpID, type: itemType, displayName } = comp;

    const schemaConfig = getComponentConfig(pageComponentSchemas[cpID], itemType);
    const schema = getComponentSchema(itemType as any);

    schema.config = schemaConfig;
    schema.config.cpName = displayName;
    schema.config.id = cpID;

    const props = {
      id: cpID,
      type: itemType,
      ...schema,
      config: {
        ...schema.config,
        label: {
          ...schema.config.label,
          display: false
        }
      },
    };

    setCurComponentID(cpID);
    setSelectedColumnId(cpID);
    setCurComponentSchema(props);
    setPageComponentSchemas(cpID, props);
  };

  // 获取列的位置和宽度
  const getColumnInfo = (columnId: string) => {
    const columnIndex = columns.findIndex(col => col.id === columnId);
    if (columnIndex < 0 || columnPositions.length === 0) return null;

    const left = Math.floor(columnPositions[columnIndex]);
    const widthFromArray = columnWidths[columnIndex];
    const width = Math.floor(widthFromArray ?? 120);

    return {
      left,
      width: Math.max(0, width - 1)
    };
  };

  // 拖拽处理函数
  const handleDragStart = (e: React.DragEvent, index: number, comp: any) => {
    // 若拖拽的不是当前选中列，则清除选中状态
    const draggedColumnId = columns[index]?.id;
    if (selectedColumnId && draggedColumnId !== selectedColumnId) {
      setSelectedColumnId(null);
    }
    setDraggedIndex(index);
    e.dataTransfer.effectAllowed = 'move';
    // 提供至少一种数据格式，避免部分浏览器回退到默认图标
    e.dataTransfer.setData('text/plain', ' ');
    e.dataTransfer.setData('text/html', ' ');

    const { id: cpID, type: itemType, displayName } = comp;
    const schemaConfig = getComponentConfig(pageComponentSchemas[cpID], itemType);
    const schema = getComponentSchema(itemType as any);

    schema.config = schemaConfig;
    schema.config.cpName = displayName;
    schema.config.id = cpID;

    const props = {
      id: cpID,
      type: itemType,
      ...schema,
      config: {
        ...schema.config,
        label: {
          ...schema.config.label,
          display: false
        }
      },
    };

    setCurComponentID(cpID);
    setCurComponentSchema(props);
    setPageComponentSchemas(cpID, props);

    // 自定义拖拽预览：使用一个仅包含列头文案的 div 作为 drag image
    if (tableRef.current && draggedColumnId) {
      const info = getColumnInfo(draggedColumnId);
      if (info) {
        const ghost = document.createElement('div');
        ghost.style.position = 'absolute';
        ghost.style.top = '-99999px';
        ghost.style.left = '-99999px';
        ghost.style.boxSizing = 'border-box';
        ghost.style.background = '#E6F7FF';
        ghost.style.border = '1px solid #009E9E';
        ghost.style.color = '#1677ff';
        ghost.style.fontWeight = '600';
        ghost.style.fontSize = '14px';
        ghost.style.padding = '6px 10px';
        ghost.style.boxShadow = '0 8px 24px rgba(0,0,0,0.15)';
        ghost.style.whiteSpace = 'nowrap';
        ghost.textContent = columns[index].title;
        document.body.appendChild(ghost);
        dragImageRef.current = ghost;
        try {
          e.dataTransfer.setDragImage(ghost, 10, 18);
        } catch (_) {
          // ignore
        }
      }
    }
  };

  const handleDragOver = (e: React.DragEvent, index: number) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
    if (draggedIndex === null) {
      setDropIndex(null);
      return;
    }
    // 根据拖拽方向（相对被拖拽列中线）确定插入在目标列左/右
    if (!tableRef.current) return;
    const targetColumnInfo = getColumnInfo(columns[index]?.id || '');
    if (!targetColumnInfo) return;
    const tableRect = tableRef.current.getBoundingClientRect();
    const mouseX = e.clientX - tableRect.left;
    const targetMidpoint = targetColumnInfo.left + targetColumnInfo.width / 2;
    const toRight = mouseX > targetMidpoint;
    setDragToRight(toRight);

    const locked = isLockedIndex(index);
    if (locked) {
      setDropIndex(null);
      return;
    }
    // 计算插入位置：根据鼠标在目标列的位置决定插入索引
    const minInsertIndex = 1; // 首列（序号列）后
    const maxInsertIndex = columns.length - 1; // 操作列索引（插入到其左边允许，右边不允许）

    // 直接使用目标列的索引作为插入位置
    let insertIndex = index;
    if (toRight) {
      // 鼠标在目标列右半边，插入到目标列右侧
      insertIndex = index + 1;
    }

    const clamped = Math.max(minInsertIndex, Math.min(maxInsertIndex, insertIndex));
    setDropIndex(clamped);
  };

  const handleDragLeave = () => {
    setDropIndex(null);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    if (draggedIndex !== null && dropIndex !== null) {
      // 仅当插入位置有效且不是自身左/右时才移动
      if (dropIndex !== draggedIndex && dropIndex !== draggedIndex + 1) {
        // 计算正确的目标位置
        let targetIndex = dropIndex;

        // 如果向右拖拽（dropIndex > draggedIndex），需要调整目标位置
        // 因为被拖拽的元素被移除后，后面的元素会向前移动一位
        if (dropIndex > draggedIndex) {
          targetIndex = dropIndex - 1;
        }

        const newColumns = arrayMoveImmutable(columns, draggedIndex, targetIndex);
        setColumns(newColumns);
        // 拖拽结束后选中被移动的列，显示实线边框
        setSelectedColumnId(columns[draggedIndex].id);

        const newArr = newColumns.slice(1, -1);  // 去掉头尾
        setLayoutSubComponents(id, [newArr]);
      }
    }
    setDraggedIndex(null);
    setDropIndex(null);
    setDragToRight(null);

    // 移除拖拽影像
    if (dragImageRef.current) {
      try {
        document.body.removeChild(dragImageRef.current);
      } catch (_) { }
      dragImageRef.current = null;
    }
  };

  const handleDragEnd = () => {
    setDraggedIndex(null);
    setDropIndex(null);
    setDragToRight(null);
    if (dragImageRef.current) {
      try {
        document.body.removeChild(dragImageRef.current);
      } catch (_) { }
      dragImageRef.current = null;
    }
  };

  // 获取选中列的位置和宽度
  const getSelectedColumnInfo = () => {
    if (!selectedColumnId || columnPositions.length === 0) return null;
    return getColumnInfo(selectedColumnId);
  };

  const selectedColumnInfo = getSelectedColumnInfo();
  const hoveredColumnInfo = getColumnInfo(hoveredColumnId || '');
  const dropIndicatorLeft = (() => {
    if (dropIndex === null || columnPositions[dropIndex] === undefined) return null;

    return columnPositions[dropIndex];
  })();

  return (
    <>
      {columns.length === 0 ? <div className='emptyTable'>请将组件拖到此区域（可拖入多个组件）</div> :
        <>
          <div className='tablewrapper' ref={scrollRef} onScroll={updateRightStickyShadow}>
            <table className='table' ref={tableRef}>
              <thead>
                <tr>
                  {columns.map((column: any, index: number) => {
                    const isDragging = draggedIndex === index;
                    const isLocked = isLockedIndex(index);
                    const draggableAllowed = !isLocked;

                    return (
                      <th
                        className='columnTitle'
                        key={column.id}
                        draggable={draggableAllowed}
                        onDragStart={draggableAllowed ? (e) => handleDragStart(e, index, column) : undefined}
                        onDragOver={draggableAllowed ? (e) => handleDragOver(e, index) : undefined}
                        onDragLeave={draggableAllowed ? handleDragLeave : undefined}
                        onDrop={draggableAllowed ? (e) => handleDrop(e) : undefined}
                        onDragEnd={draggableAllowed ? handleDragEnd : undefined}
                        onMouseEnter={() => (!isLocked ? setHoveredColumnId(column.id) : null)}
                        onMouseLeave={() => setHoveredColumnId(null)}
                        style={{
                          cursor: draggableAllowed ? 'move' : 'default',
                          position: index === columns.length - 1 ? 'sticky' : 'relative',
                          right: index === columns.length - 1 ? 0 : undefined,
                          opacity: isDragging ? 0.3 : 1,
                          zIndex: isDragging ? 1000 : 1,
                          boxShadow: isDragging
                            ? '0 8px 24px rgba(0,0,0,0.15)'
                            : (index === columns.length - 1 && showRightStickyShadow
                              ? '-8px 0 12px -8px rgba(0,0,0,0.2)'
                              : 'none'),
                        }}
                        onClick={draggableAllowed ? (e) => handleColumnClick(e, column) : undefined}
                      >
                        {/* {column.title} */}
                        {flexRender(table.getHeaderGroups()[0].headers[index].column.columnDef.header,
                          table.getHeaderGroups()[0].headers[index].getContext())}
                        {/* 操作列左侧阴影，跟随 sticky 列并定位在其左侧 */}
                        {index === columns.length - 1 && showRightStickyShadow && (
                          <div className='overflowShadow' />
                        )}
                      </th>
                    );
                  })}
                </tr>
              </thead>
              <tbody>
                {table.getRowModel().rows.map((row) => (
                  <tr key={row.id}>
                    {row.getVisibleCells().map((cell) => {
                      const columnIndex = columns.findIndex(col => col.id === cell.column.id);
                      const isDragging = draggedIndex === columnIndex;
                      const isLocked = isLockedIndex(columnIndex);
                      const draggableAllowed = !isLocked;

                      return (
                        <td
                          className='columnBody'
                          key={cell.id}
                          draggable={draggableAllowed}
                          onDragStart={draggableAllowed ? (e) => handleDragStart(e, columnIndex, columns.find(c => c.id === cell.column.id)) : undefined}
                          onDragOver={draggableAllowed ? (e) => handleDragOver(e, columnIndex) : undefined}
                          onDragLeave={draggableAllowed ? handleDragLeave : undefined}
                          onDrop={draggableAllowed ? (e) => handleDrop(e) : undefined}
                          onDragEnd={draggableAllowed ? handleDragEnd : undefined}
                          onMouseEnter={() => (!isLocked ? setHoveredColumnId(cell.column.id) : null)}
                          onMouseLeave={() => setHoveredColumnId(null)}
                          style={{
                            cursor: draggableAllowed ? 'move' : 'default',
                            position: columnIndex === columns.length - 1 ? 'sticky' : 'relative',
                            right: columnIndex === columns.length - 1 ? 0 : undefined,
                            opacity: isDragging ? 0.3 : 1,
                            zIndex: isDragging ? 1000 : 1,
                            boxShadow: isDragging
                              ? '0 8px 24px rgba(0,0,0,0.15)'
                              : (columnIndex === columns.length - 1 && showRightStickyShadow
                                ? '-8px 0 12px -8px rgba(0,0,0,0.2)'
                                : 'none'),
                          }}
                          onClick={draggableAllowed ? (e) => handleColumnClick(e, columns.find(c => c.id === cell.column.id)) : undefined}
                        >
                          {flexRender(cell.column.columnDef.cell, cell.getContext())}
                          {/* 操作列左侧阴影，跟随 sticky 列并定位在其左侧 */}
                          {columnIndex === columns.length - 1 && showRightStickyShadow && (
                            <div className='overflowShadow' />
                          )}
                        </td>
                      );
                    })}
                  </tr>
                ))}
              </tbody>
            </table>

            {/* 选中列边框覆盖层 */}
            {selectedColumnInfo && curComponentID === selectedColumnId && (
              <div className='hoverColumn'
                style={{
                  position: 'absolute',
                  top: 0,
                  height: 'calc(100% - 2px)',
                  border: '1px solid #009E9E',
                  pointerEvents: 'none',
                  left: `${selectedColumnInfo.left}px`,
                  width: `${selectedColumnInfo.width}px`,
                  zIndex: 10,
                }}
              />
            )}

            {/* 悬停列虚线边框覆盖层 */}
            {hoveredColumnInfo && hoveredColumnId !== selectedColumnId && (
              <div className='hoverColumn'
                style={{
                  position: 'absolute',
                  top: 0,
                  height: 'calc(100% - 2px)',
                  border: '1px dashed #009E9E',
                  pointerEvents: 'none',
                  left: `${hoveredColumnInfo.left}px`,
                  width: `${hoveredColumnInfo.width}px`,
                  zIndex: 5,
                }}
              />
            )}

            {/* 拖拽插入位置竖线 */}
            {dropIndicatorLeft !== null && draggedIndex !== null && (
              <div className='hoverColumn'
                style={{
                  position: 'absolute',
                  top: 0,
                  border: '1px solid #009E9E',
                  pointerEvents: 'none',
                  left: `${dropIndicatorLeft}px`,
                  width: '2px',
                  height: '100%',
                  background: '#009E9E',
                  zIndex: 15,
                }}
              />
            )}
          </div>
        </>
      }
    </>);
};

export default DragableTable;