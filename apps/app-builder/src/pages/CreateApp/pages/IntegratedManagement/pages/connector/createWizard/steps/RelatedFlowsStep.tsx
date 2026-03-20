import ResizableTable from '@/components/ResizableTable';
import { useState, useEffect } from 'react';
import { Spin, Empty, Button } from '@arco-design/web-react';
import { useConnectorWizardStore } from '../store';
import styles from '../index.module.less';

const RelatedFlowsStep: React.FC = () => {
  const { nextStep, prevStep } = useConnectorWizardStore();
  const [loading, setLoading] = useState(false);
  const [flows, setFlows] = useState([]);
  const [total, setTotal] = useState(0);

  useEffect(() => {
    fetchRelatedFlows();
  }, []);

  const fetchRelatedFlows = async () => {
    setLoading(true);
    try {
      // TODO: 调用后端 API
      // const res = await getRelatedFlows({
      //   connectorType: connectorType.nodeCode,
      //   appId: getHashQueryParam('appId'),
      // });
      // setFlows(res.list);
      // setTotal(res.total);

      // 临时：空数据
      setFlows([]);
      setTotal(0);
    } catch (error) {
      console.error('获取关联流程失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    { title: '流程名称', dataIndex: 'flowName' },
    { title: '流程状态', dataIndex: 'status' },
    { title: '关联时间', dataIndex: 'relatedAt' },
    { title: '关联人', dataIndex: 'relatedBy' },
  ];

  return (
    <div className={styles.stepContainer}>
      <h3>关联逻辑流</h3>

      <Spin loading={loading} size={40}>
        {flows.length > 0 ? (
          <ResizableTable data={flows} columns={columns} pagination={{ total }} />
        ) : (
          <Empty description="暂无关联的逻辑流" />
        )}
      </Spin>

      <div className={styles.stepFooter}>
        <Button onClick={prevStep}>上一步</Button>
        <Button type="primary" onClick={nextStep}>
          下一步
        </Button>
      </div>
    </div>
  );
};

export default RelatedFlowsStep;
