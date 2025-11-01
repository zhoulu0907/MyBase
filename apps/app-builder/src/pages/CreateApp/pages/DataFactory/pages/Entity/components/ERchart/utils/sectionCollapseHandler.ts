import { Graph, Edge, Node } from '@antv/x6';
import { FIELD_TYPE } from '@onebase/ui-kit';
import type { EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';

const LINE_HEAD_HEIGHT = 48;
const LINE_TITLE_HEIGHT = 44;
const LINE_HEIGHT = 34.8;
const NODE_WIDTH = 280;

/**
 * 处理节点字段折叠逻辑
 */
export class SectionCollapseHandler {
  private graph: Graph;

  constructor(graph: Graph) {
    this.graph = graph;
  }

  /**
   * 更新连接桩位置
   * @param nodesData 所有节点数据
   * @param systemCollapsed 系统字段是否折叠（默认true）
   */
  updatePortsPosition(nodesData: EntityNode[], systemCollapsed: boolean = true) {
    nodesData.forEach((nodeData) => {
      const nodeId = nodeData.entityId;
      const systemFields = nodeData.fields.filter((f) => f.isSystemField === FIELD_TYPE.SYSTEM);
      const customFields = nodeData.fields.filter((f) => f.isSystemField === FIELD_TYPE.CUSTOM);

      // 更新自定义字段的连接桩位置（依赖系统字段的折叠状态）
      if (systemFields.length > 0 && customFields.length > 0) {
        this.updateCustomFieldsPorts(nodeId, systemCollapsed, systemFields, customFields);
      }
    });
  }

  /**
   * 批量初始化所有节点的折叠状态
   * @param nodesData 所有节点数据
   * @param systemCollapsed 系统字段是否折叠（默认true）
   * @param customCollapsed 自定义字段是否折叠（默认false）
   */
  batchInitializeCollapse(nodesData: EntityNode[], systemCollapsed: boolean = true, customCollapsed: boolean = false) {
    // 使用静默模式批量处理，避免触发多次重绘
    const edges = this.graph.getEdges();

    // 为每个节点处理折叠状态
    nodesData.forEach((nodeData) => {
      const nodeId = nodeData.entityId;

      if (nodeData.fields.some((f) => f.isSystemField === FIELD_TYPE.SYSTEM)) {
        this.applyCollapseState(nodeId, 'system', systemCollapsed, nodeData, edges);
      }

      if (nodeData.fields.some((f) => f.isSystemField === FIELD_TYPE.CUSTOM)) {
        this.applyCollapseState(nodeId, 'custom', customCollapsed, nodeData, edges);
      }
    });
  }

  /**
   * 应用折叠状态(内部方法)
   */
  private applyCollapseState(
    nodeId: string,
    section: 'system' | 'custom',
    isCollapsed: boolean,
    nodeData: EntityNode,
    edges: Edge[]
  ) {
    const aggregatePortId = `${nodeId}_${section}_fields`;

    const systemFields = nodeData.fields.filter((f) => f.isSystemField === FIELD_TYPE.SYSTEM);
    const customFields = nodeData.fields.filter((f) => f.isSystemField === FIELD_TYPE.CUSTOM);
    const fieldsInThisSection = section === 'system' ? systemFields : customFields;

    // 提取所有字段对应的portID
    const portIdsInSection = new Set(
      fieldsInThisSection.flatMap((field) => [
        `${field.fieldId || field.fieldName}_source`,
        `${field.fieldId || field.fieldName}_target`
      ])
    );

    // 处理边重连
    this.handleEdgeReconnection(edges, nodeId, aggregatePortId, portIdsInSection, isCollapsed, true);

    // 当系统字段折叠状态改变时，需要重新计算自定义字段的连接桩位置
    if (section === 'system') {
      this.updateCustomFieldsPorts(nodeId, isCollapsed, systemFields, customFields);
    }
  }

  /**
   * 处理字段折叠/展开
   * @param nodeId 节点ID
   * @param section 字段类型
   * @param isCollapsed 是否折叠
   * @param nodeData 节点数据
   */
  handleSectionCollapse(nodeId: string, section: 'system' | 'custom', isCollapsed: boolean, nodeData: EntityNode) {
    const edges = this.graph.getEdges();

    // 确定聚合portID
    const aggregatePortId = `${nodeId}_${section}_fields`;

    const systemFields = nodeData.fields.filter((f) => f.isSystemField === FIELD_TYPE.SYSTEM);
    const customFields = nodeData.fields.filter((f) => f.isSystemField === FIELD_TYPE.CUSTOM);
    const fieldsInThisSection = section === 'system' ? systemFields : customFields;

    // 提取所有字段对应的portID
    const portIdsInSection = new Set(
      fieldsInThisSection.flatMap((field) => [
        `${field.fieldId || field.fieldName}_source`,
        `${field.fieldId || field.fieldName}_target`
      ])
    );

    // 处理边重连
    this.handleEdgeReconnection(edges, nodeId, aggregatePortId, portIdsInSection, isCollapsed, false);

    // 当系统字段折叠状态改变时，需要重新计算自定义字段的连接桩位置
    if (section === 'system') {
      this.updateCustomFieldsPorts(nodeId, isCollapsed, systemFields, customFields);
    }
  }

  /**
   * 处理边重连逻辑
   * @param edges 所有边
   * @param nodeId 节点ID
   * @param aggregatePortId 聚合连接桩ID
   * @param portIdsInSection 该section下的所有连接桩ID
   * @param isCollapsed 是否折叠
   * @param silent 是否静默模式（不触发重绘）
   */
  private handleEdgeReconnection(
    edges: Edge[],
    nodeId: string,
    aggregatePortId: string,
    portIdsInSection: Set<string>,
    isCollapsed: boolean,
    silent: boolean = false
  ) {
    // 批量收集需要更新的边
    const edgesToUpdate: Array<{
      edge: Edge;
      source?: { cell: string; port: string };
      target?: { cell: string; port: string };
    }> = [];

    edges.forEach((edge) => {
      const data = edge.getData() as {
        originalSource?: { cell: string; port: string };
        originalTarget?: { cell: string; port: string };
        [key: string]: unknown;
      };
      const originalSource = data?.originalSource;
      const originalTarget = data?.originalTarget;

      if (!originalSource || !originalTarget) return;

      const currentSource = edge.getSource() as { cell: string; port?: string } | undefined;
      const currentTarget = edge.getTarget() as { cell: string; port?: string } | undefined;

      const updateInfo: {
        edge: Edge;
        source?: { cell: string; port: string };
        target?: { cell: string; port: string };
      } = { edge };

      // 处理 source 端
      if (currentSource && currentSource.cell === nodeId && typeof currentSource.port === 'string') {
        const isOriginalInSection =
          portIdsInSection.has(currentSource.port) ||
          (originalSource.port && portIdsInSection.has(originalSource.port));

        if (isOriginalInSection) {
          if (isCollapsed) {
            // 折叠 → 指向聚合 port
            updateInfo.source = { cell: nodeId, port: `${aggregatePortId}_source` };
          } else {
            // 展开 → 恢复原始 port
            updateInfo.source = originalSource;
          }
        }
      }

      // 处理 target 端
      if (currentTarget && currentTarget.cell === nodeId && typeof currentTarget.port === 'string') {
        const isOriginalInSection =
          portIdsInSection.has(currentTarget.port) ||
          (originalTarget.port && portIdsInSection.has(originalTarget.port));

        if (isOriginalInSection) {
          if (isCollapsed) {
            updateInfo.target = { cell: nodeId, port: `${aggregatePortId}_target` };
          } else {
            updateInfo.target = originalTarget;
          }
        }
      }

      // 如果有需要更新的连接桩，添加到批量更新列表
      if (updateInfo.source || updateInfo.target) {
        edgesToUpdate.push(updateInfo);
      }
    });

    // 批量更新边（在静默模式下使用批量操作）
    if (silent) {
      // 静默模式
      edgesToUpdate.forEach(({ edge, source, target }) => {
        if (source) {
          edge.setSource(source, { silent: true });
        }
        if (target) {
          edge.setTarget(target, { silent: true });
        }
      });
    } else {
      // 正常模式
      edgesToUpdate.forEach(({ edge, source, target }) => {
        if (source) {
          edge.setSource(source);
        }
        if (target) {
          edge.setTarget(target);
        }
      });
    }
  }

  /**
   * 更新自定义字段的连接桩位置
   */
  private updateCustomFieldsPorts(
    nodeId: string,
    systemCollapsed: boolean,
    systemFields: EntityNode['fields'],
    customFields: EntityNode['fields']
  ) {
    if (customFields.length === 0) return;

    // 计算新的自定义字段标题位置
    const systemTitleOffset =
      systemFields.length > 0
        ? systemCollapsed
          ? LINE_TITLE_HEIGHT
          : LINE_TITLE_HEIGHT + systemFields.length * LINE_HEIGHT
        : LINE_TITLE_HEIGHT;
    const customTitleY = LINE_HEAD_HEIGHT + systemTitleOffset + LINE_TITLE_HEIGHT / 2;

    // 更新自定义字段聚合连接桩位置
    const node = this.graph.getCellById(nodeId);
    if (node && node.isNode()) {
      const nodeInstance = node as Node;
      nodeInstance.setPortProp(`${nodeId}_custom_fields_source`, 'args', { x: NODE_WIDTH, y: customTitleY });
      nodeInstance.setPortProp(`${nodeId}_custom_fields_target`, 'args', { x: 0, y: customTitleY });

      customFields.forEach((field, index) => {
        const accumulatedHeight = index * LINE_HEIGHT;
        const finalY = customTitleY + accumulatedHeight + LINE_HEIGHT / 2 + LINE_TITLE_HEIGHT / 2;

        const sourcePortId = `${field.fieldId || field.fieldName}_source`;
        const targetPortId = `${field.fieldId || field.fieldName}_target`;

        nodeInstance.setPortProp(sourcePortId, 'args', { x: NODE_WIDTH, y: finalY });
        nodeInstance.setPortProp(targetPortId, 'args', { x: 0, y: finalY });
      });
    }
  }
}
