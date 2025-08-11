import React, { useEffect, useState } from 'react';
import { CheckEntityPage } from './main/CheckEntityPage';
import { EmptyEntityPage } from './main/EmptyEntityPage';

const EntityPage: React.FC = () => {
  const [pageType, setPageType] = useState('check-entity');

  const handlePageType = (type: string) => {
    setPageType(type);
  };

  useEffect(() => {}, []);

  return (
    <>
      {pageType === 'check-entity' && <CheckEntityPage />}
      {pageType === 'empty-entity' && <EmptyEntityPage handlePageType={handlePageType} />}
    </>
  );
};

export default EntityPage;
