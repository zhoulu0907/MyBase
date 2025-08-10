import { getDatasource, type DatasourceSaveReqVO } from '@onebase/app';
import React, { useState } from 'react';
import CreateDsPage from './components/CreateDsPage';
import DataSourceTable from './components/DataSourceTable';
import EditDsDrawer from './components/EditDsDrawer';
import EmptyDsPage from './components/EmptyDsPage';

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
