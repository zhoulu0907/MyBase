import { useEffect, useState } from 'react';
import { Avatar, Loading, Steps } from '@arco-design/mobile-react';
import { fetchFlowPredict } from '@onebase/app/src/services/app_runtime';
import './flow.css';

const Step = Steps.Step;

const FlowPredict = ({ businessUuid, entityParam }: any) => {
  const [current, setCurrent] = useState(0);
  const [stepData, setStepData] = useState();
  const [loading, setLoading] = useState(false);

  function renderDescript(value: any) {
    const nameArr: Array<any> = []
    const imgArr: Array<{ img: any, text: string }> = []
    value?.handlers?.forEach((handler: any) => {
      nameArr.push(handler?.handlerName);
      imgArr.push({ img: handler?.avatar, text: handler?.handlerName?.charAt(0) || '' });
    });
    return (
      <div className='predictBox'>
        <Avatar.Group isGroup size='ultra-small' style={{ display: 'flex' }}>
          {imgArr.slice(0, 3).map((item: any, index: number) => {
            return item?.img ? <Avatar key={index}><img className='predictImg' src={item.img} alt='' /></Avatar> : <Avatar>{item?.text}</Avatar>
          })}
          {imgArr.length > 3 && <Avatar key='3' textAvatar='...' />}
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
      <Steps current={current} direction="vertical" iconType="dot" style={{ paddingLeft: '2px' }}>
        {data?.map((item: any, index: number) => {
          if (index === data.length - 1) {
            return <Step key={index} title={renderDescript(item)} />;
          }
          return <Step key={index} title={renderDescript(item)} />;
        })}
      </Steps>
    );
  };

  const fetchData = async () => {
    setLoading(true);
    const res = await fetchFlowPredict({ businessUuid, entity: entityParam });
    setStepData(res);
    // 因为是发起流程，所以它的当前节点永远是第一个
    // setCurrent(res?.length - 1);
    setCurrent(1);
    setTimeout(() => {
      setLoading(false);
    }, 100);
  };

  useEffect(() => {
    if (businessUuid && entityParam) {
      fetchData();
    }
  }, [businessUuid, entityParam]);

  return (
    <div>
      <div className='predictTitle' >提交流程</div>
      <div className='predictContent' style={{ maxHeight: '65vh', overflowY: 'auto' }}>
        {loading ? <Loading type="arc" color='rgb(var(--primary-6))' /> : <ProcessFlow data={stepData} />}
      </div>
    </div>
  );
};
export default FlowPredict;
