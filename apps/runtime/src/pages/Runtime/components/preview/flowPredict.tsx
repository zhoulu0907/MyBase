import { useEffect, useState, type FC } from 'react';
import { Steps, Avatar } from '@arco-design/web-react';
import { fetchFlowPredict } from '@onebase/app/src/services/app_runtime';
import styles from './index.module.less';

const Step = Steps.Step;

const FlowPredict = ({ businessId, entityParam }:any) => {
  const [current, setCurrent] = useState(0);
  const [stepData, setStepData] = useState();

  function renderDescript(value:any) {
    return (
      <div className={styles.predictBox}>
        <div className={styles.predictImg}>{value?.avatar && <img src={value?.avatar} />}</div>
        <div className={styles.predictText}>
          <div className={styles.predictName}>
            {value?.handlers?.map((handler:any) => handler?.handlerName).join('、') || '暂无处理人'}
          </div>
          <div className={styles.predictDesc}>{value?.nodeName}</div>
        </div>
      </div>
    );
  }

  const ProcessFlow = ({ data }:any) => {
    return (
      <Steps current={current} direction="vertical" type="dot">
        {data?.map((item:any, index:number) => {
          if (index === data.length - 1) {
            return <Step key={index} disabled={true} title={renderDescript(item)} className="predictStepGray" />;
          }
          return <Step key={index} disabled={true} title={renderDescript(item)} className="predictStepSuccess" />;
        })}
      </Steps>
    );
  };

  const fetchData = async () => {
    const res = await fetchFlowPredict({ businessId, entity: entityParam });
    setStepData(res);
    setCurrent(res?.length - 1);
  };

  useEffect(() => {
    if (businessId && entityParam) {
      fetchData();
    }
  }, [businessId, entityParam]);

  return (
    <div>
      <div className={styles.predictTitle}>提交流程</div>
      <div className={styles.predictContent} style={{maxHeight: '65vh', overflowY: 'auto'}}>
        <ProcessFlow data={stepData} />
      </div>
    </div>
  );
};
export default FlowPredict;
