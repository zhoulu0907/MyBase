import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams, useSearchParams } from 'react-router-dom';
import PreviewContainer from './components/preview';
import TaskCenterPage from './components/TaskCenter/TaskCenterPage';
import styles from './index.module.less';
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
      children: [],
    })
  }, [curMenuId]);

  return (
    <div className={styles.runtimePage}>
      {curMenuId.indexOf('TASK-') >= 0 ? (
        <TaskCenterPage curMenuId={curMenuId} />
      ) : (
        <PreviewContainer menuId={curMenuId || ''} runtime={true} />
      )}
    </div>
  );
};

export default Runtime;
