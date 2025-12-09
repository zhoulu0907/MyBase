import { useState, type FC } from 'react';
import { Steps, Avatar } from '@arco-design/web-react';
import { IconDownload, IconEye } from '@arco-design/web-react/icon';
import '../style/tcPage.less'
import ExpendSp from '@/assets/images/task_center/expend-sp.svg'

const Step = Steps.Step;
const AvatarGroup = Avatar.Group;

const DetailStep:FC = () => {
    const [current, setCurrent] = useState(5);

    function renderDescript() {
        return <>
            <div className='flex-bw-center'>
                <p className='photo-img'></p>
                <div style={{flex: 1}}>
                    <p>张三</p>
                    <p className='flex-bw-center'>
                        <span className='sp-options'>审批意见</span>
                        <span className='gray-color'>2025-02-02 12:11</span>
                    </p>
                </div>
            </div>
            <div className='flex-bw-center no-photo' style={{marginTop: '8px'}}>
                <p className='photo-img'></p>
                <div className="arco-upload-list arco-upload-list-type-picture-card">
                    <div className="arco-upload-list-item arco-upload-list-item-done">
                        <div className="arco-upload-list-item-picture">
                            <img src="/src/assets/images/application_banner.svg" alt="20200717-103937.png" />
                        </div>
                    </div>
                    <div className="arco-upload-list-item arco-upload-list-item-done">
                        <div className="arco-upload-list-item-picture">
                            <img src="/src/assets/images/application_banner.svg" alt="hahhahahahaha.png" />
                            <div className="arco-upload-list-item-picture-mask" role="radiogroup">
                                <div className="arco-upload-list-item-picture-operation" style={{fontSize: '18px'}}>
                                    <span className="arco-upload-list-preview-icon" role="button" aria-label="预览"><IconEye /></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="arco-upload-list-item arco-upload-list-item-done">
                        <div className="arco-upload-list-item-picture">
                            <img src="/src/assets/images/application_banner.svg" alt="hahhahahahaha.png" />
                            <div className="arco-upload-list-item-picture-mask" role="radiogroup" style={{opacity: 1}}>
                                <div className="arco-upload-list-item-picture-operation" style={{fontSize: '16px'}}>
                                    <span className="arco-upload-list-preview-icon" role="button" aria-label="预览">+7</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div className='flex-bw-center no-photo'>
                <p className='photo-img'></p>
                <ul className='fj-file-box'>
                    <li className='flex-bw-center'>
                        <span className='flex-bw-center'><img src={ExpendSp} alt='' />12313213</span>
                        <span className='flex-bw-center fj-rgt-btns'><IconEye /><IconDownload /></span>
                    </li>
                    <li className='flex-bw-center'>
                        <span className='flex-bw-center fj-lft-btn'><img src={ExpendSp} alt='' /><span className='text-span'>12313213123132131231321312313213123132131231321312313213</span></span>
                        <span className='flex-bw-center fj-rgt-btns'><IconEye /><IconDownload /></span>
                    </li>
                </ul>
            </div>
        </>
    }
    function renderDespUsers() {
        return <>
            <div style={{display: 'flex', alignItems: 'flex-end'}}>
                <AvatarGroup>
                    <Avatar>
                        <img
                        alt='avatar'
                        src='//p1-arco.byteimg.com/tos-cn-i-uwbnlip3yd/3ee5f13fb09879ecb5185e440cef6eb9.png~tplv-uwbnlip3yd-webp.webp'
                        />
                    </Avatar>
                    <Avatar>
                        <img
                        alt='avatar'
                        src='//p1-arco.byteimg.com/tos-cn-i-uwbnlip3yd/3ee5f13fb09879ecb5185e440cef6eb9.png~tplv-uwbnlip3yd-webp.webp'
                        />
                    </Avatar>
                    <Avatar>
                        <img
                        alt='avatar'
                        src='//p1-arco.byteimg.com/tos-cn-i-uwbnlip3yd/3ee5f13fb09879ecb5185e440cef6eb9.png~tplv-uwbnlip3yd-webp.webp'
                        />
                    </Avatar>
                    <Avatar>
                        <img
                        alt='avatar'
                        src='//p1-arco.byteimg.com/tos-cn-i-uwbnlip3yd/3ee5f13fb09879ecb5185e440cef6eb9.png~tplv-uwbnlip3yd-webp.webp'
                        />
                    </Avatar>
                    <Avatar>
                        <img
                        alt='avatar'
                        src='//p1-arco.byteimg.com/tos-cn-i-uwbnlip3yd/3ee5f13fb09879ecb5185e440cef6eb9.png~tplv-uwbnlip3yd-webp.webp'
                        />
                    </Avatar>
                </AvatarGroup>
                <span className='gray-color' style={{marginLeft: '16px', marginBottom: '3px'}}>张三、李四已读</span>
            </div>
            <p className='flex-bw-center date-line'>
                <span className='sp-options'><b>审批中</b><span>·多人审批（会签）</span></span>
                <span className='gray-color'>2025-02-02 12:11</span>
            </p>
        </>
    }
    return <Steps current={current} onChange={setCurrent} direction='vertical' className='rgt-sp-steps'>
        <Step disabled={true} title='绿色成功' description={renderDescript()} className='succss-box' />
        <Step disabled={true} title='灰色成功' description={renderDescript()} className='succss-gray-box' />
        <Step disabled={true} title='红色撤回' description={renderDescript()} className='back-box' />
        <Step disabled={true} title='红色拒绝' description={renderDescript()} className='refuse-box' />
        <Step disabled={true} title='Processing' description={renderDespUsers()} />
        <Step disabled={true} title='Pending' description={renderDescript()} />
    </Steps>
}
export default DetailStep;