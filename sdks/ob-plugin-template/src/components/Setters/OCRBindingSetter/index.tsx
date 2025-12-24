import React, { useState, useEffect } from 'react';
import { Button, Modal, Table, Select, Typography, Empty, Message, Checkbox } from '@arco-design/web-react';
import { IconSettings } from '@arco-design/web-react/icon';
import { OCR_FIELDS, OCR_TYPES } from '../../PluginOCR/constants';

interface Props {
  onChange: (value: any) => void;
  value: any; // Can be array or object { bindings: [], autoCreate: boolean }
  config: any; // The full component config to access recognitionType
  sdk?: any;
  isInSubTable?: boolean;
}

const OCRBindingSetter = ({ onChange, value, config, sdk, isInSubTable }: Props) => {
  const [visible, setVisible] = useState(false);
  const [bindings, setBindings] = useState<any[]>([]);
  const [bindingsFront, setBindingsFront] = useState<any[]>([]);
  const [bindingsBack, setBindingsBack] = useState<any[]>([]);
  const [autoCreate, setAutoCreate] = useState(false);
  const [entityFields, setEntityFields] = useState<any[]>([]);
  const [currentTableName, setCurrentTableName] = useState<string>('');
  const [scopeText, setScopeText] = useState<string>('');
  const [currentEntityName, setCurrentEntityName] = useState<string>('');

  // Helper to parse value safely
  const parseValue = (val: any) => {
    const defaults = {
      bindings: [],
      bindingsFront: [],
      bindingsBack: [],
      autoCreate: false
    };

    if (Array.isArray(val)) {
      return { ...defaults, bindings: val };
    }
    if (val && typeof val === 'object') {
      return {
        bindings: Array.isArray(val.bindings) ? val.bindings : [],
        bindingsFront: Array.isArray(val.bindingsFront) ? val.bindingsFront : [],
        bindingsBack: Array.isArray(val.bindingsBack) ? val.bindingsBack : [],
        autoCreate: !!val.autoCreate
      };
    }
    return defaults;
  };

  // Get current recognition fields based on type
  const recognitionType = config?.recognitionType || 'general';
  
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
    const sub = sdk?.context?.entity?.subscribe?.(() => {
      if (visible) {
        refreshEntityFields();
      }
    });
    return () => {
      if (typeof sub === 'function') sub();
    };
  }, [sdk, visible]);

  // Initialize bindings when visible or type changes
  useEffect(() => {
    if (visible) {
      const type = config?.recognitionType || 'general';
      const { bindings: existingBindings, bindingsFront: existingFront, bindingsBack: existingBack, autoCreate: existingAutoCreate } = parseValue(value);
      setAutoCreate(existingAutoCreate);

      if (type === 'id_card_both') {
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
        const fields = OCR_FIELDS[type as keyof typeof OCR_FIELDS] || OCR_FIELDS.general;
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
    }
  }, [visible, config?.recognitionType]);

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
    const type = config?.recognitionType || 'general';
    if (type === 'id_card_both') {
      const validFront = bindingsFront.filter((b) => b.formField);
      const validBack = bindingsBack.filter((b) => b.formField);
      onChange({ bindingsFront: validFront, bindingsBack: validBack, autoCreate });
    } else {
      const validBindings = bindings.filter((b) => b.formField);
      onChange({ bindings: validBindings, autoCreate });
    }
    setVisible(false);
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
              <Select.Option key={f.fieldName} value={f.fieldName}>
                {f.displayName}
              </Select.Option>
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
              <Select.Option key={f.fieldName} value={f.fieldName}>
                {f.displayName}
              </Select.Option>
            ))}
        </Select>
      )
    }
  ];

  // Display text for summary
  const getSummary = () => {
    const { bindings: savedBindings, bindingsFront: savedFront, bindingsBack: savedBack } = parseValue(value);
    const type = config?.recognitionType || 'general';
    if (type === 'id_card_both') {
      const count = (savedFront?.length || 0) + (savedBack?.length || 0);
      return `已配置 ${count} 个字段绑定`;
    }
    return `已配置 ${savedBindings.length || 0} 个字段绑定`;
  };

  return (
    <div style={{ padding: '8px 0' }}>
      <div style={{ marginBottom: 8, fontSize: 12, color: '#86909C' }}>
        {getSummary()}
      </div>
      <Button type="secondary" size="small" onClick={() => setVisible(true)}>
        <IconSettings /> 配置绑定规则
      </Button>

      <Modal
        title="数据绑定规则配置"
        visible={visible}
        onOk={handleSave}
        onCancel={() => setVisible(false)}
        style={{ width: 600 }}
        footer={
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Checkbox checked={autoCreate} onChange={setAutoCreate}>
              自动生成表单元素
            </Checkbox>
            <div style={{ display: 'flex', gap: 8 }}>
              <Button onClick={() => setVisible(false)}>取消</Button>
              <Button type="primary" onClick={handleSave}>确定</Button>
            </div>
          </div>
        }
      >
        <div style={{ marginBottom: 16 }}>
           <Typography.Text type="secondary">
            当前识别类型: {OCR_TYPES.find(t => t.value === recognitionType)?.label || recognitionType}
            ，绑定范围: {currentEntityName || scopeText}{currentTableName ? `(${currentTableName})` : ''}
          </Typography.Text>
        </div>
        
        {recognitionType === 'id_card_both' ? (
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
    </div>
  );
};

export default OCRBindingSetter;
