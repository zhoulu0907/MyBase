import { useFieldStore } from '@/store/store_field';
import { getFieldTypes } from '@onebase/app';
import React, { useEffect } from 'react';
import { EntityPageContainer } from './main/EntityPageContainer';

const EntityPage: React.FC = () => {
  const { setFieldTypes } = useFieldStore();

  // 加载字段类型
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
      <EntityPageContainer />
    </>
  );
};

export default EntityPage;
