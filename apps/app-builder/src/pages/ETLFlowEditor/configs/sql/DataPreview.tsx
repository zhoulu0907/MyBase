import { Table } from '@arco-design/web-react';
import { previewETLFlowData } from '@onebase/app';
import { etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useState } from 'react';

interface DataPreviewProps {}

const DataPreview: React.FC<DataPreviewProps> = ({}) => {
  useSignals();

  const { curDrawerTab, curNode, nodeData, graphData } = etlEditorSignal;

  const [sqlValue] = useState<string>(nodeData.value[curNode.value.id]?.config?.sqlValue);

  const [previewData, setPreviewData] = useState<{
    columns: any[];
    data: any[];
  }>({
    columns: [],
    data: []
  });

  useEffect(() => {
    console.log('sqlValue: ', sqlValue);

    if (sqlValue && sqlValue != '') {
      handlePreviewData();
    }
  }, [sqlValue]);

  const handlePreviewData = async () => {
    const nodes = graphData.value.nodes?.map((node: any) => {
      return {
        id: node.id,
        title: nodeData.value[node.id].title || '',
        description: nodeData.value[node.id].description || '',
        type: node.type,
        config: nodeData.value[node.id].config || {},
        output: nodeData.value[node.id].output || {},
        meta: node.meta || {}
      };
    });

    const edges = graphData.value.edges?.map((edge: any) => {
      return {
        sourceNodeId: edge.sourceNodeID,
        targetNodeId: edge.targetNodeID
      };
    });

    const res = await previewETLFlowData({
      nodeId: curNode.value.id,
      workflow: {
        nodes: nodes,
        edges: edges
      }
    });

    console.log('res: ', res);

    if (res) {
      setPreviewData(res);
    }
  };

  return (
    <div style={{ height: '100%', overflow: 'auto', backgroundColor: '#fff' }}>
      <Table
        data={previewData.data}
        virtualized={true}
        columns={previewData.columns}
        pagination={false}
        scroll={{
          y: 480,
          x: true
        }}
        border={{
          wrapper: true,
          cell: true
        }}
      />
    </div>
  );
};

export default DataPreview;
