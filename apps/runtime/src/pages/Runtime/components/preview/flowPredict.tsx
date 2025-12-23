import { useEffect, useState, type FC } from 'react';
import { Steps, Avatar } from '@arco-design/web-react';
import { fetchFlowPredict } from '@onebase/app/src/services/app_runtime';
// import styles from './index.module.less';
import './flow.less'

const Step = Steps.Step;

const FlowPredict = ({ businessUuid, entityParam }: any) => {
  const [current, setCurrent] = useState(0);
  const [stepData, setStepData] = useState();

  function renderDescript(value: any) {
    const nameArr:Array<any> = []
    const imgArr:Array<{img: any, text:string}> = []
    value?.handlers?.forEach((handler:any) => {
      nameArr.push(handler?.handlerName)
      imgArr.push({img: handler?.avatar, text: handler?.handlerName?.charAt(0) || ''})
    })
    return (
      <div className='predictBox'>
        <Avatar.Group>
          {imgArr.map((item:any) => {
            return item?.img ? <Avatar><img src={item.img} alt='' /></Avatar> : <Avatar>{item?.text}</Avatar>
          })}
        </Avatar.Group>
        <div className='predictText'>
          <div className='predictName'>
            {nameArr.join('、') || '暂无处理人'}
          </div>
          <div className='predictDesc'>{value?.nodeName}</div>
        </div>
      </div>
    );
  }

  const ProcessFlow = ({ data }: any) => {
    return (
      <Steps current={current} direction="vertical" type="dot" style={{paddingLeft: '2px'}}>
        {data?.map((item: any, index: number) => {
          if (index === data.length - 1) {
            return <Step key={index} disabled={true} title={renderDescript(item)} className="predictStepGray" />;
          }
          return <Step key={index} disabled={true} title={renderDescript(item)} className="predictStepSuccess" />;
        })}
      </Steps>
    );
  };

  const fetchData = async () => {
    const res = await fetchFlowPredict({ businessUuid, entity: entityParam });
    setStepData(res);
    setCurrent(res?.length - 1);
  };

  useEffect(() => {
    if (businessUuid && entityParam) {
      fetchData();
    }
  }, [businessUuid, entityParam]);

  return (
    <div>
      <div className='predictTitle'>提交流程</div>
      <div className='predictContent' style={{ maxHeight: '65vh', overflowY: 'auto' }}>
        <ProcessFlow data={stepData} />
      </div>
    </div>
  );
};
export default FlowPredict;
