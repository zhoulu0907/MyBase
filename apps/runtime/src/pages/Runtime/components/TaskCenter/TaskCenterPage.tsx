import { type FC } from 'react';
import { useLocation } from 'react-router-dom';
import WillDo from './page/WillDo';
import Done from './page/IDone';
import ICreated from './page/ICreated';
import ICopied from './page/ICopied';
import TaskProxy from './page/TaskProxy';
import { TASKMENU_TYPE } from '@onebase/app';
const TaskCenterPage: FC<any> = ({ curMenuId }) => {
  const location = useLocation();
  const pathParts = location.pathname.split('/').filter((part: any) => part !== '');
  const runtimeIndex = pathParts.indexOf('runtime');
  const appId = runtimeIndex !== -1 && pathParts[runtimeIndex + 1] ? pathParts[runtimeIndex + 1] : null;

  function renderPage() {
    if (curMenuId === TASKMENU_TYPE.TASKINEEDTODO) {
      return <WillDo appId={appId} />;
    } else if (curMenuId === TASKMENU_TYPE.TASKIHAVEDONE) {
      return <Done appId={appId} />;
    } else if (curMenuId === TASKMENU_TYPE.TASKICREATED) {
      return <ICreated appId={appId} />;
    } else if (curMenuId === TASKMENU_TYPE.TASKICOPIED) {
      return <ICopied />;
    } else if (curMenuId === TASKMENU_TYPE.TASKTASKPROXY) {
      return <TaskProxy appId={appId} />;
    } else {
      return <></>;
    }
  }
  return <>{renderPage()}</>;
};

export default TaskCenterPage;
