import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams, useSearchParams } from 'react-router-dom';
import PreviewContainer from './components/preview';
import TaskCenterPage from './components/TaskCenter/TaskCenterPage';
import styles from './index.module.less';

const Runtime: React.FC = () => {
  const [search] = useSearchParams();
  const curMenuId = search.get('curMenu') || '';


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
