import { useEffect, useState, useRef, useMemo } from 'react';
import { nanoid } from 'nanoid';
import { ReactSortable } from 'react-sortablejs';
import { IconPlus } from '@arco-design/web-react/icon';
import { useSignals } from '@preact/signals-react/runtime';
import {
  flexRender,
  getCoreRowModel,
  useReactTable,
} from '@tanstack/react-table';
import { arrayMoveImmutable } from 'array-move';
import { Layout, Message, Table, Button, Popconfirm, Form, Grid } from '@arco-design/web-react';
import { getComponentConfig, getComponentWidth } from 'src/components/Materials/schema';
import EditRender from 'src/components/render/EditRender';
import { usePageEditorSignal } from 'src/hooks/useSignal';
import { COMPONENT_GROUP_NAME } from 'src/utils/const';
import { STATUS_OPTIONS, STATUS_VALUES, LAYOUT_VALUES, LAYOUT_OPTIONS } from '../../../constants';
import { ALL_COMPONENT_TYPES, FORM_COMPONENT_TYPES } from '../../../componentTypes';
import { getComponentSchema } from '../../../schema';
import { type XChildrenTableConfig } from './schema';
import './index.css';

const leftPanelWidth = 343;
const rightPanelWidth = 310;
const canvasPaddingWidth = 40 + 32 + 10;
const canvasMarginWidth = 10;
const componentMaxWidth = leftPanelWidth + rightPanelWidth + canvasPaddingWidth + canvasMarginWidth;

const XChildrenTable = (props: XChildrenTableConfig & { runtime?: boolean }) => {
  const { colCount, id, runtime = true, label, layout, tooltip, labelColSpan, status, verify } = props;

  useSignals();

  const {
    curComponentID,
    setComponents,
    setCurComponentID,
    clearCurComponentID,
    curComponentSchema,
    setCurComponentSchema,
    pageComponentSchemas,
    setPageComponentSchemas,
    delPageComponentSchemas,
    showDeleteButton,
    setShowDeleteButton,

    layoutSubComponents,
    setLayoutSubComponents
  } = usePageEditorSignal();

  const [columns, setColumns] = useState<any[]>([]);
  const [tableData, setTableData] = useState<any[]>([]);

  console.log({ pageComponentSchemas, layoutSubComponents }, 'columns')


  const [selectedColumnId, setSelectedColumnId] = useState<string | null>(null); // 选中的列
  const [columnPositions, setColumnPositions] = useState<number[]>([]);
  const [columnWidths, setColumnWidths] = useState<number[]>([]);
  const [draggedIndex, setDraggedIndex] = useState<number | null>(null);
  const [hoveredColumnId, setHoveredColumnId] = useState<string | null>(null);
  const [dropIndex, setDropIndex] = useState<number | null>(null);
  // 通过拖拽方向控制插入位置（内部用于计算，不直接读取显示）
  const [, setDragToRight] = useState<boolean | null>(null);
  const tableRef = useRef<HTMLTableElement>(null);
  const dragImageRef = useRef<HTMLDivElement | null>(null);

  const scrollRef = useRef<HTMLDivElement>(null);
  const [showRightStickyShadow, setShowRightStickyShadow] = useState(false);


  // 从 store 中获取当前组件的列数据，如果不存在则初始化为空数组
  const colComponents = layoutSubComponents[id] || Array.from({ length: colCount }, () => []);

  // 首末列锁定：禁止点击与拖拽
  const isLockedIndex = (index: number) => index === 0 || index === columns.length - 1;

  // 创建列配置
  const tableColumns = useMemo(() => {
    // 隐藏子字段label
    const childrenLabel = {
      display: false,
      text: '',
    };

    return columns.map(col => ({
      id: col.id,
      header: col.title,
      accessorKey: col.dataIndex,
      cell: (info: any) => {
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
              <Button size="small" type="text" disabled={!runtime} onClick={() => handleCopy(col)}>
                复制
              </Button>
              <Popconfirm
                title="确认删除吗?"
                disabled={tableData.length === 1 || !runtime}
                onOk={() => handleDelete(col.key)}
              >
                <Button size="small" type="text" status="danger" disabled={tableData.length === 1 || !runtime}>
                  删除
                </Button>
              </Popconfirm>
            </div>
          );
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
            <EditRender runtime={runtime} cpId={col.id} cpType={col.type} pageComponentSchema={pageComponentSchemas[col.id]} reset={{ label: childrenLabel }} />
          </div>
        );
      },
    }));
  }, [columns]);

  // table配置
  const table = useReactTable({
    data: tableData,
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

    setSelectedColumnId(cpID === selectedColumnId ? null : cpID);

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
  const handleDragStart = (e: React.DragEvent, index: number) => {
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
        console.warn(newArr, newColumns, '1232131', columns[draggedIndex].id)
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

    // 插入符应该显示在目标位置的左边界
    // dropIndex 就是 arrayMoveImmutable 的目标位置
    return columnPositions[dropIndex];
  })();

  useEffect(() => {
    if (colComponents[0].length === 0) return;
    const index = {
      id: 'index',
      title: '序号',
      dataIndex: 'index',
      align: 'center',
      width: 65,
      render: (_: any, __: any, rowIndex: number) => rowIndex + 1,
    };
    const operation = {
      id: 'operation',
      title: '操作',
      dataIndex: 'operation',
      align: 'center',
      fixed: 'right',
      width: 150,
      render: (_: any, record: any) => (
        <div style={{ display: 'flex', gap: '8px', justifyContent: 'center' }}>
          <Button size="small" type="text" onClick={() => handleCopy(record)}>
            复制
          </Button>
          <Popconfirm
            title="确认删除吗?"
            disabled={tableData.length === 1}
            onOk={() => handleDelete(record.key)}
          >
            <Button size="small" type="text" status="danger">
              删除
            </Button>
          </Popconfirm>
        </div>
      ),
    }

    const label = {
      display: false,
      text: '',
    };

    const copyData = [...colComponents[0]];

    const customData = copyData.map(comp => ({
      ...comp,
      id: comp.id,
      title: comp.displayName,
      dataIndex: comp.id,
      align: 'center',
      width: 200,
      render: (_: any, _record: any) => (
        <div key={comp.id}>
          <EditRender runtime={runtime} cpId={comp.id} cpType={comp.type} pageComponentSchema={pageComponentSchemas[comp.id]} reset={{ label }} />
        </div>
      )
    }));

    const newColumns = [index, ...customData, operation];
    setColumns(newColumns);
  }, [colComponents[0]]);

  useEffect(() => {
    if (tableData.length === 0) {
      colComponents[0].forEach(comp => {
        const defaultData = [{ [comp.id]: '', key: nanoid() }];
        setTableData(defaultData);
      })
    }
    if (colComponents[0].length === 0) {
      // setTableData([]);
    }
  }, [tableData, columns]);

  // 如果列数变了，就重新初始化列
  useEffect(() => {
    const currentColumns = layoutSubComponents[id];
    if (!currentColumns || currentColumns.length !== colCount) {
      // console.log('id', id, 'colCount', colCount);
      const newColumns = Array.from({ length: colCount }, () => []);
      setLayoutSubComponents(id, newColumns);
    }
  }, [colCount, id, colComponents]);

  // const handleDeleteComponent = (componentId: string) => {
  //   // 从组件列表中移除
  //   // 遍历二维数组的每一列，过滤掉 id 匹配的组件
  //   const updatedColumns = colComponents.map((col) => col.filter((cp) => cp.id !== componentId));
  //   setLayoutSubComponents(id, updatedColumns);

  //   // 如果删除的是当前选中的组件，清除选中状态
  //   if (curComponentID === componentId) {
  //     delPageComponentSchemas(componentId);
  //     clearCurComponentID();
  //   }
  // };

  // 复制
  const handleCopy = (record: any) => {
    Message.success('复制成功');
    setTableData(prev => [...prev, { ...record, key: nanoid() }]);
  };

  // 删除
  const handleDelete = (key: string) => {
    setTableData(prev => prev.filter(item => item.key !== key));
    Message.success('删除成功');
  };

  // 新增
  const handleAdd = () => {
    setTableData(prev => [...prev, { ...tableData[0], key: nanoid() }]);
  };

  return (
    <Layout className="XChildrenTable" onClick={() => setSelectedColumnId(null)}>
      {colComponents.map((_colComponents, index) => (
        <Grid.Col key={index} className="item">
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
              // 允许拖入的组件
              const validata = ['XInputText', 'XInputTextArea', 'XInputNumber', 'XDatePicker', 'XRadio', 'XCheckbox', 'XSelectOne', 'XSelectMutiple', 'XImgUpload', 'XFileUpload', 'XUserSelect', 'XDeptSelect'];
              console.debug("onAdd", e.item.getAttribute('data-cp-type'));

              const cpID = e.item.id || e.item.getAttribute('data-cp-id');
              console.log(`拖入组件${id}内， 索引为${index}， 拖入组件为 ${cpID}`);
              const itemType = e.item.getAttribute('data-cp-type') || '';
              if (!validata.includes(itemType)) {
                return Message.warning('不支持的组件类型');
              }

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

              const containerType = FORM_COMPONENT_TYPES.CHILDREN_TABLE;

              const containerSchemaConfig = getComponentConfig(pageComponentSchemas[id], containerType);
              const containerSchema = getComponentSchema(containerType);

              containerSchema.config = containerSchemaConfig;
              containerSchema.config.cpName = '子表单';
              containerSchema.config.id = id;

              const containerProps = {
                id,
                type: containerType,
                ...containerSchema
              };

              setCurComponentID(id);
              setCurComponentSchema(containerProps);

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
          // onStart={(e) => {
          //   console.log('onStart', e);
          //   const cpID = e.item.getAttribute('data-id') || '';
          //   setCurComponentID(cpID);
          //   const curComponentSchema = pageComponentSchemas[cpID] || {};
          //   setCurComponentSchema(curComponentSchema);
          //   setShowDeleteButton(true);
          // }}
          >
            <Form.Item
              label={label.display && label.text}
              // field={dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.CHILDREN_TABLE}_${nanoid()}`}
              layout={layout}
              tooltip={tooltip}
              labelCol={{
                style: { width: labelColSpan, flex: 'unset' }
              }}
              wrapperCol={{ style: { flex: 1 } }}
              rules={[{ required: verify?.required }]}
              hidden={runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
              style={{
                margin: 0,
                display: 'flex',
                maxWidth: runtime ? '100%' : `calc(100vw - ${componentMaxWidth}px)`,
                opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
              }}
            >
              <div style={{
                width: '100%',
                minHeight: 130,
                // pointerEvents: runtime ? 'unset' : 'none'
              }}>
                {
                  runtime ? <Table columns={columns} data={tableData} scroll={{ x: 'max-content' }} /> :
                    (
                      <div ref={scrollRef} onScroll={updateRightStickyShadow} style={{ borderTop: '1px solid #e8e8e8', borderLeft: '1px solid #e8e8e8', borderRadius: '6px', position: 'relative', overflowX: 'auto' }}>
                        <table ref={tableRef} style={{ width: '100%', borderCollapse: 'collapse' }}>
                          <thead>
                            <tr>
                              {columns.map((column, index) => {
                                const isSelected = selectedColumnId === column.id;
                                const isDragging = draggedIndex === index;
                                const isLocked = isLockedIndex(index);
                                const draggableAllowed = !isLocked;

                                return (
                                  <th
                                    key={column.id}
                                    draggable={draggableAllowed}
                                    onDragStart={draggableAllowed ? (e) => handleDragStart(e, index) : undefined}
                                    onDragOver={draggableAllowed ? (e) => handleDragOver(e, index) : undefined}
                                    onDragLeave={draggableAllowed ? handleDragLeave : undefined}
                                    onDrop={draggableAllowed ? (e) => handleDrop(e) : undefined}
                                    onDragEnd={draggableAllowed ? handleDragEnd : undefined}
                                    onMouseEnter={() => (!isLocked ? setHoveredColumnId(column.id) : null)}
                                    onMouseLeave={() => setHoveredColumnId(null)}
                                    style={{
                                      padding: '12px',
                                      textAlign: 'center',
                                      borderBottom: '1px solid #e8e8e8',
                                      borderRight: '1px solid #e8e8e8',
                                      // background: isSelected ? '#E6F7FF' : '#fafafa',
                                      cursor: draggableAllowed ? 'move' : 'default',
                                      minWidth: '120px',
                                      position: index === columns.length - 1 ? 'sticky' : 'relative',
                                      right: index === columns.length - 1 ? 0 : undefined,
                                      userSelect: 'none',
                                      opacity: isDragging ? 0.3 : 1,
                                      transition: 'all 0.2s ease',
                                      transform: 'none',
                                      zIndex: isDragging ? 1000 : 1,
                                      boxShadow: isDragging
                                        ? '0 8px 24px rgba(0,0,0,0.15)'
                                        : (index === columns.length - 1 && showRightStickyShadow
                                          ? '-8px 0 12px -8px rgba(0,0,0,0.2)'
                                          : 'none'),
                                    }}
                                    onClick={draggableAllowed ? (e) => handleColumnClick(e, column) : undefined}
                                  >
                                    {column.title}
                                    {/* 操作列左侧阴影，跟随 sticky 列并定位在其左侧 */}
                                    {index === columns.length - 1 && showRightStickyShadow && (
                                      <div
                                        style={{
                                          position: 'absolute',
                                          top: 0,
                                          left: -18,
                                          width: 18,
                                          height: '100%',
                                          pointerEvents: 'none',
                                          background: 'linear-gradient(to right, rgba(0,0,0,0), rgba(0,0,0,0.12))',
                                        }}
                                      />
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
                                  const isSelected = selectedColumnId === cell.column.id;
                                  const columnIndex = columns.findIndex(col => col.id === cell.column.id);
                                  const isDragging = draggedIndex === columnIndex;
                                  const isLocked = isLockedIndex(columnIndex);
                                  const draggableAllowed = !isLocked;

                                  // console.log(row, cell, 888)

                                  return (
                                    <td
                                      key={cell.id}
                                      draggable={draggableAllowed}
                                      onDragStart={draggableAllowed ? (e) => handleDragStart(e, columnIndex) : undefined}
                                      onDragOver={draggableAllowed ? (e) => handleDragOver(e, columnIndex) : undefined}
                                      onDragLeave={draggableAllowed ? handleDragLeave : undefined}
                                      onDrop={draggableAllowed ? (e) => handleDrop(e) : undefined}
                                      onDragEnd={draggableAllowed ? handleDragEnd : undefined}
                                      onMouseEnter={() => (!isLocked ? setHoveredColumnId(cell.column.id) : null)}
                                      onMouseLeave={() => setHoveredColumnId(null)}
                                      style={{
                                        padding: '0',
                                        minWidth: 200,
                                        borderBottom: '1px solid #e8e8e8',
                                        borderRight: '1px solid #e8e8e8',
                                        // background: isSelected ? '#f0f9ff' : '#ffffff',
                                        cursor: draggableAllowed ? 'move' : 'default',
                                        position: columnIndex === columns.length - 1 ? 'sticky' : 'relative',
                                        right: columnIndex === columns.length - 1 ? 0 : undefined,
                                        userSelect: 'none',
                                        opacity: isDragging ? 0.3 : 1,
                                        transition: 'all 0.2s ease',
                                        transform: 'none',
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
                                        <div
                                          style={{
                                            position: 'absolute',
                                            top: 0,
                                            left: -18,
                                            width: 18,
                                            height: '100%',
                                            pointerEvents: 'none',
                                            background: 'linear-gradient(to right, rgba(0,0,0,0), rgba(0,0,0,0.12))',
                                          }}
                                        />
                                      )}
                                    </td>
                                  );
                                })}
                              </tr>
                            ))}
                          </tbody>
                        </table>

                        {/* 选中列边框覆盖层 */}
                        {selectedColumnInfo && (
                          <div
                            style={{
                              position: 'absolute',
                              top: 0,
                              left: `${selectedColumnInfo.left}px`,
                              width: `${selectedColumnInfo.width}px`,
                              height: 'calc(100% - 2px)',
                              border: '1px solid #009E9E',
                              pointerEvents: 'none',
                              zIndex: 10,
                            }}
                          />
                        )}

                        {/* 悬停列虚线边框覆盖层（有选中列时，仍允许其他列显示 hover）*/}
                        {hoveredColumnInfo && hoveredColumnId !== selectedColumnId && (
                          <div
                            style={{
                              position: 'absolute',
                              top: 0,
                              left: `${hoveredColumnInfo.left}px`,
                              width: `${hoveredColumnInfo.width}px`,
                              height: 'calc(100% - 2px)',
                              border: '1px dashed #009E9E',
                              pointerEvents: 'none',
                              zIndex: 5,
                            }}
                          />
                        )}

                        {/* 拖拽插入位置竖线（高度为整列高度）*/}
                        {dropIndicatorLeft !== null && draggedIndex !== null && (
                          <div
                            style={{
                              position: 'absolute',
                              top: 0,
                              left: `${dropIndicatorLeft}px`,
                              width: '2px',
                              height: '100%',
                              background: '#009E9E',
                              zIndex: 15,
                            }}
                          />
                        )}
                      </div>
                    )
                }
              </div>
            </Form.Item>
            <Button type='outline' icon={<IconPlus />} style={{ pointerEvents: runtime ? 'unset' : 'none' }} onClick={handleAdd}>新增一项</Button>

          </ReactSortable>
        </Grid.Col>
      ))}
    </Layout>
  );
};

export default XChildrenTable;