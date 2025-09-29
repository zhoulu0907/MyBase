import { Graph } from '@antv/x6';
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
   * 处理字段折叠/展开
   * @param nodeId 节点ID
   * @param section 字段类型 ('system' | 'custom')
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
    this.handleEdgeReconnection(edges, nodeId, aggregatePortId, portIdsInSection, isCollapsed);

    // 当系统字段折叠状态改变时，需要重新计算自定义字段的端口位置
    if (section === 'system') {
      this.updateCustomFieldsPorts(nodeId, isCollapsed, systemFields, customFields);
    }
  }

  /**
   * 处理边重连逻辑
   */
  private handleEdgeReconnection(
    edges: any[],
    nodeId: string,
    aggregatePortId: string,
    portIdsInSection: Set<string>,
    isCollapsed: boolean
  ) {
    edges.forEach((edge) => {
      const data = edge.getData();
      const originalSource = data?.originalSource;
      const originalTarget = data?.originalTarget;

      if (!originalSource || !originalTarget) return;

      const currentSource = edge.getSource();
      const currentTarget = edge.getTarget();

      // 处理 source 端
      if (currentSource.cell === nodeId && typeof currentSource.port === 'string') {
        const isOriginalInSection =
          portIdsInSection.has(currentSource.port) ||
          (originalSource.port && portIdsInSection.has(originalSource.port));

        if (isOriginalInSection) {
          if (isCollapsed) {
            // 折叠 → 指向聚合 port
            edge.setSource({ cell: nodeId, port: `${aggregatePortId}_source` });
          } else {
            // 展开 → 恢复原始 port
            edge.setSource(originalSource);
          }
        }
      }

      // 处理 target 端
      if (currentTarget.cell === nodeId && typeof currentTarget.port === 'string') {
        const isOriginalInSection =
          portIdsInSection.has(currentTarget.port) ||
          (originalTarget.port && portIdsInSection.has(originalTarget.port));

        if (isOriginalInSection) {
          if (isCollapsed) {
            edge.setTarget({ cell: nodeId, port: `${aggregatePortId}_target` });
          } else {
            edge.setTarget(originalTarget);
          }
        }
      }
    });
  }

  /**
   * 更新自定义字段的端口位置
   */
  private updateCustomFieldsPorts(nodeId: string, systemCollapsed: boolean, systemFields: any[], customFields: any[]) {
    if (customFields.length === 0) return;

    // 计算新的自定义字段标题位置
    const systemTitleOffset =
      systemFields.length > 0
        ? systemCollapsed
          ? LINE_TITLE_HEIGHT
          : LINE_TITLE_HEIGHT + systemFields.length * LINE_HEIGHT
        : LINE_TITLE_HEIGHT;
    const customTitleY = LINE_HEAD_HEIGHT + systemTitleOffset + LINE_TITLE_HEIGHT / 2;

    // 更新自定义字段聚合端口位置
    const node = this.graph.getCellById(nodeId);
    if (node) {
      // TODO: 需要根据X6的实际API来更新端口位置
      // 目前暂时注释掉，因为API可能不同
      console.log('需要更新自定义字段端口位置:', {
        nodeId,
        customTitleY,
        customFields: customFields.length
      });

      // 更新自定义字段聚合端口位置
      node.portProp(`${nodeId}_custom_fields_source`, 'args', { x: NODE_WIDTH, y: customTitleY });
      node.portProp(`${nodeId}_custom_fields_target`, 'args', { x: 0, y: customTitleY });

      // 更新所有自定义字段的端口位置
      customFields.forEach((field, index) => {
        const accumulatedHeight = index * LINE_HEIGHT;
        const finalY = customTitleY + accumulatedHeight + LINE_HEIGHT / 2 + LINE_TITLE_HEIGHT / 2;

        const sourcePortId = `${field.fieldId || field.fieldName}_source`;
        const targetPortId = `${field.fieldId || field.fieldName}_target`;

        node.portProp(sourcePortId, 'args', { x: NODE_WIDTH, y: finalY });
        node.portProp(targetPortId, 'args', { x: 0, y: finalY });
      });
    }
  }
}
