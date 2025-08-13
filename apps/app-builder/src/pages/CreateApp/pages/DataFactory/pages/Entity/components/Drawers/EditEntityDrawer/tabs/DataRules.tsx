import { type EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Button, Collapse, Empty, Message, Spin, Tag } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import React, { useEffect, useState } from 'react';
import { getEntityRules } from '@onebase/app';
import styles from './DataRules.module.less';

// 数据规则接口定义
interface DataRule {
  id: string;
  name: string;
  type: 'required' | 'range' | 'format' | 'custom';
  condition: {
    field: string;
    operator: string;
    value?: string | number;
    minValue?: number;
    maxValue?: number;
  };
  hint: string;
  isActive: boolean;
}

interface DataRulesProps {
  node: EntityNode;
}

const DataRules: React.FC<DataRulesProps> = ({ node }) => {
  const [rules, setRules] = useState<DataRule[]>([]);
  const [loading, setLoading] = useState(false);

  // 获取实体规则数据
  const loadRules = async () => {
    if (!node?.entityId) return;

    setLoading(true);
    try {
      // TODO 分页接口
      const response = await getEntityRules({ entityId: node.entityId });
      console.log('getEntityRules', response);
      if (response?.list?.length > 0) {
        setRules(response);
      }
    } catch (error) {
      console.error('获取数据规则失败:', error);
    } finally {
      setLoading(false);
    }
  };

  // 获取规则类型标签
  const getRuleTypeTag = (type: DataRule['type']) => {
    const typeMap = {
      required: { text: '必填验证', color: 'green' },
      range: { text: '范围验证', color: 'blue' },
      format: { text: '格式验证', color: 'orange' },
      custom: { text: '自定义验证', color: 'purple' }
    };

    const config = typeMap[type];
    return <Tag color={config.color}>{config.text}</Tag>;
  };

  // 格式化条件显示
  const formatCondition = (rule: DataRule) => {
    const { condition } = rule;

    if (rule.type === 'required') {
      return `【${condition.field}】【${condition.operator}】`;
    } else if (rule.type === 'range') {
      return `【${condition.field}】【${condition.operator}】【${condition.minValue}】~【${condition.maxValue}】`;
    }

    return `【${condition.field}】【${condition.operator}】`;
  };

  // 添加新规则
  const handleAddRule = () => {
    Message.info('添加规则功能开发中...');
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
          <Collapse expandIconPosition="right" bordered={false} defaultActiveKey={rules[0].id}>
            {rules.map((rule) => (
              <Collapse.Item
                key={rule.id}
                name={rule.id}
                header={
                  <div className={styles['rule-header']}>
                    <span className={styles['rule-name']}>{rule.name}</span>
                    {getRuleTypeTag(rule.type)}
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
                    <div className={styles.hint}>{rule.hint}</div>
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
