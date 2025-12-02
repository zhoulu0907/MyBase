import type { EntityNode, EdgeData } from '../../../../../utils/interface';
import { FIELD_TYPE } from '@onebase/ui-kit';

// 布局配置
interface LayoutConfig {
  nodeWidth: number;
  horizontalSpacing: number;
  verticalSpacing: number;
  startX: number;
  startY: number;
}

// 节点位置信息
interface NodePosition {
  id: string;
  x: number;
  y: number;
  level: number; // 层级，用于分层布局
  connections: number; // 连接数，用于排序
  isSource: boolean; // 是否为source节点
  isTarget: boolean; // 是否为target节点
  isIsolated: boolean; // 是否为孤立节点
}

// 节点高度计算常量（与主组件保持一致）
const LINE_HEAD_HEIGHT = 48;
const LINE_HEIGHT = 34.8;
const LINE_TITLE_HEIGHT = 44;

// 默认布局配置
const DEFAULT_CONFIG: LayoutConfig = {
  nodeWidth: 280,
  horizontalSpacing: 50,
  verticalSpacing: 50,
  startX: 100,
  startY: 100
};

/**
 * 自动布局算法
 * 1. 无关联关系的节点统一排列在第一排，资产顶端对齐、横向等间距分布
 * 2. 有关联关系的节点根据拓扑排序算法分层，有sourceid的节点放左边，有targetid的放右边
 */
export class AutoLayout {
  private config: LayoutConfig;
  private nodes: EntityNode[];
  private edges: EdgeData[];

  constructor(nodes: EntityNode[], edges: EdgeData[], config: Partial<LayoutConfig> = {}) {
    this.nodes = nodes;
    this.edges = edges;
    this.config = { ...DEFAULT_CONFIG, ...config };
  }

  /**
   * 执行自动布局
   * @returns 布局后的节点位置数组
   */
  public layout(): Array<{ id: string; x: number; y: number }> {
    if (this.nodes.length === 0) return [];

    console.log('开始执行新布局算法...');

    // 1. 分析节点类型和关系
    const nodeAnalysis = this.analyzeNodes();

    // 2. 分离孤立节点和关联节点
    const { isolatedNodes, connectedNodes } = this.separateNodes(nodeAnalysis);

    // 3. 计算所有节点的位置 - 孤立节点第一排，关联节点第二排
    const positions = this.calculateAllPositions(isolatedNodes, connectedNodes, nodeAnalysis);

    return positions;
  }

  /**
   * 计算节点高度
   */
  private calculateNodeHeight(node: EntityNode): number {
    const systemFields = node.fields.filter((field) => field.isSystemField === FIELD_TYPE.SYSTEM);
    const customFields = node.fields.filter((field) => field.isSystemField === FIELD_TYPE.CUSTOM);

    // 基础高度：头部 + 系统字段标题 + 自定义字段标题
    let totalHeight = LINE_HEAD_HEIGHT;

    if (systemFields.length > 0) {
      totalHeight += LINE_TITLE_HEIGHT;
    }
    if (customFields.length > 0) {
      totalHeight += LINE_TITLE_HEIGHT;
    }

    // 字段行高度
    totalHeight += customFields.length * LINE_HEIGHT;
    return totalHeight;
  }

  /**
   * 计算第一排节点的最大高度
   */
  private calculateMaxNodeHeight(nodes: EntityNode[]): number {
    if (nodes.length === 0) return 200; // 默认高度

    return Math.max(...nodes.map((node) => this.calculateNodeHeight(node)));
  }

  /**
   * 分析节点类型和关系
   */
  private analyzeNodes(): Map<string, NodePosition> {
    const analysis = new Map<string, NodePosition>();

    // 初始化所有节点
    this.nodes.forEach((node) => {
      analysis.set(node.entityId, {
        id: node.entityId,
        x: 0,
        y: 0,
        level: 0,
        connections: 0,
        isSource: false,
        isTarget: false,
        isIsolated: true
      });
    });

    // 分析边的方向
    this.edges.forEach((edge) => {
      const sourceNode = analysis.get(edge.sourceEntityId);
      const targetNode = analysis.get(edge.targetEntityId);

      if (sourceNode) {
        sourceNode.isSource = true;
        sourceNode.isIsolated = false;
        sourceNode.connections++;
      }
      if (targetNode) {
        targetNode.isTarget = true;
        targetNode.isIsolated = false;
        targetNode.connections++;
      }
    });

    return analysis;
  }

  /**
   * 分离孤立节点和关联节点
   */
  private separateNodes(nodeAnalysis: Map<string, NodePosition>): {
    isolatedNodes: string[];
    connectedNodes: string[];
  } {
    const isolatedNodes: string[] = [];
    const connectedNodes: string[] = [];

    nodeAnalysis.forEach((node, id) => {
      if (node.isIsolated) {
        isolatedNodes.push(id);
      } else {
        connectedNodes.push(id);
      }
    });

    return { isolatedNodes, connectedNodes };
  }

  /**
   * 计算所有节点的位置
   */
  private calculateAllPositions(
    isolatedNodes: string[],
    connectedNodes: string[],
    nodeAnalysis: Map<string, NodePosition>
  ): Array<{ id: string; x: number; y: number }> {
    const positions: Array<{ id: string; x: number; y: number }> = [];
    const { nodeWidth, horizontalSpacing, verticalSpacing, startX, startY } = this.config;

    // 计算第一排节点的最大高度
    const firstRowNodes = this.nodes.filter((node) => isolatedNodes.includes(node.entityId));
    const maxNodeHeight = this.calculateMaxNodeHeight(firstRowNodes);

    // 1. 处理孤立节点 - 第一排，横向等间距分布
    if (isolatedNodes.length > 0) {
      const isolatedY = startY;
      const totalWidth = isolatedNodes.length * nodeWidth + (isolatedNodes.length - 1) * horizontalSpacing;
      const startXForIsolated = startX + (800 - totalWidth) / 2; // 居中分布

      isolatedNodes.forEach((nodeId, index) => {
        const x = startXForIsolated + index * (nodeWidth + horizontalSpacing);
        positions.push({
          id: nodeId,
          x,
          y: isolatedY
        });
      });
    }

    // 2. 处理关联节点 - 全部放在第二排，按方向分组排列
    if (connectedNodes.length > 0) {
      const connectedY = startY + maxNodeHeight + verticalSpacing; // 第二排

      // 按节点类型分组
      const sourceNodes: string[] = [];
      const targetNodes: string[] = [];
      const mixedNodes: string[] = [];

      connectedNodes.forEach((nodeId) => {
        const analysis = nodeAnalysis.get(nodeId);
        if (analysis) {
          if (analysis.isSource && !analysis.isTarget) {
            sourceNodes.push(nodeId);
          } else if (analysis.isTarget && !analysis.isSource) {
            targetNodes.push(nodeId);
          } else {
            mixedNodes.push(nodeId);
          }
        }
      });

      // 合并所有关联节点：source -> mixed -> target
      const allConnectedNodes = [...sourceNodes, ...mixedNodes, ...targetNodes];

      // 计算总宽度并居中分布
      const totalWidth = allConnectedNodes.length * nodeWidth + (allConnectedNodes.length - 1) * horizontalSpacing;
      const startXForConnected = startX + (800 - totalWidth) / 2; // 居中分布

      // 按从左到右的顺序排列
      allConnectedNodes.forEach((nodeId, index) => {
        const x = startXForConnected + index * (nodeWidth + horizontalSpacing);
        positions.push({
          id: nodeId,
          x,
          y: connectedY
        });
      });
    }

    return positions;
  }

  /**
   * 优化布局
   */
  public optimizeLayout(
    positions: Array<{ id: string; x: number; y: number }>
  ): Array<{ id: string; x: number; y: number }> {
    // 目前直接返回
    return positions;
  }
}

/**
 * 执行自动布局的便捷函数
 */
export function performAutoLayout(
  nodes: EntityNode[],
  edges: EdgeData[],
  config?: Partial<LayoutConfig>
): Array<{ id: string; x: number; y: number }> {
  const layout = new AutoLayout(nodes, edges, config);
  const positions = layout.layout();
  return layout.optimizeLayout(positions);
}
