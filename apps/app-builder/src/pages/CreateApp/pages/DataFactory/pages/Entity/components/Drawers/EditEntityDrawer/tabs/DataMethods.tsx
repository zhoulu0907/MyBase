import { type EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Collapse, Empty, Spin, Tag } from '@arco-design/web-react';
import React, { useEffect, useState } from 'react';
import styles from './DataMethods.module.less';
import { getEntityMethods } from '@onebase/app';

// 数据方法接口定义
interface DataMethod {
  id: string;
  methodName: string;
  methodType: 'create' | 'update' | 'delete' | 'query' | 'custom';
  methodCode: string;
  url: string;
}

interface DataMethodsProps {
  node: EntityNode;
}

const DataMethods: React.FC<DataMethodsProps> = ({ node }) => {
  const [methods, setMethods] = useState<DataMethod[]>([]);
  const [loading, setLoading] = useState(false);

  // 获取资产方法数据
  const loadMethods = async () => {
    if (!node?.entityId) return;

    setLoading(true);
    try {
      const response = await getEntityMethods({ entityId: node.entityId });
      console.log('getEntityMethods', response);
      if (response?.length > 0) {
        setMethods(response);
      }
    } catch (error) {
      console.error('获取数据方法失败:', error);
    } finally {
      setLoading(false);
    }
  };

  // 获取方法类型标签
  const getMethodTypeTag = () => {
    return <Tag color="rgba(var(--primary-6), 1)">系统预设</Tag>;
  };

  useEffect(() => {
    if (node?.entityId) {
      loadMethods();
    }
  }, [node?.entityId]);

  return (
    <div className={styles['data-methods']}>
      <div className={styles.header}>
        <h3>数据方法</h3>
      </div>

      <div className={styles.content}>
        {loading ? (
          <div className={styles.loading}>
            <Spin size={24} />
            <span>加载中...</span>
          </div>
        ) : methods.length === 0 ? (
          <Empty description="暂无数据方法" />
        ) : (
          <Collapse expandIconPosition="right" bordered={false} defaultActiveKey={methods[0].id}>
            {methods.map((method) => (
              <Collapse.Item
                key={method.id || method.methodCode}
                name={method.id || method.methodCode}
                header={
                  <div className={styles['method-header']}>
                    <span className={styles['method-name']}>{method.methodName}</span>
                    {getMethodTypeTag()}
                  </div>
                }
                className={styles['method-item']}
              >
                <div className={styles['method-content']}>
                  <div className={styles['description-section']}>
                    <div className={styles.label}>方法编码:</div>
                    <div className={styles.text}>{method.methodCode}</div>
                  </div>

                  <div className={styles['url-section']}>
                    <div className={styles.label}>URL:</div>
                    <div className={styles.text}>
                      {method.url}
                      {/* {method?.parameters?.map((param, index) => (
                        <div key={index} className={styles.parameter}>
                          <span className={styles['param-name']}>{param.name}</span>
                          <span className={styles['param-type']}>: {param.type}</span>
                          {param.required && (
                            <Tag size="small" color="red">
                              必填
                            </Tag> 
                          )}
                          <span className={styles['param-desc']}> - {param.description}</span>
                        </div>
                      ))} */}
                    </div>
                  </div>
                </div>
              </Collapse.Item>
            ))}
          </Collapse>
        )}
      </div>
    </div>
  );
};

export default DataMethods;
