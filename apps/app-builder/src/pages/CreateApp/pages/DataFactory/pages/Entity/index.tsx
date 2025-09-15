import React, { useEffect, useState } from 'react';
import { CheckEntityPage } from './main/CheckEntityPage';
import { EmptyEntityPage } from './main/EmptyEntityPage';
import { getFieldTypes } from '@onebase/app';
import { useFieldStore } from '@/store/store_field';

const EntityPage: React.FC = () => {
  const [pageType, setPageType] = useState('check-entity');
  const { setFieldTypes } = useFieldStore();
  const handlePageType = (type: string) => {
    setPageType(type);
  };

  const loadFieldTypes = async () => {
    const res = await getFieldTypes();

    if (res.length > 0) {
      setFieldTypes(res);
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
