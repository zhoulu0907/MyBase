import React, { useEffect, useState } from 'react';
import EmptyEntityPage from './main/EmptyEntityPage';
import CheckEntityPage from './main/EntityPage';

const EntityPage: React.FC = () => {
  const [pageType, setPageType] = useState('check-entity');

  const handlePageType = (type: string) => {
    setPageType(type);
  };

  useEffect(() => {}, []);

  return (
    <>
      {pageType === 'check-entity' && <CheckEntityPage handlePageType={handlePageType} />}
      {pageType === 'empty-entity' && <EmptyEntityPage handlePageType={handlePageType} />}
    </>
  );
};

export default EntityPage;
