import { useEffect, useState, type FC } from 'react';
import { Steps, Avatar } from '@arco-design/web-react';
import { fetchFlowPredict } from '@onebase/app/src/services/app_runtime';
import styles from './index.module.less';

const Step = Steps.Step;

const FlowPredict = ({ businessId }) => {
  const [current, setCurrent] = useState(0);
  const [stepData, setStepData] = useState();

  function renderDescript(value) {
    return (
      <div className={styles.predictBox}>
        <div className={styles.predictImg}>{value?.avatar && <img src={value?.avatar} />}</div>
        <div className={styles.predictText}>
          <div className={styles.predictName}>
            {value?.handlers?.map((handler) => handler?.handlerName).join('、') || '暂无处理人'}
          </div>
          <div className={styles.predictDesc}>{value?.nodeName}</div>
        </div>
      </div>
    );
  }

  const ProcessFlow = ({ data }) => {
    return (
      <Steps current={current} direction="vertical" type="dot">
        {data?.map((item, index) => {
          if (index === data.length - 1) {
            return <Step key={index} disabled={true} title={renderDescript(item)} className="predictStepGray" />;
          }
          return <Step key={index} disabled={true} title={renderDescript(item)} className="predictStepSuccess" />;
        })}
      </Steps>
    );
  };

  const fetchData = async () => {
    const res = await fetchFlowPredict({ businessId });
    setStepData(res);
    setCurrent(res?.length - 1);
  };

  useEffect(() => {
    fetchData();
  }, []);

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
