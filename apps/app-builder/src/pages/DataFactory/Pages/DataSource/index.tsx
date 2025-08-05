import React, { useState } from 'react';
import DataSourceTable from './DataSourceTable';
import CreateDsPage from './CreateDsPage';
import EmptyDsPage from './EmptyDsPage';
import EditDsDrawer from './EditDsDrawer';
import { getDatasource } from '@onebase/app/src/services/dataresource';
import type { DatasourceSaveReqVO } from '@onebase/app/src/types';

const DataSourcePage: React.FC = () => {
  const [pageType, setPageType] = useState('check-ds');
  const [editDrawerVisible, setEditDrawerVisible] = useState(false);
  const [currentDataSource, setCurrentDataSource] = useState<DatasourceSaveReqVO | null>(null);

  const handlePageType = (tab: string) => {
    setPageType(tab);
  };

  const handleEdit = async (id: number) => {
    try {
      const res = await getDatasource(id);
      console.log('handleEdit res', res);
      if (res) {
        setCurrentDataSource(res);
        setEditDrawerVisible(true);
      }
    } catch (error) {
      console.error('获取数据源详情失败:', error);
    }
  };

  const handleEditSuccess = () => {
    // 编辑成功后刷新数据
    setPageType('check-ds'); 
    // getTableData();
  };

  const renderContent = () => {
    switch (pageType) {
      case 'create-ds':
        return <CreateDsPage handlePageType={handlePageType} />;
      case 'check-ds':
        return <DataSourceTable handlePageType={handlePageType} onEdit={handleEdit} />;
      default:
        return <EmptyDsPage handlePageType={handlePageType} />;
    }
  };

  return (
    <div>
      {renderContent()}
      <EditDsDrawer
        visible={editDrawerVisible}
        onClose={() => setEditDrawerVisible(false)}
        dataSource={currentDataSource || undefined}
        onSuccess={handleEditSuccess}
      />
    </div>
  );
};

export default DataSourcePage;
