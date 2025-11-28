import { useFieldStore } from '@/store/store_field';
import { getFieldTypes } from '@onebase/app';
import React, { useEffect } from 'react';
import { EntityPageContainer } from './main/EntityPageContainer';

interface EntityPageProps {
  appId: string;
  handleMenuClick: (key: string) => void;
}

const EntityPage: React.FC<EntityPageProps> = ({ appId, handleMenuClick }) => {
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
      <EntityPageContainer appId={appId} handleMenuClick={handleMenuClick} />
    </>
  );
};

export default EntityPage;
