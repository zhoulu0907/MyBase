/**
 * OCRSettingsSetter（识别模式与联动规则配置器）
 *
 * 用途：
 * - 在搭建器中配置 OCR 组件的识别模式与规则
 * - 固定模式：直接选择识别类型并配置字段绑定
 * - 联动模式：根据某个联动字段的值切换识别类型，并为每个分支配置对应的字段绑定
 *
 * 交互与数据：
 * - 通过宿主 `sdk.context.entity` 获取实体与字段列表用于绑定选择
 * - 绑定规则结构统一为 { bindings | bindingsFront | bindingsBack, autoCreate }
 * - 识别类型涵盖 `id_card_front`（单面）与 `id_card_both`（双面）
 *
 * 建议：
 * - 联动字段优先使用单选型（如下拉/单选）以保证匹配稳定
 * - 将后端的识别类型与字段映射保持在 `PluginOCR/constants.ts` 中，避免分散维护
 */
// ===== 导入 begin =====
import React, { useState, useEffect, useMemo } from 'react';
import { Button, Select, Form } from '@arco-design/web-react';
import { IconSettings } from '@arco-design/web-react/icon';
import { OCR_TYPES } from '../../PluginOCR/constants';
import BindingsModal from './BindingsModal'
import LinkedRulesModal from './LinkedRulesModal'
import { parseBindingRules, getEntityInfo, computeLinkFieldOptions, computeSingleSelectFields, findLinkField, initBindingsForType } from './utils'
// ===== 导入 end =====

// ===== 外部 props 定义 begin =====
interface Props {
  onChange: (value: any) => void;
  value: any; // { recognitionMode, recognitionType, bindingRules: { bindings, bindingsFront, bindingsBack, autoCreate } }
  config: any;
  sdk?: any;
  isInSubTable?: boolean;
}
// ===== 外部 props 定义 end =====

// ===== 组件定义 begin =====
const OCRSettingsSetter = ({ onChange, value, config, sdk, isInSubTable }: Props) => {
  // ===== 外部值解析 begin =====
  const safeValue = value || {};
  const recognitionMode = safeValue.recognitionMode || 'fixed';
  const recognitionType = safeValue.recognitionType || 'id_card_front';
  const bindingRules = safeValue.bindingRules || {};
  const linkConfig = safeValue.linkConfig || { linkField: '', rules: [] };
  const stableBindingRules = useMemo(() => bindingRules, [JSON.stringify(bindingRules)]);
  const stableLinkConfig = useMemo(() => linkConfig, [JSON.stringify(linkConfig)]);
  // ===== 外部值解析 end =====

  // ===== 内部状态 & 回显 begin =====
  const [visible, setVisible] = useState(false);
  const [linkVisible, setLinkVisible] = useState(false);
  const [editingRuleIndex, setEditingRuleIndex] = useState<number | null>(null);
  
  // Binding states
  const [bindings, setBindings] = useState<any[]>([]);
  const [bindingsFront, setBindingsFront] = useState<any[]>([]);
  const [bindingsBack, setBindingsBack] = useState<any[]>([]);
  
  
  // Entity info
  const [entityFields, setEntityFields] = useState<any[]>([]);
  const [currentTableName, setCurrentTableName] = useState<string>('');
  const [scopeText, setScopeText] = useState<string>('');
  const [currentEntityName, setCurrentEntityName] = useState<string>('');
  // ===== 内部状态 & 回显 end =====

  // ===== 方法：帮助方法 begin =====
  const singleSelectFields = useMemo(() => computeSingleSelectFields(entityFields), [entityFields]);

  const currentLinkField = useMemo(() => findLinkField(singleSelectFields, stableLinkConfig?.linkField), [singleSelectFields, stableLinkConfig]);

  const [currentLinkFieldOptions, setCurrentLinkFieldOptions] = useState<any[]>([]);
  useEffect(() => {
    computeLinkFieldOptions(currentLinkField, sdk, currentTableName)
      .then((opts: any[]) => setCurrentLinkFieldOptions(Array.isArray(opts) ? opts : []))
      .catch(() => setCurrentLinkFieldOptions([]));
  }, [sdk, currentLinkField, currentTableName]);

  
  // ===== 方法：帮助方法 end =====

  // ===== 内部事件：顶层设置 begin =====
  const handleModeChange = (val: string) => {
    onChange({
      ...safeValue,
      recognitionMode: val
    });
  };

  const handleTypeChange = (val: string) => {
    onChange({
      ...safeValue,
      recognitionType: val
    });
  };

  const updateLinkField = (val: string) => {
    const next = { ...linkConfig, linkField: val };
    onChange({ ...safeValue, linkConfig: next });
  };

  const addLinkRule = () => {
    const nextRules = Array.isArray(linkConfig.rules) ? [...linkConfig.rules] : [];
    nextRules.push({ whenValue: '', recognitionType: 'id_card_front', bindingRules: {} });
    onChange({ ...safeValue, linkConfig: { ...linkConfig, rules: nextRules } });
  };

  const updateLinkRule = (index: number, patch: any) => {
    const nextRules = Array.isArray(linkConfig.rules) ? [...linkConfig.rules] : [];
    nextRules[index] = { ...(nextRules[index] || {}), ...patch };
    onChange({ ...safeValue, linkConfig: { ...linkConfig, rules: nextRules } });
  };
  // ===== 内部事件：顶层设置 end =====

  // ===== 实体字段刷新 begin =====
  const refreshEntityFields = React.useCallback(() => {
    const info = getEntityInfo(sdk, config, isInSubTable)
    setEntityFields(info.entityFields)
    setCurrentTableName(info.currentTableName)
    setCurrentEntityName(info.currentEntityName)
    setScopeText(info.scopeText)
  }, [sdk, config, isInSubTable]);
  // ===== 实体字段刷新 end =====

  useEffect(() => {
    if (visible || linkVisible) refreshEntityFields();
  }, [visible, linkVisible, refreshEntityFields]);

  useEffect(() => {
    const sub = sdk?.context?.entity?.subscribe?.(() => {
      if (visible) {
        refreshEntityFields();
      }
    });
    return () => {
      if (typeof sub === 'function') sub();
    };
  }, [sdk, visible]);

  // ===== 当前弹窗编辑上下文识别类型与规则 =====
  const editRecognitionType = useMemo(() => {
    return editingRuleIndex !== null
      ? (stableLinkConfig?.rules?.[editingRuleIndex]?.recognitionType || recognitionType)
      : recognitionType;
  }, [editingRuleIndex, stableLinkConfig, recognitionType]);

  const editBindingRules = useMemo(() => {
    return editingRuleIndex !== null
      ? (stableLinkConfig?.rules?.[editingRuleIndex]?.bindingRules || {})
      : stableBindingRules;
  }, [editingRuleIndex, stableLinkConfig, stableBindingRules]);

  // ===== 弹窗打开时初始化绑定列表 =====
  useEffect(() => {
    if (!visible) return;
    const { bindings: existingBindings, bindingsFront: existingFront, bindingsBack: existingBack } = parseBindingRules(editBindingRules);
    const res = initBindingsForType(editRecognitionType as string, existingBindings, existingFront, existingBack)
    setBindings(res.bindings)
    setBindingsFront(res.bindingsFront)
    setBindingsBack(res.bindingsBack)
  }, [visible, editRecognitionType, editBindingRules]);

  const updateListItem = (list: any[], setter: (v: any[]) => void, index: number, val: string) => {
    const next = [...list];
    next[index].formField = val;
    setter(next);
  };
  const updateBinding = (index: number, val: string) => updateListItem(bindings, setBindings, index, val);
  const updateBindingFront = (index: number, val: string) => updateListItem(bindingsFront, setBindingsFront, index, val);
  const updateBindingBack = (index: number, val: string) => updateListItem(bindingsBack, setBindingsBack, index, val);

  // ===== 保存绑定结果 =====
  const handleSave = () => {
    let newRules;
    if (editRecognitionType === 'id_card_both') {
      const validFront = bindingsFront.filter((b) => b.formField);
      const validBack = bindingsBack.filter((b) => b.formField);
      newRules = { bindingsFront: validFront, bindingsBack: validBack };
    } else {
      const validBindings = bindings.filter((b) => b.formField);
      newRules = { bindings: validBindings };
    }
    
    if (editingRuleIndex !== null) {
      const nextRules = Array.isArray(linkConfig.rules) ? [...linkConfig.rules] : [];
      nextRules[editingRuleIndex] = { ...(nextRules[editingRuleIndex] || {}), bindingRules: newRules };
      onChange({ ...safeValue, linkConfig: { ...linkConfig, rules: nextRules } });
    } else {
      onChange({ ...safeValue, bindingRules: newRules });
    }
    setVisible(false);
  };

  // ===== 渲染方法：辅助函数 =====

  const getCount = () => {
    const { bindings: savedBindings, bindingsFront: savedFront, bindingsBack: savedBack } = parseBindingRules(stableBindingRules);
    if (recognitionType === 'id_card_both') {
      return (savedFront?.length || 0) + (savedBack?.length || 0);
    }
    return savedBindings?.length || 0;
  };


  // ===== 渲染方法 begin =====
  return (
    <div style={{ padding: '8px 0' }}>
      <Form.Item label="识别内容">
        <div style={{ display: 'flex', gap: 8, width: '100%' }}>
          <Select
            value={recognitionMode}
            onChange={handleModeChange}
            style={{ flex: 1 }}
            options={[{ label: '指定类型', value: 'fixed' }, { label: '数据联动', value: 'linked' }]}
          />
          <Select
            value={recognitionType}
            onChange={handleTypeChange}
            style={{ flex: 1 }}
            options={OCR_TYPES}
            disabled={recognitionMode !== 'fixed'}
            placeholder={recognitionMode !== 'fixed' ? '数据联动模式下由规则决定' : undefined}
          />
        </div>
      </Form.Item>

      {recognitionMode === 'fixed' && (
        <div style={{ marginBottom: 4 }}>
          <Button type={getCount() > 0 ? 'primary' : 'secondary'} size="small" onClick={() => { setEditingRuleIndex(null); setVisible(true); }}>
            <IconSettings /> 配置字段绑定，已配置 {getCount()} 个
          </Button>
        </div>
      )}

      {recognitionMode !== 'fixed' && (
        <div style={{ marginBottom: 4 }}>
          <Button type="secondary" size="small" onClick={() => setLinkVisible(true)}>
            <IconSettings /> 数据联动规则配置
          </Button>
        </div>
      )}

      <BindingsModal
        visible={visible}
        onOk={handleSave}
        onCancel={() => setVisible(false)}
        type={editRecognitionType}
        bindings={bindings}
        bindingsFront={bindingsFront}
        bindingsBack={bindingsBack}
        updateBinding={updateBinding}
        updateBindingFront={updateBindingFront}
        updateBindingBack={updateBindingBack}
        entityFields={entityFields}
        currentEntityName={currentEntityName}
        scopeText={scopeText}
        currentTableName={currentTableName}
      />

      <LinkedRulesModal
        visible={linkVisible}
        onCancel={() => setLinkVisible(false)}
        linkConfig={linkConfig}
        singleSelectFields={singleSelectFields}
        currentLinkFieldOptions={currentLinkFieldOptions}
        updateLinkField={updateLinkField}
        updateLinkRule={updateLinkRule}
        addLinkRule={addLinkRule}
        onEditRule={(idx) => { setEditingRuleIndex(idx); setVisible(true) }}
        onDeleteRule={(idx) => {
          const next = (linkConfig.rules || []).filter((_: any, i: number) => i !== idx)
          onChange({ ...safeValue, linkConfig: { ...linkConfig, rules: next } })
        }}
        OCR_TYPES={OCR_TYPES}
      />
    </div>
  );
};

export default OCRSettingsSetter;
