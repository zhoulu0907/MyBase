import React, { useState } from 'react';
import { Modal, Button, Progress, Space, Link } from '@arco-design/web-react';
import { IconCheckCircleFill, IconCloseCircleFill, IconClockCircle, IconRefresh, IconShareInternal, IconRecordStop } from '@arco-design/web-react/icon';
import ProcessBlueImg from '../../../../../assets/images/task_center/process-blue.svg'

import '../style/batchApprove.less'

interface ModalProps {
  approveVisible: boolean;
  setApproveVisible: Function;
}

const BatchApproveModal: React.FC<ModalProps> = ({approveVisible, setApproveVisible}) => {
    let [loading1, setLoading1] = useState(false)
    return <Modal className='batch-modal-wrap'
        title={
          <div style={{ textAlign: 'left' }}>正在处理批量同意审批任务</div>
        }
        footer={
          <>
            <Button type='secondary'><IconRecordStop />终止任务</Button>
            <Button
              loading={loading1}
              onClick={() => {
                setLoading1(true);
              }}
              type='primary'
            >
              确定
            </Button>
          </>
        }
        visible={approveVisible}
        onOk={() => setApproveVisible(false)}
        onCancel={() => setApproveVisible(false)}
        autoFocus={false}
        focusLock={true}
      >
        <div className='arco-row'>
            <span>处理进度：13/50</span>
            <Progress percent={30} size='large' style={{flex: 1, marginLeft: '16px'}} />
        </div>
        <div style={{padding: '8px 0 16px', borderBottom: '1px solid rgba(229, 230, 235, 1)', display: 'flex', justifyContent: 'space-between'}}>
            <Space size='medium'>
                <Link hoverable={false} icon={<IconCheckCircleFill />}>9项成功</Link>
                <Link hoverable={false} icon={<IconCloseCircleFill />}>4项成功</Link>
                <Link hoverable={false} icon={<IconClockCircle />}>29项待处理</Link>
            </Space>
            <Button type='outline' className='gray-btn'><IconRefresh />重试所有失败项</Button>
        </div>
        <div className="arco-list arco-list-default list-demo-actions" style={{border: 'none'}}>
            <div role="list" className="arco-list-content">
                <div role="listitem" className="arco-list-item">
                    <div className="arco-list-item-main">
                        <div className="arco-list-item-meta">
                            <div className="arco-list-item-meta-avatar">
                                <IconCheckCircleFill />
                            </div>
                            <div className="arco-list-item-meta-content">
                                <div className="arco-list-item-meta-title">Beijing Bytedance Technology Co., Ltd.</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div role="listitem" className="arco-list-item">
                    <div className="arco-list-item-main">
                        <div className="arco-list-item-meta">
                            <div className="arco-list-item-meta-avatar">
                                <IconClockCircle />
                            </div>
                            <div className="arco-list-item-meta-content">
                                <div className="arco-list-item-meta-title">Beijing Bytedance Technology Co., Ltd.</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div role="listitem" className="arco-list-item">
                    <div className="arco-list-item-main">
                        <div className="arco-list-item-meta">
                            <div className="arco-list-item-meta-avatar">
                                <img src={ProcessBlueImg} />
                            </div>
                            <div className="arco-list-item-meta-content">
                                <div className="arco-list-item-meta-title">Beijing Bytedance Technology Co., Ltd.</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div role="listitem" className="arco-list-item">
                    <div className="arco-list-item-main">
                        <div className="arco-list-item-meta">
                            <div className="arco-list-item-meta-avatar">
                                <IconCloseCircleFill />
                            </div>
                            <div className="arco-list-item-meta-content">
                                <div className="arco-list-item-meta-title">Beijing Bytedance Technology Co., Ltd.</div>
                                <div className="arco-list-item-meta-description">Beijing ByteDance Technology Co., Ltd. is an enterprise located in China.</div>
                            </div>
                        </div>
                    </div>
                    <div className="arco-list-item-action">
                        <Button type='outline' className='gray-btn' style={{marginRight: '12px'}}><IconShareInternal />查看表单</Button>
                        <Button type='outline' className='gray-btn'><IconRefresh />重试</Button>
                    </div>
                </div>
            </div>
        </div>
    </Modal>
}

export default BatchApproveModal;