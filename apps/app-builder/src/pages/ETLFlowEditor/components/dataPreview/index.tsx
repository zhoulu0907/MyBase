import { Table } from '@arco-design/web-react';
import React from 'react';

interface DataPreviewProps {
  /**
   * 预览数据
   */
  data?: any[];
  /**
   * 列定义
   */
  columns?: any[];
}

const DataPreview: React.FC<DataPreviewProps> = ({ data, columns }) => {
  return (
    <div style={{ height: '100%', overflow: 'auto', backgroundColor: '#fff' }}>
      <Table
        data={data}
        virtualized={true}
        columns={columns}
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
