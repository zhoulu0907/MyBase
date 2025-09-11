// utils/purePositionCalculator.ts

interface GridConfig {
  /** 网格起始 X 坐标 */
  startX?: number;
  /** 网格起始 Y 坐标 */
  startY?: number;
  /** 网格列数 */
  columns?: number;
  /** 节点宽度 */
  nodeWidth?: number;
  /** 节点高度 */
  nodeHeight?: number;
  /** 水平间距 */
  horizontalSpacing?: number;
  /** 垂直间距 */
  verticalSpacing?: number;
}

export const DEFAULT_GRID_CONFIG: Required<GridConfig> = {
  startX: 100,
  startY: 100,
  columns: 3,
  nodeWidth: 280,
  nodeHeight: 350,
  horizontalSpacing: 50,
  verticalSpacing: 50
};

interface Position {
  x: number;
  y: number;
}

// 缓存已使用的位置
const usedPositionsCache = new Set<string>();

export class PurePositionCalculator {
  private config: Required<GridConfig>;
  private usedPositions: Set<string>;

  constructor(config: GridConfig = {}) {
    this.config = {
      startX: config.startX ?? DEFAULT_GRID_CONFIG.startX,
      startY: config.startY ?? DEFAULT_GRID_CONFIG.startY,
      columns: config.columns ?? DEFAULT_GRID_CONFIG.columns,
      nodeWidth: config.nodeWidth ?? DEFAULT_GRID_CONFIG.nodeWidth,
      nodeHeight: config.nodeHeight ?? DEFAULT_GRID_CONFIG.nodeHeight,
      horizontalSpacing: config.horizontalSpacing ?? DEFAULT_GRID_CONFIG.horizontalSpacing,
      verticalSpacing: config.verticalSpacing ?? DEFAULT_GRID_CONFIG.verticalSpacing
    };

    this.usedPositions = usedPositionsCache;
  }

  /**
   * 查找下一个可用的网格位置
   */
  private findNextAvailableGridPosition(): { row: number; col: number } {
    let row = 0;
    let col = 0;

    while (this.usedPositions.has(`${row}_${col}`)) {
      col++;
      if (col >= this.config.columns) {
        col = 0;
        row++;
      }
    }

    return { row, col };
  }

  /**
   * 将网格坐标转换为实际坐标
   */
  private gridToPosition(row: number, col: number): Position {
    return {
      x: this.config.startX + col * (this.config.nodeWidth + this.config.horizontalSpacing),
      y: this.config.startY + row * (this.config.nodeHeight + this.config.verticalSpacing)
    };
  }

  /**
   * 获取新增节点的位置（核心方法）
   */
  getNewNodePosition(): Position {
    const { row, col } = this.findNextAvailableGridPosition();
    const position = this.gridToPosition(row, col);

    // 标记为已使用
    this.usedPositions.add(`${row}_${col}`);

    return position;
  }

  /**
   * 释放位置（当节点创建失败或被删除时调用）
   */
  releasePosition(x: number, y: number) {
    const gridX = Math.round((x - this.config.startX) / (this.config.nodeWidth + this.config.horizontalSpacing));
    const gridY = Math.round((y - this.config.startY) / (this.config.nodeHeight + this.config.verticalSpacing));

    if (gridX >= 0 && gridY >= 0 && gridX < this.config.columns) {
      this.usedPositions.delete(`${gridY}_${gridX}`);
    }
  }

  /**
   * 重置所有已使用位置（可选：页面刷新或重置时调用）
   */
  reset() {
    this.usedPositions.clear();
  }

  /**
   * 手动标记某个位置为已使用（用于初始化时同步已有节点位置）
   */
  markPositionAsUsed(x: number, y: number) {
    const gridX = Math.round((x - this.config.startX) / (this.config.nodeWidth + this.config.horizontalSpacing));
    const gridY = Math.round((y - this.config.startY) / (this.config.nodeHeight + this.config.verticalSpacing));

    if (gridX >= 0 && gridY >= 0 && gridX < this.config.columns) {
      this.usedPositions.add(`${gridY}_${gridX}`);
    }
  }
}

/**
 * 快捷函数：获取新增节点位置
 */
let globalCalculator: PurePositionCalculator | null = null;

export const getNewNodePosition = (config: GridConfig = {}): Position => {
  if (!globalCalculator) {
    globalCalculator = new PurePositionCalculator(config);
  }
  return globalCalculator.getNewNodePosition();
};

/**
 * 重置位置计算器（可选）
 */
export const resetPositionCalculator = () => {
  if (globalCalculator) {
    globalCalculator.reset();
  }
  globalCalculator = null;
};

/**
 * 手动标记位置为已使用（用于同步已有节点）
 */
export const markPositionAsUsed = (x: number, y: number, config: GridConfig = {}) => {
  if (!globalCalculator) {
    globalCalculator = new PurePositionCalculator(config);
  }
  globalCalculator.markPositionAsUsed(x, y);
};
