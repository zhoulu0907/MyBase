import React, { useEffect, useState } from 'react';
import { CheckEntityPage } from './main/CheckEntityPage';
import { EmptyEntityPage } from './main/EmptyEntityPage';
import { getFieldTypes } from '@onebase/app';

const EntityPage: React.FC = () => {
  const [pageType, setPageType] = useState('check-entity');
  const handlePageType = (type: string) => {
    setPageType(type);
  };

  const loadFieldTypes = async () => {
    const res = await getFieldTypes();

    if (res.length > 0) {
      const fieldTypes = res.map((item: { displayName: string; fieldType: string }) => ({
        label: item.displayName,
        value: item.fieldType
      }));
      localStorage.setItem('fieldTypes', JSON.stringify(fieldTypes));
    }
  };

  useEffect(() => {
    loadFieldTypes();
  }, []);

  return (
    <>
      {pageType === 'check-entity' && <CheckEntityPage />}
      {pageType === 'empty-entity' && <EmptyEntityPage handlePageType={handlePageType} />}
    </>
  );
};

export default EntityPage;
