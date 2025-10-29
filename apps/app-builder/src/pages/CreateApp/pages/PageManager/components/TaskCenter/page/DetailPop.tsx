import { useRef, useState, type FC } from 'react';
import { Drawer, Grid, Tag, Button, Popconfirm, Tooltip } from '@arco-design/web-react';
import { IconFullscreen, IconLink, IconDoubleRight, IconFullscreenExit} from '@arco-design/web-react/icon';
import ExpendSp from '@/assets/images/task_center/expend-sp.svg'
import ProPreviewImg from '@/assets/images/task_center/process-preview.svg'
import DetailTable from './DetailTable'
import DetailStep from './DetailStep'
import DetailOKConfirm from './DetailOKConfirm';

const Row = Grid.Row;
const Col = Grid.Col;

interface PageProps {
  detailPopVisible: boolean,
  setPopVisible: Function
}

const DetailPage:FC<PageProps> = ({detailPopVisible=false, setPopVisible}) => {
    let [drawWidth, setDrawWidth] = useState<string>('66.66%')
    let [isShowRight, setIsShowRight] = useState(true)

    let confirmRef = useRef<any>(null)

    function toggleFullScreen(type: string) {
        if (type === 'FULLSCREEN') {
            setDrawWidth('100%')
        } else {
            setDrawWidth('66.66%')
        }
    }
    function renderTitle() {
        return <>
            <span>Basic Information </span>
            <div>
                {drawWidth !== '100%' ? <IconFullscreen onClick={() => toggleFullScreen('FULLSCREEN')}/> :
                    <IconFullscreenExit onClick={() => toggleFullScreen('INITSCREEN')}/>}
                <IconLink />
            </div>
        </>
    }
    function handleConfirmOK() {
        confirmRef.current.childMethod()
    }
    function handlePreview() {
        console.log('handle Preview ...')
    }
    function renderDrawerFooter() {
        return <>
            <Button type='text' onClick={handlePreview}><img src={ProPreviewImg} style={{marginRight: '3px'}}/>流程预览</Button>
            <Popconfirm
                title=''
                style={{maxWidth: '420px', width: '420px'}}
                className='dt-ok-confirm'
                content={<DetailOKConfirm ref={confirmRef}/>}
                onOk={() => {handleConfirmOK()}}
                onCancel={() => {console.log('pop confirm cancel...')}}
            >
                <Button type='primary'>确定</Button>
            </Popconfirm>
            <Button type='outline' onClick={() => setPopVisible(false)}>拒绝</Button>
        </>
    }
    return <section>
        <Drawer className='draw-detail-pop'
            width={drawWidth}
            title={renderTitle()}
            visible={detailPopVisible}
            footer={renderDrawerFooter()}
            onOk={() => {
                setPopVisible(false);
            }}
            onCancel={() => {
                setPopVisible(false);
            }}
        >
            <div className='draw-wrap-box'>
                <Row className='header-row' style={{ marginBottom: 16 }}>
                    <Col span={6}>
                        <p className='gray-color'>当前状态</p>
                        <div style={{padding: '4px 0'}}><Tag color='arcoblue' defaultChecked checkable={false}>Lark</Tag></div>
                    </Col>
                    <Col span={6}>
                        <p className='gray-color'>发起人</p>
                        <div className='photo-box'>
                            <p className='photo-img'></p>某某人
                        </div>
                    </Col>
                    <Col span={6}>
                        <p className='gray-color'>发起部门</p>
                        <div className='photo-box'>科创中心</div>
                    </Col>
                    <Col span={6}>
                        <p className='gray-color'>流程版本号</p>
                        <div className='photo-box'>V1</div>
                    </Col>
                </Row>
                <div className='draw-content'>
                    <div className='draw-left'>
                        <Row className='' style={{ marginBottom: 16 }}>
                            <Col span={8}>
                                <p className='gray-color'>申请人</p>
                                <div className='photo-box'>
                                    <p className='photo-img'></p>某某人
                                </div>
                            </Col>
                            <Col span={8}>
                                <p className='gray-color'>申请部门</p>
                                <div className='photo-box'>科创中心</div>
                            </Col>
                            <Col span={8}>
                                <p className='gray-color'>申请日期</p>
                                <div className='photo-box'>2025-08-09 14:55</div>
                            </Col>
                        </Row>
                        <p className='gray-color photo-box'>申请明细</p>
                        <DetailTable />
                    </div>
                    {isShowRight ?(<div className='draw-right'>
                            <div className='arco-drawer-header'>
                                <div className='arco-drawer-header-title'>
                                    <span>审批记录</span>
                                    <IconDoubleRight onClick={() => setIsShowRight(false)}/>
                                </div>
                            </div>
                            <DetailStep />
                        </div>) : 
                        (<Tooltip position='lt' trigger='hover' content='展开审批记录'><div className='expend-sp-box' onClick={() => setIsShowRight(true)}>
                            <img src={ExpendSp} alt='' />
                        </div></Tooltip>)
                    }
                </div>
            </div>
        </Drawer>
    </section>
}

export default DetailPage;
