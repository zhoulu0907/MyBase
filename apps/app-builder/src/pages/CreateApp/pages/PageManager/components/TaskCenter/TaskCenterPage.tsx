import { type FC } from 'react';
import WillDo from './page/WillDo';
import Done from './page/IDone'
import ICreated from './page/ICreated';
import ICopied from './page/ICopied';
import TaskProxy from './page/TaskProxy'

const TaskCenterPage:FC<any> = ({curMenuId}) => {
    function renderPage() {
        if (curMenuId === 'TASK-ineedtodo') {
            return <WillDo />
        } else if (curMenuId === 'TASK-ihavedone') {
            return <Done />
        } else if (curMenuId === 'TASK-icreated') {
            return <ICreated />
        } else if (curMenuId === 'TASK-icopied') {
            return <ICopied />
        } else if (curMenuId === 'TASK-taskproxy') {
            return <TaskProxy />
        } else {
            return <section>123</section>
        }
    }
    return <>
        {renderPage()}
    </>
}

export default TaskCenterPage;