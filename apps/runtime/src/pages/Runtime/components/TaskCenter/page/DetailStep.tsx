import { useEffect, useState, type FC } from 'react';
import { Steps, Avatar } from '@arco-design/web-react';
// import { IconDownload, IconEye } from '@arco-design/web-react/icon';
import dayjs from 'dayjs';
// import ExpendSp from '@/assets/images/task_center/expend-sp.svg';
import dotImg from '../../../../../assets/images/task_center/one-dot.svg'
import systemImg from '../../../../../assets/images/task_center/system.svg'
import '../style/tcPage.less';
import {approvalConfigVar, displayStatusMap} from '../constant'


const Step = Steps.Step;
const AvatarGroup = Avatar.Group;

const DetailStep: FC<any> = ({ stepData }: any) => {
  const [current, setCurrent] = useState(0);

  function renderDescript(nodeItem: any) {
    if (nodeItem) {
      if (nodeItem?.operators?.length > 1) {
        return renderManyUsers(nodeItem)
      } else {
        return renderOneUser(nodeItem)
      }
    } else {
      return <></>
      // ui test code
      // return <>
      //   <div className='flex-bw-center'>
      //     <p className='photo-img'></p>
      //     <div style={{flex: 1}}>
      //         <p>张三</p>
      //         <p className='flex-bw-center'>
      //             <span className='sp-options'>审批意见</span>
      //             <span className='gray-color'>2025-02-02 12:11</span>
      //         </p>
      //     </div>
      //   </div>
      //   <div className="flex-bw-center no-photo" style={{ marginTop: '8px' }}>
      //     <p className="photo-img"></p>
      //     <div className="arco-upload-list arco-upload-list-type-picture-card">
      //       <div className="arco-upload-list-item arco-upload-list-item-done">
      //         <div className="arco-upload-list-item-picture">
      //           <img src="/src/assets/images/application_banner.svg" alt="20200717-103937.png" />
      //         </div>
      //       </div>
      //       <div className="arco-upload-list-item arco-upload-list-item-done">
      //         <div className="arco-upload-list-item-picture">
      //           <img src="/src/assets/images/application_banner.svg" alt="hahhahahahaha.png" />
      //           <div className="arco-upload-list-item-picture-mask" role="radiogroup">
      //             <div className="arco-upload-list-item-picture-operation" style={{ fontSize: '18px' }}>
      //               <span className="arco-upload-list-preview-icon" role="button" aria-label="预览">
      //                 <IconEye />
      //               </span>
      //             </div>
      //           </div>
      //         </div>
      //       </div>
      //       <div className="arco-upload-list-item arco-upload-list-item-done">
      //         <div className="arco-upload-list-item-picture">
      //           <img src="/src/assets/images/application_banner.svg" alt="hahhahahahaha.png" />
      //           <div className="arco-upload-list-item-picture-mask" role="radiogroup" style={{ opacity: 1 }}>
      //             <div className="arco-upload-list-item-picture-operation" style={{ fontSize: '16px' }}>
      //               <span className="arco-upload-list-preview-icon" role="button" aria-label="预览">
      //                 +7
      //               </span>
      //             </div>
      //           </div>
      //         </div>
      //       </div>
      //     </div>
      //   </div>
      //   <div className="flex-bw-center no-photo">
      //     <p className="photo-img"></p>
      //     <ul className="fj-file-box">
      //       <li className="flex-bw-center">
      //         <span className="flex-bw-center">
      //           <img src={ExpendSp} alt="" />
      //           12313213
      //         </span>
      //         <span className="flex-bw-center fj-rgt-btns">
      //           <IconEye />
      //           <IconDownload />
      //         </span>
      //       </li>
      //       <li className="flex-bw-center">
      //         <span className="flex-bw-center fj-lft-btn">
      //           <img src={ExpendSp} alt="" />
      //           <span className="text-span">12313213123132131231321312313213123132131231321312313213</span>
      //         </span>
      //         <span className="flex-bw-center fj-rgt-btns">
      //           <IconEye />
      //           <IconDownload />
      //         </span>
      //       </li>
      //     </ul>
      //   </div>
      // </>
    }
  }
  function renderManyUsers(nodeItem:any) {
    if(!nodeItem) {
      return;
    }
    // const opperator = nodeItem?.operators?.[0];
    const pendingUserArr:string[] = [];
    const viewedUserArr:string[] = [];
    const endUsersArr:any[] = [];
    const autoCopyArr: string[] = []
    const autoCopyItem:any = {
      // 模拟renderOneUser参数的结构
      operators: []
    };
    if (Array.isArray(nodeItem?.operators)) {
      let isNotStart:boolean = false;
      let isPending:boolean = false;
      nodeItem.operators.forEach((item:any) => {
        isNotStart = item.taskStatus === 'pre_approval' || item.taskStatus === '待审批'
        isPending = item.taskStatus === 'curr_in_approval' || item.taskStatus === '审批中'
        if (isPending || isNotStart) {
          pendingUserArr.push(item?.avatar)
        } else {
          // 不是审批中的，需要单独显示，模拟renderOneUser参数的结构
          if (item.taskStatus === 'post_auto_cc') {
            // 自动抄送 有特殊样式
            autoCopyArr.push(item?.operator)
          } else {
            endUsersArr.push({operators: [item]})
          }
        }
        if (item?.viewed) {
          viewedUserArr.push(item?.operator)
        }
      })
      if(autoCopyArr.length > 0) {
        let text:string;
        if (autoCopyArr.length > 2) {
          text = `${autoCopyArr[0]}、${autoCopyArr[1]}等`
        } else {
          text = autoCopyArr.join('、')
        }
        autoCopyItem.operators.push({
          operator: '系统',
          avatar: systemImg,
          taskStatus: 'post_auto_cc',
          operatorTime: nodeItem?.displayTime,
          colorText: text,
          autoCopyArr
        })
        endUsersArr.push(autoCopyItem)
      }
    }
    const userMap = displayStatusMap(nodeItem?.displayStatus)
    return (
      <>
        {endUsersArr.map((item:any) => {
          return renderOneUser(item)
        })}
        {pendingUserArr.length > 0 ? 
          <>
            <div className='user-temp' style={{ display: 'flex', alignItems: 'flex-end' }}>
              <AvatarGroup style={{maxWidth: '50%'}}>
                {pendingUserArr && pendingUserArr.map((imgUrl, i) => <Avatar key={i}>
                  <img
                    alt="avatar"
                    src={imgUrl}
                  />
                </Avatar>)}
              </AvatarGroup>
              {viewedUserArr.length > 0 &&<span className="gray-color" style={{ marginLeft: '14px', marginBottom: '3px', width: '44%', lineHeight: '16px' }}>
                {viewedUserArr.join('、')}&nbsp;已读
              </span>}
            </div>
            <p className="flex-bw-center date-line">
              <span className="sp-options" style={{padding: '5px 0px 8px'}}>
                <b className={userMap?.labelColor}>{userMap?.label}</b>
                <span><img src={dotImg} alt='' style={{width: '17px', position: 'relative', top: '4px'}} />多人审批{nodeItem?.approveMode && `（${approvalConfigVar.approvalMode[nodeItem?.approveMode]}）`}</span>
              </span>
              <span className="gray-color">
                {nodeItem?.waitTimeDesc ? nodeItem?.waitTimeDesc : '-'}
              </span>
            </p>
          </> : <div style={{height: '16px'}}></div>}
      </>
    );
  }
  function renderOneUser(nodeItem: any) {
    if (nodeItem?.nodeType === 'end') {
      return <div style={{height: '40px'}}></div>
    }
    const opperator = nodeItem?.operators?.[0];
    const userMap = displayStatusMap(opperator?.taskStatus);
    // 发起节点post_submitted 不需要显示 审批意见
    const hasComment = opperator?.taskStatus !== 'post_submitted'
    return <>
      <div className="flex-bw-center user-temp">
        <p className="photo-img">{opperator?.avatar && <img src={opperator.avatar} alt='' />}</p>
        <div style={{ flex: 1 }}>
          <p>{opperator?.operator}</p>
          <p className="flex-bw-center">
            {
              (opperator?.autoCopyArr?.length > 0) ? 
                <p style={{position: 'relative'}}>
                  <span className={`sp-options ${userMap?.labelColor}`} style={{fontWeight: 'normal'}}>{userMap?.label}</span>&nbsp;
                  <span className='auto-copy-color-span'>{opperator?.colorText}</span>
                  <span className='auto-copy-color-span absolute-span'>{opperator?.autoCopyArr?.length}人</span>
                </p> : 
                <span>
                  <b className={`sp-options ${userMap?.labelColor}`}>{userMap?.label}</b>
                  {(opperator?.comment && hasComment) && <span className="gray-color">&nbsp;({opperator?.comment})</span>}
                </span>
            }
            <span className="gray-color">
              {(nodeItem?.displayTime || opperator?.operatorTime)
                ? dayjs(nodeItem?.displayTime || opperator?.operatorTime).format('YYYY-MM-DD HH:mm:ss')
                : '-'}
            </span>
          </p>
        </div>
      </div>
    </>
  }

  const ProcessFlow = ({ data }: any) => {
    return (
      <Steps current={current} onChange={setCurrent} direction="vertical" className="rgt-sp-steps">
        {data?.map((item: any, index: number) => {
          if (index === data.length - 1) {
            // 最后一个，属于未处理状态
            return <Step key={index} disabled={true} title={item?.nodeName} description={renderDescript(item)} />;
          } else {
            // 完成状态
            return (
              <Step
                key={index}
                disabled={true}
                title={item?.nodeName}
                description={renderDescript(item)}
                className={displayStatusMap(item?.displayStatus)?.iconClass}
              />
            );
          }
        })}
      </Steps>
    );
  };

  useEffect(() => {
    if (Array.isArray(stepData)) {
      for(let k = 0; k < stepData.length; k++) {
        if (stepData[k]?.isCurrent) {
          setCurrent(k + 1)
          break;
        }
      }
    }
  }, [stepData]);

  return (<>
    <ProcessFlow data={stepData} />
    {/* <Steps current={5} onChange={setCurrent} direction="vertical" className="rgt-sp-steps">
      <Step disabled={true} title="绿色成功" description={renderDescript(0)} className="succss-box" />
      <Step disabled={true} title="灰色成功" description={renderDescript(0)} className="succss-gray-box" />
      <Step disabled={true} title="红色撤回" description={renderDescript(0)} className="back-box" />
      <Step disabled={true} title="红色拒绝" description={renderDescript(0)} className="refuse-box" />
      <Step disabled={true} title="Processing" description={renderManyUsers(renderManyTest)} />
      <Step disabled={true} title="Pending" description={renderDescript(0)} />
    </Steps> */}
  </>);
};
export default DetailStep;
