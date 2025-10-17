import { type FC } from 'react';
import WillDo from './page/WillDo';
import Done from './page/Done'

const TaskCenterPage:FC<any> = ({curMenuId}) => {
    function renderPage() {
        if (curMenuId === 'TASK-ineedtodo') {
            return <WillDo />
        } else if (curMenuId === 'TASK-ihavedone') {
            return <Done />
        } else if (curMenuId === 'TASK-icreated') {
            return <section>123</section>
        } else if (curMenuId === 'TASK-icopied') {
            return <section>123</section>
        } else if (curMenuId === 'TASK-taskproxy') {
            return <section>123</section>
        } else {
            return <section>123</section>
        }
    }
    return <>
        {renderPage()}
    </>
}

export default TaskCenterPage;