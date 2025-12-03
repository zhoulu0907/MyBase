import React, { useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import PreviewContainer from './components/preview';
import { menuSignal } from '@onebase/app';

const Runtime: React.FC = () => {
  const { setCurMenu } = menuSignal;
  const [search] = useSearchParams();
  const curMenuId = search.get('curMenu') || '';
  useEffect(() => {
    setCurMenu({
      id: curMenuId,
      menuCode: curMenuId,
      menuSort: 1,
      menuType: 1,
      menuName: curMenuId,
      menuIcon: '',
      isVisible: 1,
      children: []
    });
  }, [curMenuId]);

  const renderContent =
    curMenuId.indexOf('TASK-') >= 0 ? null : <PreviewContainer menuId={curMenuId || ''} runtime={true} />;

  return <div>{renderContent}</div>;
};

export default Runtime;
