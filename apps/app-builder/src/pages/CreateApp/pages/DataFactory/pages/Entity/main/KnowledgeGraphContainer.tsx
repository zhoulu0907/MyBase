import { Graph } from '@antv/g6';
import { getEntityGraph } from '@onebase/app';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import styles from '../index.module.less';

interface KnowledgeGraphContainerProps {
  datasourceId?: string;
}

interface GraphData {
  nodes: any[];
  edges: any[];
}

export const KnowledgeGraphContainer: React.FC<KnowledgeGraphContainerProps> = ({ datasourceId }) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const graphRef = useRef<Graph | null>(null);
  const [data, setData] = useState<GraphData>({
    nodes: [],
    edges: []
  });

  const getGraphData = useCallback(async () => {
    if (datasourceId) {
      const res = await getEntityGraph(datasourceId);
      console.log(res);

      // 将接口返回的实体、字段和关系数据，转换为 G6 所需的节点和边格式
      if (res && Array.isArray(res.entities) && res.entities.length > 0) {
        type Entity = {
          entityId: string;
          entityName: string;
          displayConfig?: string;
          fields?: any[];
        };
        type Relationship = {
          sourceEntityId: string;
          targetEntityId: string;
          sourceEntityName: string;
          targetEntityName: string;
          relationshipType: string;
          description?: string;
        };

        const nodes: any[] = [];
        const edges: any[] = [];

        // 先处理实体节点
        res.entities.forEach((entity: Entity) => {
          let config = {};
          try {
            config = entity.displayConfig ? JSON.parse(entity.displayConfig) : {};
          } catch {
            config = {};
          }
          // entity节点
          nodes.push({
            id: entity.entityId,
            label: entity.entityName,
            isLeaf: false,
            size: 60
          });

          // 针对每个字段，生成Leaf节点，并和entity连接
          if (Array.isArray(entity.fields)) {
            entity.fields.forEach((field: any) => {
              // field节点
              nodes.push({
                id: field.fieldId,
                label: field.displayName || field.fieldName,
                isLeaf: true,
                size: 20
              });
              // field-edge(entity -> field)
              edges.push({
                source: entity.entityId,
                target: field.fieldId,
                label: '',
                relationshipType: 'ENTITY_FIELD'
              });
            });
          }
        });

        // 处理实体间关系
        if (Array.isArray(res.relationships) && res.relationships.length > 0) {
          res.relationships.forEach((rel: Relationship) => {
            edges.push({
              source: rel.sourceEntityId,
              target: rel.targetEntityId,
              label:
                rel.relationshipType === 'ONE_TO_MANY'
                  ? `${rel.sourceEntityName} → ${rel.targetEntityName} [1:N]`
                  : `${rel.sourceEntityName} → ${rel.targetEntityName}`
            });
          });
        }

        console.log(nodes);
        console.log(edges);

        setData({ nodes, edges });
      }
    }
  }, [datasourceId]);

  const renderGraph = useCallback(() => {
    if (!containerRef.current) {
      return;
    }

    // 如果 graph 已存在，先销毁
    if (graphRef.current) {
      graphRef.current.destroy();
      graphRef.current = null;
    }

    // 如果数据为空，不渲染
    if (data.nodes.length === 0 && data.edges.length === 0) {
      return;
    }

    graphRef.current = new Graph({
      container: containerRef.current!,
      data,
      node: {
        style: {
          size: (d: any) => d.size,
          labelText: (d: any) => d.label
        }
      },
      layout: {
        type: 'd3-force',
        link: {
          distance: (d: any) => {
            if (!d.source.isLeaf && !d.target.isLeaf) {
              return 200;
            }
            return 50;
          },
          strength: () => {
            return 0.1;
          }
        },
        manyBody: {
          strength: (d: any) => {
            if (d.isLeaf) {
              return -50;
            }
            return -10;
          }
        }
      },
      behaviors: ['drag-element-force']
    });

    graphRef.current.render();
  }, [data]);

  // 获取数据
  useEffect(() => {
    getGraphData();
  }, [getGraphData]);

  // 渲染图表
  useEffect(() => {
    renderGraph();

    // 清理函数：组件卸载时销毁 graph
    return () => {
      if (graphRef.current) {
        graphRef.current.destroy();
        graphRef.current = null;
      }
    };
  }, [renderGraph]);
  return (
    <div style={{ height: '100%' }} className={styles['entity-page-container']}>
      <div style={{ width: '100%', height: '100%' }} ref={containerRef}></div>
    </div>
  );
};
