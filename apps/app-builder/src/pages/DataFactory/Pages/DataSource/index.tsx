import React, { useState } from 'react';
import DataSourceTable from './DataSourceTable';
import CreateDsPage from './CreateDsPage';
import EmptyDsPage from './EmptyDsPage';
import EditDsDrawer from './EditDsDrawer';

const DataSource: React.FC = () => {
  const [pageType, setPageType] = useState('check-ds');
  const [editDrawerVisible, setEditDrawerVisible] = useState(false);
  const [currentDataSource, setCurrentDataSource] = useState<{
    id: number;
    datasourceName: string;
    code: string;
    datasourceType: string;
    config: Record<string, unknown>;
    description: string;
    runMode: number;
    appId: number;
  } | null>(null);

  const handlePageType = (tab: string) => {
    setPageType(tab);
  };

  const handleEdit = async (id: number) => {
    try {
      // TODO: 调用获取数据源详情接口
      // const res = await getDatasource(id);
      // if (res.code === 0) {
      //   setCurrentDataSource(res.data);
      //   setEditDrawerVisible(true);
      // }
      
      // 临时模拟数据
      setCurrentDataSource({
        id,
        datasourceName: '测试数据源',
        code: 'test_ds',
        datasourceType: 'MySQL',
        config: {
          host: 'localhost',
          port: 3306,
          database: 'test',
          username: 'root',
          password: 'password',
        },
        description: '测试用的数据源',
        runMode: 0,
        appId: 1,
      });
      setEditDrawerVisible(true);
    } catch (error) {
      console.error('获取数据源详情失败:', error);
    }
  };

  const handleEditSuccess = () => {
    // 编辑成功后刷新数据
    setPageType('check-ds');
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
        dataSource={currentDataSource}
        onSuccess={handleEditSuccess}
      />
    </div>
  );
};

export default DataSource;
