import { type EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Collapse, Empty, Spin, Tag } from '@arco-design/web-react';
import React, { useEffect, useState } from 'react';
import { getEntityRules } from '@onebase/app';
import { validationTypeMap } from '../../../Modals/CreateEditRuleModal/rule';
import styles from './DataRules.module.less';

// 数据规则接口定义
interface DataRule {
  id: string;
  rgName: string;
  validationType: string;
  validationItems: string[];
  condition: {
    field: string;
    operator: string;
    value?: string | number;
    minValue?: number;
    maxValue?: number;
  };
  errorMessage: string;
  isActive: boolean;
}

interface DataRulesProps {
  node: EntityNode;
}

const DataRules: React.FC<DataRulesProps> = ({ node }) => {
  const [rules, setRules] = useState<DataRule[]>([]);
  const [loading, setLoading] = useState(false);

  // 获取资产规则数据
  const loadRules = async () => {
    if (!node?.entityId) return;

    setLoading(true);
    try {
      const response = await getEntityRules({ entityId: node.entityId });
      console.log('getEntityRules', response);
      if (response?.list?.length > 0) {
        setRules(response.list);
      }
    } catch (error) {
      console.error('获取数据规则失败:', error);
    } finally {
      setLoading(false);
    }
  };

  // 获取规则类型标签
  // const getRuleTypeTag = (type: DataRule['type']) => {
  //   const typeMap = {
  //     required: { text: '必填验证', color: 'green' },
  //     range: { text: '范围验证', color: 'blue' },
  //     format: { text: '格式验证', color: 'orange' },
  //     custom: { text: '自定义验证', color: 'purple' }
  //   };

  //   const config = typeMap[type];
  //   return <Tag color={config.color}>{config.text}</Tag>;
  // };

  // 格式化条件显示
  const formatCondition = (rule: DataRule) => {
    const { validationItems, validationType } = rule;

    const cond = validationItems.map((item: string) => {
      return `【${item}】【${validationTypeMap[validationType].slice(0, -2)}】`;
    });

    return cond;
  };

  useEffect(() => {
    if (node?.entityId) {
      loadRules();
    }
  }, [node?.entityId]);

  return (
    <div className={styles['data-rules']}>
      <div className={styles.header}>
        <h3>数据规则</h3>
      </div>

      <div className={styles.content}>
        {loading ? (
          <div className={styles.loading}>
            <Spin size={24} />
            <span>加载中...</span>
          </div>
        ) : rules.length === 0 ? (
          <Empty description="暂无数据规则" />
        ) : (
          <Collapse expandIconPosition="right" bordered={false} defaultActiveKey={rules[0]?.id || ''}>
            {rules.length > 0 &&
              rules.map((rule) => (
                <Collapse.Item
                  key={rule.id}
                  name={rule.id}
                  header={
                    <div className={styles['rule-header']}>
                      <span className={styles['rule-name']}>{rule.rgName}</span>
                      {/* {getRuleTypeTag(rule.type)} */}
                    </div>
                  }
                  className={styles['rule-item']}
                >
                  <div className={styles['rule-content']}>
                    <div className={styles['condition-section']}>
                      <div className={styles.label}>条件设置:</div>
                      <div className={styles.condition}>{formatCondition(rule)}</div>
                    </div>
                    <div className={styles['hint-section']}>
                      <div className={styles.label}>提示:</div>
                      <div className={styles.hint}>{rule.errorMessage}</div>
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

export default DataRules;
