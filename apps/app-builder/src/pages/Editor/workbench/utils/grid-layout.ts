import { getWorkbenchComponentWidth, type GridItem, type WorkbenchComponentType } from '@onebase/ui-kit';
import { WB_GRID_CONFIG } from './constants';

/**
 * 将百分比宽度换算为 grid 列数（round 吸附到最近列）
 */
export function percentageToColSpan(widthStr: string): number {
  const percentage = Number.parseFloat(String(widthStr).replace('%', '')) || 100;
  const raw = Math.round((percentage / 100) * WB_GRID_CONFIG.columns);
  return Math.min(Math.max(raw, WB_GRID_CONFIG.minCols), WB_GRID_CONFIG.columns);
}

/**
 * 将 grid 列数换算为相对容器的百分比字符串
 */
export function colSpanToPercentage(colSpan: number, containerWidth: number): string {
  const colWidthPx = calcColWidthPx(containerWidth);
  const snappedPixels = colSpan * colWidthPx + (colSpan - 1) * WB_GRID_CONFIG.gap;
  return `${((snappedPixels / containerWidth) * 100).toFixed(4)}%`;
}

/**
 * 计算每列的实际像素宽度（含 gap 均摊）
 * 公式：colWidth = (containerWidth - (columns - 1) * gap) / columns
 */
export function calcColWidthPx(containerWidth: number): number {
  return (containerWidth - (WB_GRID_CONFIG.columns - 1) * WB_GRID_CONFIG.gap) / WB_GRID_CONFIG.columns;
}

/**
 * 根据拖动像素宽度吸附到最近列，返回列数
 */
export function snapToCol(draggingWidth: number, containerWidth: number): number {
  const colWidthPx = calcColWidthPx(containerWidth);
  const rawCols = (draggingWidth + WB_GRID_CONFIG.gap) / (colWidthPx + WB_GRID_CONFIG.gap);
  return Math.min(Math.max(Math.round(rawCols), WB_GRID_CONFIG.minCols), WB_GRID_CONFIG.columns);
}

/**
 * 根据列数计算对应的精确像素宽度
 */
export function colSpanToPixels(colSpan: number, containerWidth: number): number {
  const colWidthPx = calcColWidthPx(containerWidth);
  return colSpan * colWidthPx + (colSpan - 1) * WB_GRID_CONFIG.gap;
}

/**
 * 根据内容高度计算 rowSpan
 */
export function calcRowSpan(contentHeight: number): number {
  return Math.max(1, Math.ceil((contentHeight + WB_GRID_CONFIG.gap) / (WB_GRID_CONFIG.rowHeight + WB_GRID_CONFIG.gap)));
}

/**
 * 贪心算法：对 GridItem 列表应用 grid 布局，计算每个组件的 row/column/rowSpan/colSpan。
 * 使用「列高度占位图」找到每个组件能放入的最小起始行。
 *
 * @param items - 待布局的组件列表
 * @param getSchema - 根据组件 id 获取 schema 的函数
 * @param rowSpanOverrides - 临时覆盖某组件的 rowSpan（用于高度变化时绕过 signal 异步延迟）
 */
export function applyGridLayout(
  items: GridItem[],
  getSchema: (id: string) => any,
  rowSpanOverrides?: Record<string, number>
): GridItem[] {
  if (!items || items.length === 0) return items;

  const { columns } = WB_GRID_CONFIG;
  const columnHeights: number[] = Array(columns).fill(1);

  return items.map((cp) => {
    const schema = getSchema(cp.id);
    const widthStr = getWorkbenchComponentWidth(schema, cp.type as WorkbenchComponentType);
    const colSpan = percentageToColSpan(widthStr);
    const rowSpan = rowSpanOverrides?.[cp.id] ?? schema?.config?.gridLayout?.rowSpan ?? 1;

    let bestColumn = 1;
    let bestRow = Number.MAX_SAFE_INTEGER;

    for (let startCol = 1; startCol <= columns - colSpan + 1; startCol++) {
      let candidateRow = 1;
      for (let c = startCol; c < startCol + colSpan; c++) {
        candidateRow = Math.max(candidateRow, columnHeights[c - 1]);
      }
      if (candidateRow < bestRow) {
        bestRow = candidateRow;
        bestColumn = startCol;
      }
    }

    for (let c = bestColumn; c < bestColumn + colSpan; c++) {
      columnHeights[c - 1] = bestRow + rowSpan;
    }

    return { ...cp, layout: { row: bestRow, column: bestColumn, rowSpan, colSpan } } as GridItem;
  });
}
