import React, { useEffect, useState } from 'react';
import CheckEntityPage from './CheckEntityPage';
import EmptyEntityPage from './EmptyEntityPage';

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
