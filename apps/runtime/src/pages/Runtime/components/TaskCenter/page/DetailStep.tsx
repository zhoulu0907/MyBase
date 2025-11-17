import { useEffect, useState, type FC } from 'react';
import { Steps, Avatar } from '@arco-design/web-react';
// import { IconDownload, IconEye } from '@arco-design/web-react/icon';
import dayjs from 'dayjs';
// import ExpendSp from '@/assets/images/task_center/expend-sp.svg';
import '../style/tcPage.less';
import {approvalConfigVar} from '../constant'


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
    const opperator = nodeItem?.operators?.[0];
    const userImgArr:string[] = [];
    const userNameArr:string[] = [];
    if (Array.isArray(nodeItem?.operators)) {
      nodeItem.operators.forEach((item:any) => {
        userImgArr.push(item?.avatar)
        userNameArr.push(item?.operator)
      })
    }
    return (
      <>
        <div style={{ display: 'flex', alignItems: 'flex-end' }}>
          <AvatarGroup>
            {userImgArr && userImgArr.map((imgUrl) => <Avatar>
              <img
                alt="avatar"
                src={imgUrl}
              />
            </Avatar>)}
          </AvatarGroup>
          <span className="gray-color" style={{ marginLeft: '16px', marginBottom: '3px' }}>
            {userNameArr.join('、')}
          </span>
        </div>
        <p className="flex-bw-center date-line">
          <span className="sp-options" style={{padding: '5px 0px 8px'}}>
            <b>{nodeItem?.displayStatus}</b>
            <span>·多人审批{nodeItem?.approveMode && `（${approvalConfigVar.approvalMode[nodeItem?.approveMode]}）`}</span>
          </span>
          <span className="gray-color">
            {opperator?.operatorTime
                ? dayjs(opperator.operatorTime).format('YYYY-MM-DD HH:mm:ss')
                : '-'}
          </span>
        </p>
      </>
    );
  }
  function renderOneUser(nodeItem: any) {
    const opperator = nodeItem?.operators?.[0];
    return <>
      <div className="flex-bw-center">
        <p className="photo-img">{opperator?.avatar && <img src={opperator.avatar} alt='' />}</p>
        <div style={{ flex: 1 }}>
          <p>{opperator?.operator}</p>
          <p className="flex-bw-center">
            <span className="sp-options">{opperator?.taskStatus}</span>
            <span className="gray-color">
              {opperator?.operatorTime
                ? dayjs(opperator.operatorTime).format('YYYY-MM-DD HH:mm:ss')
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
                className="succss-box"
              />
            );
          }
        })}
      </Steps>
    );
  };

  useEffect(() => {
    setCurrent(stepData?.length);
  }, [stepData]);

  return (<>
    <ProcessFlow data={stepData} />
    {/* <Steps current={current} onChange={setCurrent} direction="vertical" className="rgt-sp-steps">
      <Step disabled={true} title="绿色成功" description={renderDescript(0)} className="succss-box" />
      <Step disabled={true} title="灰色成功" description={renderDescript(0)} className="succss-gray-box" />
      <Step disabled={true} title="红色撤回" description={renderDescript(0)} className="back-box" />
      <Step disabled={true} title="红色拒绝" description={renderDescript(0)} className="refuse-box" />
      <Step disabled={true} title="Processing" description={renderManyUsers()} />
      <Step disabled={true} title="Pending" description={renderDescript(0)} />
    </Steps> */}
  </>);
};
export default DetailStep;
