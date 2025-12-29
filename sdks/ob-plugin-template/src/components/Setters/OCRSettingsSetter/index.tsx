import React, { useState, useEffect, useMemo } from 'react';
import { Button, Modal, Table, Select, Typography, Empty, Checkbox, Form, Input, Divider, Space, Card } from '@arco-design/web-react';
import { IconSettings } from '@arco-design/web-react/icon';
import { OCR_FIELDS, OCR_TYPES } from '../../PluginOCR/constants';

interface Props {
  onChange: (value: any) => void;
  value: any; // { recognitionMode, recognitionType, bindingRules: { bindings, bindingsFront, bindingsBack, autoCreate } }
  config: any;
  sdk?: any;
  isInSubTable?: boolean;
}

const OCRSettingsSetter = ({ onChange, value, config, sdk, isInSubTable }: Props) => {
  // Parse value
  const safeValue = value || {};
  const recognitionMode = safeValue.recognitionMode || 'fixed';
  const recognitionType = safeValue.recognitionType || 'id_card_front';
  const bindingRules = safeValue.bindingRules || {};
  const linkConfig = safeValue.linkConfig || { linkField: '', rules: [] };

  // Local state for modal
  const [visible, setVisible] = useState(false);
  const [linkVisible, setLinkVisible] = useState(false);
  const [editingRuleIndex, setEditingRuleIndex] = useState<number | null>(null);
  
  // Binding states
  const [bindings, setBindings] = useState<any[]>([]);
  const [bindingsFront, setBindingsFront] = useState<any[]>([]);
  const [bindingsBack, setBindingsBack] = useState<any[]>([]);
  const [autoCreate, setAutoCreate] = useState(false);
  
  // Entity info
  const [entityFields, setEntityFields] = useState<any[]>([]);
  const [currentTableName, setCurrentTableName] = useState<string>('');
  const [scopeText, setScopeText] = useState<string>('');
  const [currentEntityName, setCurrentEntityName] = useState<string>('');

  const singleSelectFields = useMemo(() => {
    const filtered = (entityFields || []).filter((f: any) => ['SELECT', 'RADIO'].includes(String(f?.fieldType || '').toUpperCase()));
    return filtered;
  }, [entityFields]);

  const currentLinkField = useMemo(() => {
    return (singleSelectFields || []).find((f: any) => f.fieldName === linkConfig?.linkField);
  }, [singleSelectFields, linkConfig]);

  const currentLinkFieldOptions = useMemo(() => {
    // Try to get options from field object directly or from its props
    const rawOptions = currentLinkField?.options || currentLinkField?.props?.options || [];
    console.log('[OCRSettingsSetter] singleSelectFields:', singleSelectFields);
    console.log('[OCRSettingsSetter] currentLinkField:', currentLinkField);
    console.log('[OCRSettingsSetter] rawOptions:', rawOptions);

    const opts = (rawOptions || []).map((o: any) => ({ 
      label: o?.optionLabel || o?.label || o?.text || String(o?.value ?? ''), 
      value: o?.id ?? o?.optionValue ?? o?.value 
    }));
    return Array.isArray(opts) ? opts : [];
  }, [currentLinkField]);

  // Helpers
  const parseBindingRules = (rules: any) => {
    const defaults = {
      bindings: [],
      bindingsFront: [],
      bindingsBack: [],
      autoCreate: false
    };
    if (Array.isArray(rules)) {
      return { ...defaults, bindings: rules };
    }
    if (rules && typeof rules === 'object') {
      return {
        bindings: Array.isArray(rules.bindings) ? rules.bindings : [],
        bindingsFront: Array.isArray(rules.bindingsFront) ? rules.bindingsFront : [],
        bindingsBack: Array.isArray(rules.bindingsBack) ? rules.bindingsBack : [],
        autoCreate: !!rules.autoCreate
      };
    }
    return defaults;
  };

  // Handlers for top-level settings
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

  // --- Binding Logic (similar to OCRBindingSetter) ---

  const refreshEntityFields = () => {
    if (!sdk?.context?.entity?.getFields) return;
    const entities = sdk.context.entity.getEntities?.() || [];
    if (entities.length === 0) {
      setEntityFields([]);
      return;
    }
    const tableNameFromConfig = Array.isArray(config?.dataField) ? config.dataField[0] : '';
    let target = (entities || []).find((e: any) => String(e?.tableName || '') === String(tableNameFromConfig || ''));
    if (!target) {
      if (isInSubTable) {
        target = (entities || []).find((e: any) => e?.isSubEntity) || entities[0];
      } else {
        target = (entities || []).find((e: any) => !e?.isSubEntity) || entities[0];
      }
    }
    if (!target) {
        // Fallback or empty if no entity found
        setEntityFields([]);
        return;
    }
    const fields = sdk.context.entity.getFields(target.entityUuid);
    setEntityFields(Array.isArray(fields) ? fields : []);
    setCurrentTableName(String(target?.tableName || ''));
    setCurrentEntityName(String(target?.entityName || ''));
    setScopeText(String(target?.entityName || (target?.isSubEntity ? '子表' : '主表')));
  };

  useEffect(() => {
    if (visible) refreshEntityFields();
  }, [visible, sdk, config?.dataField, isInSubTable]);

  useEffect(() => {
    if (linkVisible) refreshEntityFields();
  }, [linkVisible, sdk, config?.dataField, isInSubTable]);

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

  // 当前弹窗编辑上下文识别类型与规则
  const editRecognitionType = useMemo(() => {
    return editingRuleIndex !== null
      ? (linkConfig?.rules?.[editingRuleIndex]?.recognitionType || recognitionType)
      : recognitionType;
  }, [editingRuleIndex, linkConfig, recognitionType]);

  const editBindingRules = useMemo(() => {
    return editingRuleIndex !== null
      ? (linkConfig?.rules?.[editingRuleIndex]?.bindingRules || {})
      : bindingRules;
  }, [editingRuleIndex, linkConfig, bindingRules]);

  // Initialize bindings when modal visible based on current edit context
  useEffect(() => {
    if (!visible) return;
    const { bindings: existingBindings, bindingsFront: existingFront, bindingsBack: existingBack, autoCreate: existingAutoCreate } = parseBindingRules(editBindingRules);
    setAutoCreate(existingAutoCreate);

    if (editRecognitionType === 'id_card_both') {
      const fieldsFront = OCR_FIELDS['id_card_front'] || [];
      const fieldsBack = OCR_FIELDS['id_card_back'] || [];

      const existingMapFront = new Map();
      existingFront.forEach((v: any) => {
        if (v && v.ocrField) existingMapFront.set(v.ocrField, v.formField);
      });
      const existingMapBack = new Map();
      existingBack.forEach((v: any) => {
        if (v && v.ocrField) existingMapBack.set(v.ocrField, v.formField);
      });

      const newFront = fieldsFront.map((f: any) => ({
        id: f.key,
        ocrField: f.key,
        ocrLabel: f.label,
        formField: existingMapFront.get(f.key) || ''
      }));
      const newBack = fieldsBack.map((f: any) => ({
        id: f.key,
        ocrField: f.key,
        ocrLabel: f.label,
        formField: existingMapBack.get(f.key) || ''
      }));

      setBindingsFront(newFront);
      setBindingsBack(newBack);
      setBindings([]);
    } else {
      const fields = OCR_FIELDS[editRecognitionType as keyof typeof OCR_FIELDS] || OCR_FIELDS.general;
      const existingMap = new Map();
      existingBindings.forEach((v: any) => {
        if (v && v.ocrField) existingMap.set(v.ocrField, v.formField);
      });
      const newBindings = fields.map((f: any) => ({
        id: f.key,
        ocrField: f.key,
        ocrLabel: f.label,
        formField: existingMap.get(f.key) || ''
      }));
      setBindings(newBindings);
      setBindingsFront([]);
      setBindingsBack([]);
    }
  }, [visible, editRecognitionType, editBindingRules]);

  const updateBinding = (index: number, val: string) => {
    const newBindings = [...bindings];
    newBindings[index].formField = val;
    setBindings(newBindings);
  };
  const updateBindingFront = (index: number, val: string) => {
    const next = [...bindingsFront];
    next[index].formField = val;
    setBindingsFront(next);
  };
  const updateBindingBack = (index: number, val: string) => {
    const next = [...bindingsBack];
    next[index].formField = val;
    setBindingsBack(next);
  };

  const handleSave = () => {
    let newRules;
    const editRecognitionType = editingRuleIndex !== null
      ? (linkConfig?.rules?.[editingRuleIndex]?.recognitionType || recognitionType)
      : recognitionType;
    if (editRecognitionType === 'id_card_both') {
      const validFront = bindingsFront.filter((b) => b.formField);
      const validBack = bindingsBack.filter((b) => b.formField);
      newRules = { bindingsFront: validFront, bindingsBack: validBack, autoCreate };
    } else {
      const validBindings = bindings.filter((b) => b.formField);
      newRules = { bindings: validBindings, autoCreate };
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

  // --- Render Helpers ---

  const getSummary = () => {
    const { bindings: savedBindings, bindingsFront: savedFront, bindingsBack: savedBack } = parseBindingRules(bindingRules);
    if (recognitionType === 'id_card_both') {
      const count = (savedFront?.length || 0) + (savedBack?.length || 0);
      return `已配置 ${count} 个字段绑定`;
    }
    return `已配置 ${savedBindings.length || 0} 个字段绑定`;
  };

  const getCount = () => {
    const { bindings: savedBindings, bindingsFront: savedFront, bindingsBack: savedBack } = parseBindingRules(bindingRules);
    if (recognitionType === 'id_card_both') {
      return (savedFront?.length || 0) + (savedBack?.length || 0);
    }
    return savedBindings?.length || 0;
  };

  const columns = [
    {
      title: '识别字段',
      dataIndex: 'ocrField',
      render: (val: string, item: any) => (
        <div>
          <span style={{ fontWeight: 500 }}>{item.ocrLabel}</span>
          <span style={{ color: '#86909C', marginLeft: 8, fontSize: 12 }}>({val})</span>
        </div>
      )
    },
    {
      title: '绑定数据字段',
      dataIndex: 'formField',
      render: (val: string, item: any, index: number) => (
        <Select
          value={val}
          onChange={v => updateBinding(index, v)}
          placeholder="选择表单字段"
          style={{ width: '100%' }}
          allowClear
        >
          {entityFields.length > 0 &&
            entityFields.map((f: any) => (
              <Select.Option key={f.fieldName} value={f.fieldName}>{f.displayName}</Select.Option>
            ))}
        </Select>
      )
    }
  ];

  const columnsFront = [
    {
      title: '识别字段(正面)',
      dataIndex: 'ocrField',
      render: (val: string, item: any) => (
        <div>
          <span style={{ fontWeight: 500 }}>{item.ocrLabel}</span>
          <span style={{ color: '#86909C', marginLeft: 8, fontSize: 12 }}>({val})</span>
        </div>
      )
    },
    {
      title: '绑定数据字段',
      dataIndex: 'formField',
      render: (val: string, item: any, index: number) => (
        <Select
          value={val}
          onChange={(v) => updateBindingFront(index, v)}
          placeholder="选择表单字段"
          style={{ width: '100%' }}
          allowClear
        >
          {entityFields.length > 0 &&
            entityFields.map((f: any) => (
              <Select.Option key={f.fieldName} value={f.fieldName}>{f.displayName}</Select.Option>
            ))}
        </Select>
      )
    }
  ];

  const columnsBack = [
    {
      title: '识别字段(反面)',
      dataIndex: 'ocrField',
      render: (val: string, item: any) => (
        <div>
          <span style={{ fontWeight: 500 }}>{item.ocrLabel}</span>
          <span style={{ color: '#86909C', marginLeft: 8, fontSize: 12 }}>({val})</span>
        </div>
      )
    },
    {
      title: '绑定数据字段',
      dataIndex: 'formField',
      render: (val: string, item: any, index: number) => (
        <Select
          value={val}
          onChange={(v) => updateBindingBack(index, v)}
          placeholder="选择表单字段"
          style={{ width: '100%' }}
          allowClear
        >
          {entityFields.length > 0 &&
            entityFields.map((f: any) => (
              <Select.Option key={f.fieldName} value={f.fieldName}>{f.displayName}</Select.Option>
            ))}
        </Select>
      )
    }
  ];

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

      <Modal
        title="数据绑定规则配置"
        visible={visible}
        onOk={handleSave}
        onCancel={() => setVisible(false)}
        style={{ width: 600 }}
        footer={
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            {/* <Checkbox checked={autoCreate} onChange={setAutoCreate}>
              自动生成表单元素
            </Checkbox> */}
            <div style={{ display: 'flex', gap: 8 }}>
              <Button onClick={() => setVisible(false)}>取消</Button>
              <Button type="primary" onClick={handleSave}>确定</Button>
            </div>
          </div>
        }
      >
        <div style={{ marginBottom: 16 }}>
           <Typography.Text type="secondary">
            当前识别类型: {OCR_TYPES.find(t => t.value === editRecognitionType)?.label || editRecognitionType}
            ，绑定范围: {currentEntityName || scopeText}{currentTableName ? `(${currentTableName})` : ''}
          </Typography.Text>
        </div>
        
        {editRecognitionType === 'id_card_both' ? (
          <div style={{ display: 'flex', gap: 16 }}>
            <div style={{ flex: 1 }}>
              <Typography.Title heading={6}>正面</Typography.Title>
              <Table
                columns={columnsFront}
                data={bindingsFront}
                pagination={false}
                rowKey="id"
                noDataElement={<Empty description="暂无可用字段" />}
                scroll={{ y: 300 }}
              />
            </div>
            <div style={{ flex: 1 }}>
              <Typography.Title heading={6}>反面</Typography.Title>
              <Table
                columns={columnsBack}
                data={bindingsBack}
                pagination={false}
                rowKey="id"
                noDataElement={<Empty description="暂无可用字段" />}
                scroll={{ y: 300 }}
              />
            </div>
          </div>
        ) : (
          <Table
            columns={columns}
            data={bindings}
            pagination={false}
            rowKey="id"
            noDataElement={<Empty description="暂无可用字段" />}
            scroll={{ y: 400 }}
          />
        )}
      </Modal>

      <Modal
        title="数据联动规则配置"
        visible={linkVisible}
        onOk={() => setLinkVisible(false)}
        onCancel={() => setLinkVisible(false)}
        style={{ width: 880 }}
        footer={
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div />
            <div style={{ display: 'flex', gap: 8 }}>
              <Button onClick={() => setLinkVisible(false)}>取消</Button>
              <Button type="primary" onClick={() => setLinkVisible(false)}>确定</Button>
            </div>
          </div>
        }
      >
        <div style={{ marginBottom: 12 }}>
          <Form.Item label="联动字段" labelCol={{ span: 5 }}>
            <Select
              value={linkConfig?.linkField || ''}
              onChange={updateLinkField}
              placeholder="请选择"
              style={{ width: '100%' }}
              allowClear
            >
              {singleSelectFields.map((f: any) => (
                <Select.Option key={f.fieldName} value={f.fieldName}>{f.displayName}</Select.Option>
              ))}
            </Select>
          </Form.Item>
        </div>
        <Divider style={{ margin: '12px 0' }} />

        {(Array.isArray(linkConfig?.rules) ? linkConfig.rules : []).map((rule: any, idx: number) => (
          <Card key={idx} size="small" bordered style={{ marginBottom: 12 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
              <Typography.Text style={{ fontWeight: 500 }}>规则 {idx + 1}</Typography.Text>
              <Button type="text" status="danger" size="mini" onClick={() => {
                const next = (linkConfig.rules || []).filter((_: any, i: number) => i !== idx);
                onChange({ ...safeValue, linkConfig: { ...linkConfig, rules: next } });
              }}>删除</Button>
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: 'min-content minmax(220px, 1fr) min-content minmax(220px, 1fr) auto', gap: 12, alignItems: 'center' }}>
              <Typography.Text style={{ whiteSpace: 'nowrap' }}>当字段值为</Typography.Text>
              {currentLinkFieldOptions.length > 0 ? (
                <Select
                  placeholder="请选择字段值"
                  value={rule.whenValue}
                  onChange={(v) => updateLinkRule(idx, { whenValue: v })}
                >
                  {currentLinkFieldOptions.map((opt: any) => (
                    <Select.Option key={String(opt.value)} value={opt.value}>{opt.label}</Select.Option>
                  ))}
                </Select>
              ) : (
                <Input placeholder="请输入字段值" value={rule.whenValue} onChange={(v) => updateLinkRule(idx, { whenValue: v })} />
              )}
              <Typography.Text style={{ whiteSpace: 'nowrap' }}>时，识别类型设为</Typography.Text>
              <Select
                placeholder="请选择识别类型"
                value={rule.recognitionType}
                onChange={(v) => updateLinkRule(idx, { recognitionType: v })}
                options={OCR_TYPES}
              />
              <Button size="small" onClick={() => { setEditingRuleIndex(idx); setVisible(true); }}>数据绑定规则配置</Button>
            </div>
          </Card>
        ))}

        <div>
          <Button type="text" onClick={addLinkRule}>+ 添加联动规则</Button>
        </div>
      </Modal>
    </div>
  );
};

export default OCRSettingsSetter;
