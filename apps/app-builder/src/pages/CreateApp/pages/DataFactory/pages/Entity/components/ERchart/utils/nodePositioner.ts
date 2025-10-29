// utils/nodePositioner.ts
import { Cell, Graph } from '@antv/x6';

interface GridPositionerOptions {
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

interface Position {
  x: number;
  y: number;
}

export class GridNodePositioner {
  private graph: Graph;
  private startX: number;
  private startY: number;
  private columns: number;
  private nodeWidth: number;
  private nodeHeight: number;
  private horizontalSpacing: number;
  private verticalSpacing: number;

  // 记录已被占用的网格位置
  private occupiedPositions = new Set<string>();

  // 记录下一个新节点应该放置的逻辑位置索引 (基于添加顺序)
  private nextLogicalIndex = 0;

  constructor(graph: Graph, options: GridPositionerOptions = {}) {
    this.graph = graph;
    this.startX = options.startX ?? 100;
    this.startY = options.startY ?? 100;
    this.columns = options.columns ?? 5;
    this.nodeWidth = options.nodeWidth ?? 120;
    this.nodeHeight = options.nodeHeight ?? 60;
    this.horizontalSpacing = options.horizontalSpacing ?? 20;
    this.verticalSpacing = options.verticalSpacing ?? 20;

    // 初始化时扫描已有节点位置
    this.scanExistingNodes();
  }

  /**
   * 扫描已有节点，标记已被占用的网格位置
   */
  private scanExistingNodes() {
    const nodes = this.graph.getNodes();
    let maxIndex = -1; // 找到已占用位置的最大索引

    nodes.forEach((node) => {
      const pos = node.getPosition();
      // 将实际坐标转换为网格坐标
      const gridX = Math.round((pos.x - this.startX) / (this.nodeWidth + this.horizontalSpacing));
      const gridY = Math.round((pos.y - this.startY) / (this.nodeHeight + this.verticalSpacing));

      // 如果在网格范围内，标记为已占用
      if (gridX >= 0 && gridY >= 0 && gridX < this.columns) {
        const index = gridY * this.columns + gridX;
        this.occupiedPositions.add(`${gridY}_${gridX}`);
        if (index > maxIndex) {
          maxIndex = index;
        }
      }
    });

    // 设置下一个逻辑索引为最大索引 + 1，确保新节点按顺序添加
    this.nextLogicalIndex = maxIndex + 1;
    console.log(
      `GridNodePositioner initialized. Occupied positions:`,
      Array.from(this.occupiedPositions),
      `Next logical index: ${this.nextLogicalIndex}`
    );
  }

  /**
   * 根据逻辑索引计算网格坐标
   */
  private indexToGrid(index: number): { row: number; col: number } {
    const row = Math.floor(index / this.columns);
    const col = index % this.columns;
    return { row, col };
  }

  /**
   * 将网格坐标转换为实际坐标
   */
  private gridToPosition(row: number, col: number): Position {
    return {
      x: this.startX + col * (this.nodeWidth + this.horizontalSpacing),
      y: this.startY + row * (this.nodeHeight + this.verticalSpacing)
    };
  }

  /**
   * 判断节点是否为新增节点
   */
  private isNewNode(positionX?: number, positionY?: number): boolean {
    return positionX === undefined || positionY === undefined;
  }

  /**
   * 查找下一个逻辑上可用的位置（跳过已被物理占用的位置）
   */
  private findNextLogicalAvailablePosition(): { row: number; col: number } {
    let index = this.nextLogicalIndex;
    let row, col;
    let attempts = 0;
    const maxAttempts = 1000;

    do {
      ({ row, col } = this.indexToGrid(index));
      index++;
      attempts++;
      if (attempts > maxAttempts) {
        console.warn('GridNodePositioner: Max attempts reached while finding next logical position.');
        break;
      }
    } while (this.occupiedPositions.has(`${row}_${col}`));

    this.nextLogicalIndex = index;

    return { row, col };
  }

  /**
   * 添加节点
   * @param nodeData 节点数据
   * @param options 节点其他属性
   */
  addNode(
    nodeData: {
      id?: string;
      data?: object;
      x?: number;
      y?: number;
      [key: string]: any;
    },
    options: Record<string, any> = {}
  ): Cell {
    let x: number;
    let y: number;

    if (this.isNewNode(nodeData.x, nodeData.y)) {
      // 新增节点 - 使用网格布局
      const { row, col } = this.findNextLogicalAvailablePosition();
      const position = this.gridToPosition(row, col);
      x = position.x;
      y = position.y;

      // 标记为已占用
      this.occupiedPositions.add(`${row}_${col}`);
    } else {
      // 已有节点 - 使用原有位置
      x = nodeData.x!;
      y = nodeData.y!;

      // 将坐标转换为网格坐标并标记为已占用
      const gridX = Math.round((x - this.startX) / (this.nodeWidth + this.horizontalSpacing));
      const gridY = Math.round((y - this.startY) / (this.nodeHeight + this.verticalSpacing));

      if (gridX >= 0 && gridY >= 0 && gridX < this.columns) {
        this.occupiedPositions.add(`${gridY}_${gridX}`);
      }
    }

    const node = this.graph.addNode({
      ...nodeData,
      x,
      y,
      options: { silent: true }
    });

    return node;
  }

  /**
   * 释放某个位置
   * @param x X 坐标
   * @param y Y 坐标
   */
  releasePosition(x: number, y: number) {
    const gridX = Math.round((x - this.startX) / (this.nodeWidth + this.horizontalSpacing));
    const gridY = Math.round((y - this.startY) / (this.nodeHeight + this.verticalSpacing));

    if (gridX >= 0 && gridY >= 0 && gridX < this.columns) {
      this.occupiedPositions.delete(`${gridY}_${gridX}`);
    } else {
      console.warn(`尝试释放的位置 (${x}, ${y}) 不在网格范围内或无效`);
    }
  }

  /**
   * 重置网格布局状态
   */
  reset() {
    this.occupiedPositions.clear();
    this.nextLogicalIndex = 0;
    this.scanExistingNodes();
    console.log('GridNodePositioner 已重置');
  }

  /**
   * 获取当前网格状态信息
   */
  getGridInfo() {
    return {
      nextLogicalIndex: this.nextLogicalIndex,
      occupiedCount: this.occupiedPositions.size
    };
  }
}
