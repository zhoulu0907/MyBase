import ResizableTable from '@/components/ResizableTable';
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
      <ResizableTable
        data={data}
        virtualized={true}
        columns={columns}
        pagination={false}
        scroll={{ x: 'max-content' }}
        border={{
          wrapper: true,
          cell: true
        }}
      />
    </div>
  );
};

export default DataPreview;
