import { type FC } from 'react';
import { useLocation } from 'react-router-dom';
import WillDo from './page/WillDo';
import Done from './page/IDone';
import ICreated from './page/ICreated';
import ICopied from './page/ICopied';
import TaskProxy from './page/TaskProxy';

const TaskCenterPage: FC<any> = ({ curMenuId }) => {
  const location = useLocation();
  const pathParts = location.pathname.split('/').filter((part) => part !== '');
  const runtimeIndex = pathParts.indexOf('runtime');
  const appId = runtimeIndex !== -1 && pathParts[runtimeIndex + 1] ? pathParts[runtimeIndex + 1] : null;

  function renderPage() {
    if (curMenuId === 'TASK-ineedtodo') {
      return <WillDo appId={appId} />;
    } else if (curMenuId === 'TASK-ihavedone') {
      return <Done appId={appId} />;
    } else if (curMenuId === 'TASK-icreated') {
      return <ICreated />;
    } else if (curMenuId === 'TASK-icopied') {
      return <ICopied />;
    } else if (curMenuId === 'TASK-taskproxy') {
      return <TaskProxy />;
    } else {
      return <section>123</section>;
    }
  }
  return <>{renderPage()}</>;
};

export default TaskCenterPage;
