import { type FC } from 'react';
import WillDo from './WillDo';

const TaskCenterPage:FC<any> = ({curMenuId}) => {
    function renderPage() {
        if (curMenuId === 'TASK-ineedtodo') {
            return <WillDo />
        } else {
            return <section>123</section>
        }
    }
    return <>
        {renderPage()}
    </>
}

export default TaskCenterPage;