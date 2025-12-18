import {
  AUTO_CODE_NUMBER_MODE,
  AUTO_CODE_RESET_CYCLE,
  AUTO_CODE_RULE_TYPE
} from '@/pages/CreateApp/pages/DataFactory/pages/Entity/components/Modals/ConfigFieldModal/utils/const';
import { findFieldPath } from '@/pages/CreateApp/pages/DataFactory/pages/Entity/components/Modals/ConfigFieldModal/utils/transform';
import { Form, Input, Cascader, Tooltip, Empty } from '@arco-design/web-react';
import { FilterEntityFields, type AppEntity, type AppEntityField } from '@onebase/app';
import { CONFIG_TYPES, getFieldAutoCodeConfig, useAppEntityStore } from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';
import { registerConfigRenderer } from '../../registry';
import styles from '../../index.module.less';

export interface DynamicAutoCodeConfigProps {
  handlePropsChange: (key: string, value: any) => void;
  handleConfigsChange: (config: any) => void;
  item: any;
  configs: any;
  id: string;
}
const DynamicAutoCodeConfig: React.FC<DynamicAutoCodeConfigProps> = ({
  handlePropsChange,
  handleConfigsChange,
  item,
  configs,
  id
}) => {
  const { mainEntity, subEntities } = useAppEntityStore();

  const [displayText, setDisplayText] = useState('');
  const [rules, setRules] = useState<any[]>([]);
  const [entityTree, setEntityTree] = useState<any[]>([]);

  useEffect(() => {
    if (configs.dataField?.length) {
      getAutoCodeConfig();
    } else {
      setRules([]);
    }
  }, [configs.dataField]);

  useEffect(() => {
    if (mainEntity) {
      initTreeData();
    }
  }, [mainEntity]);

  // 获取当前字段配置 通过配置获取编码规则
  const getAutoCodeConfig = async () => {
    const newRules = await getFieldAutoCodeConfig(configs.dataField, mainEntity, subEntities);
    setRules(newRules);
    const sequenceRule = newRules.find((rule) => rule.itemType === AUTO_CODE_RULE_TYPE.SEQUENCE);
    setDisplayText(getDisplayText(sequenceRule));
  };

  const getDisplayText = (sequenceConfig: any) => {
    if (!sequenceConfig) {
      return '';
    }
    const numberingMethodText =
      sequenceConfig.numberMode === AUTO_CODE_NUMBER_MODE.NATURAL ? '自然数编号' : '指定位数编号';
    const digitsText =
      sequenceConfig.numberMode === AUTO_CODE_NUMBER_MODE.NATURAL ? '' : `${sequenceConfig.digitWidth}位数`;
    const resetText = sequenceConfig.resetCycle === AUTO_CODE_RESET_CYCLE.NONE ? '不自动重置' : '自动重置';
    return `${numberingMethodText},${digitsText ? digitsText + ',' : ''}${resetText}`;
  };

  const initTreeData = async () => {
    const mainEntityTree = mainEntity.fields
      .filter((field: AppEntityField) => !FilterEntityFields.includes(field.fieldName))
      .map((field: AppEntityField) => ({
        value: field.id,
        label: field.displayName
      }));

    const subEntityTree = subEntities.entities.map((entity: AppEntity) => ({
      value: entity.entityId,
      label: entity.entityName,
      children: entity.fields
        .filter((field: AppEntityField) => !FilterEntityFields.includes(field.fieldName))
        .map((field: AppEntityField) => ({
          value: field.id,
          label: field.displayName
        }))
    }));

    setEntityTree([
      {
        value: mainEntity.entityId,
        label: mainEntity.entityName,
        children: mainEntityTree
      },
      ...subEntityTree
    ]);
  };

  // 渲染每一项规则
  const renderRuleConfig = (rule: any) => {
    switch (rule.itemType) {
      case AUTO_CODE_RULE_TYPE.SEQUENCE:
        // 自动编号
        return (
          <>
            <span className={styles.autoCodeItemLable}>自动编号:</span>
            <Input value={displayText} readOnly className={styles.ruleInput} />
          </>
        );

      case AUTO_CODE_RULE_TYPE.DATE:
        // 创建时间
        return (
          <>
            <span className={styles.autoCodeItemLable}>创建时间:</span>
            <div className={styles.ruleInput}>
              <Input value={rule.format} readOnly style={{ marginBottom: '4px' }} />
              {rule.format === '自定义' && (
                <Input value={(rule.textValue as string) || ''} readOnly className={styles.ruleInput} />
              )}
            </div>
          </>
        );

      case AUTO_CODE_RULE_TYPE.TEXT:
        // 固定字符
        return (
          <>
            <span className={styles.autoCodeItemLable}>固定字符:</span>
            <Input value={rule.textValue} readOnly className={styles.ruleInput} />
          </>
        );

      case AUTO_CODE_RULE_TYPE.FIELD_REF:
        // 表单字段
        return (
          <>
            <span className={styles.autoCodeItemLable}>表单字段:</span>
            <Cascader
              disabled
              className={styles.ruleInput}
              options={entityTree}
              animation
              value={findFieldPath(rule.format, entityTree)}
            />
          </>
        );
      default:
        return null;
    }
  };

  return (
    <>
      <Form.Item layout="vertical" label={'编号规则配置'} className={styles.formItem}>
        {rules.length ? (
          rules.map((rule: any, index: number) => (
            <Tooltip key={index} content="如需修改请前往数据建模">
              <div className={styles.autoCodeItem}>{renderRuleConfig(rule)}</div>
            </Tooltip>
          ))
        ) : (
          <Empty />
        )}
      </Form.Item>
    </>
  );
};

export default DynamicAutoCodeConfig;

registerConfigRenderer(
  CONFIG_TYPES.AUTO_CODE_RULES,
  ({ id, handlePropsChange, handleConfigsChange, item, configs }) => (
    <DynamicAutoCodeConfig
      id={id}
      handlePropsChange={handlePropsChange}
      handleConfigsChange={handleConfigsChange}
      item={item}
      configs={configs}
    />
  )
);
