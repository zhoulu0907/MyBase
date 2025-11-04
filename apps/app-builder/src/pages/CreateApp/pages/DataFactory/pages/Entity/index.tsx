import { useFieldStore } from '@/store/store_field';
import { getFieldTypes } from '@onebase/app';
import React, { useEffect } from 'react';
import { EntityPageContainer } from './main/EntityPageContainer';

interface EntityPageProps {
  appId: string;
}

const EntityPage: React.FC<EntityPageProps> = ({ appId }) => {
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
      <EntityPageContainer appId={appId} />
    </>
  );
};

export default EntityPage;
