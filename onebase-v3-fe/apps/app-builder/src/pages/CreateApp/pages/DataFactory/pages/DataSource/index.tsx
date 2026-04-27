import React, { useState } from 'react';
import CreateDsPage from './components/CreateDsPage';
import DataSourceTable from './components/DataSourceTable';
import EmptyDsPage from './components/EmptyDsPage';

const DataSourcePage: React.FC = () => {
  const [pageType, setPageType] = useState('check-ds');

  const handlePageType = (tab: string) => {
    setPageType(tab);
  };

  const renderContent = () => {
    switch (pageType) {
      case 'create-ds':
        return <CreateDsPage handlePageType={handlePageType} />;
      case 'check-ds':
        return <DataSourceTable handlePageType={handlePageType} />;
      default:
        return <EmptyDsPage handlePageType={handlePageType} />;
    }
  };

  return <div>{renderContent()}</div>;
};

export default DataSourcePage;
