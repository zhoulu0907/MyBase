import React, { useState, useEffect } from 'react'
import { LoadedPlugin, PluginComponent, CONFIG_TYPES, WIDTH_OPTIONS, STATUS_OPTIONS } from '@ob/plugin/sdk'
import { Layout, Card, Empty, Form, Input, Typography, Divider, Space, Radio, Switch, InputNumber, Checkbox, Select } from '@arco-design/web-react'
import {
  DynamicLabelInputConfig,
  DynamicTooltipInputConfig,
  DynamicTextInputConfig,
  DynamicSwitchInputConfig,
  DynamicWidthRadioConfig,
  DynamicDefaultValueConfig,
  DynamicVerifyConfig,
  DynamicSecurityConfig,
  DynamicSelectDataSourceConfig
} from './mock/components'

interface ComponentDebuggerProps {
  componentKey: string
  component: PluginComponent
  plugin: LoadedPlugin
  sdk: any
}

// Mock implementation of standard setters
const StandardSetters: Record<string, React.FC<any>> = {
  [CONFIG_TYPES.TEXT_INPUT]: ({ value, onChange, label }) => (
    <DynamicTextInputConfig onChange={onChange} value={value} item={{ name: label, key: 'mock' }} />
  ),
  [CONFIG_TYPES.PLACEHOLDER_INPUT]: ({ value, onChange, label = '占位提示' }) => (
    <DynamicTextInputConfig onChange={onChange} value={value} item={{ name: label, key: 'mock' }} />
  ),
  [CONFIG_TYPES.TEXT_AREA_INPUT]: ({ value, onChange, label }) => (
    <DynamicTextInputConfig 
      onChange={onChange} 
      value={value} 
      item={{ name: label, key: 'mock' }} 
      inputType="textarea"
    />
  ),
  [CONFIG_TYPES.TOOLTIP_INPUT]: ({ value, onChange, label = '提示内容' }) => (
    <DynamicTooltipInputConfig onChange={onChange} value={value} item={{ name: label, key: 'mock' }} />
  ),
  [CONFIG_TYPES.NUMBER_INPUT]: ({ value, onChange, label }) => (
    <DynamicTextInputConfig 
      onChange={onChange} 
      value={value} 
      item={{ name: label, key: 'mock' }} 
      inputType="number"
    />
  ),
  [CONFIG_TYPES.SWITCH_INPUT]: ({ value, onChange, label }) => (
    <DynamicSwitchInputConfig onChange={onChange} value={value} item={{ name: label, key: 'mock' }} />
  ),
  [CONFIG_TYPES.SELECT_INPUT]: ({ value, onChange, label, options }) => (
    <Form.Item label={label} style={{ marginBottom: 0 }}>
      <Select
        value={value}
        onChange={onChange}
        options={options?.map((opt: any) => ({ label: opt.label, value: opt.value }))}
        placeholder="请选择"
      />
    </Form.Item>
  ),
  [CONFIG_TYPES.VERIFY]: ({ value, onChange, label = '校验' }) => (
    <DynamicVerifyConfig onChange={onChange} value={value} item={{ name: label, key: 'mock' }} />
  ),
  [CONFIG_TYPES.LABEL_INPUT]: ({ value, onChange, label = '标题' }) => (
    <DynamicLabelInputConfig onChange={onChange} value={value} item={{ name: label, key: 'mock' }} />
  ),
  [CONFIG_TYPES.STATUS_RADIO]: ({ value, onChange, label = '状态' }) => (
    <DynamicWidthRadioConfig 
      onChange={onChange} 
      value={value} 
      item={{ 
        name: label, 
        key: 'mock',
        range: [
          { key: 'default', value: 'default', text: '默认' },
          { key: 'disabled', value: 'disabled', text: '禁用' },
          { key: 'hidden', value: 'hidden', text: '隐藏' },
          { key: 'readonly', value: 'readonly', text: '只读' }
        ]
      }} 
    />
  ),
  [CONFIG_TYPES.WIDTH_RADIO]: ({ value, onChange, label = '宽度' }) => (
    <DynamicWidthRadioConfig 
      onChange={onChange} 
      value={value} 
      item={{ 
        name: label, 
        key: 'mock',
        range: [
          { key: '25%', value: '25%', text: '25%' },
          { key: '50%', value: '50%', text: '50%' },
          { key: '75%', value: '75%', text: '75%' },
          { key: '100%', value: '100%', text: '100%' }
        ]
      }} 
    />
  ),
  [CONFIG_TYPES.DEFAULT_VALUE]: ({ value, onChange, label = '默认值' }) => (
    <DynamicDefaultValueConfig onChange={onChange} value={value} item={{ name: label, key: 'mock' }} />
  ),
  [CONFIG_TYPES.FIELD_DATA]: ({ value, onChange, label = '字段绑定' }) => (
    <DynamicTextInputConfig onChange={onChange} value={value} item={{ name: label, key: 'mock' }} />
  ),
  [CONFIG_TYPES.TEXT_ALIGN]: ({ value, onChange, label = '对齐方式' }) => (
    <DynamicWidthRadioConfig 
      onChange={onChange} 
      value={value} 
      item={{ 
        name: label, 
        key: 'mock',
        range: [
          { key: 'left', value: 'left', text: '左对齐' },
          { key: 'center', value: 'center', text: '居中' },
          { key: 'right', value: 'right', text: '右对齐' }
        ]
      }} 
    />
  ),
  [CONFIG_TYPES.FORM_LAYOUT]: ({ value, onChange, label = '布局方式' }) => (
    <DynamicWidthRadioConfig 
      onChange={onChange} 
      value={value} 
      item={{ 
        name: label, 
        key: 'mock',
        range: [
          { key: 'horizontal', value: 'horizontal', text: '水平' },
          { key: 'vertical', value: 'vertical', text: '垂直' }
        ]
      }} 
    />
  ),
  [CONFIG_TYPES.SECURITY]: ({ value, onChange, label = '安全设置' }) => (
    <DynamicSecurityConfig onChange={onChange} value={value} item={{ name: label, key: 'mock' }} />
  ),
  [CONFIG_TYPES.SELECT_DATA_SOURCE]: ({ value, onChange, label = '数据源配置', sdk }) => (
    <DynamicSelectDataSourceConfig onChange={onChange} value={value} item={{ name: label, key: 'mock' }} sdk={sdk} />
  ),
  [CONFIG_TYPES.UPLOAD_COMPRESS]: ({ value, onChange, label = '压缩图片' }) => (
     <DynamicTextInputConfig onChange={onChange} value={value} item={{ name: label, key: 'mock' }} />
  ),
  [CONFIG_TYPES.RADIO_DATA]: ({ value, onChange, label, options }) => (
    <DynamicWidthRadioConfig 
      onChange={onChange} 
      value={value} 
      item={{ 
        name: label, 
        key: 'mock',
        range: options?.map((opt: any) => ({ key: opt.value, value: opt.value, text: opt.label }))
      }} 
    />
  )
}

export const ComponentDebugger: React.FC<ComponentDebuggerProps> = ({ componentKey, component, plugin, sdk }) => {
  const [config, setConfig] = useState<any>(component.schema?.config || {})

  // Reset config when component changes
  useEffect(() => {
    setConfig(component.schema?.config || {})
  }, [componentKey])

  const handleConfigChange = (key: string, value: any) => {
    setConfig((prev: any) => ({
      ...prev,
      [key]: value
    }))
  }

  const renderSetter = (item: any, idx: number) => {
    // Determine type and props based on item structure
    let type = ''
    let key = ''
    let name = ''
    let props = {}

    if (typeof item === 'string') {
      type = item
      // Infer key based on type for common standard types
      if (type === CONFIG_TYPES.LABEL_INPUT) key = 'label'
      else if (type === CONFIG_TYPES.STATUS_RADIO) key = 'status'
      else if (type === CONFIG_TYPES.WIDTH_RADIO) key = 'width'
      else key = type // Fallback
      
      name = key // Simple name
    } else {
      type = item.type
      key = item.key
      name = item.name
      props = item.props || {}
      
      // Check dependency visibility
      if (item.dependency) {
        const { key: depKey, value: depValue } = item.dependency
        if (config[depKey] !== depValue) {
          return null
        }
      }
    }

    // 1. Try to find custom renderer registered in plugin
    const customRenderer: any = Object.values(plugin.configRenderers || {}).find((r: any) => r.type === type)
    if (customRenderer && customRenderer.component) {
      const Setter = customRenderer.component
      return (
        <div key={key || idx} style={{ marginBottom: 16 }}>
          <Setter 
            label={name} 
            value={config[key]} 
            onChange={(v: any) => handleConfigChange(key, v)} 
            sdk={sdk}
            config={config}
            fullConfig={config}
            {...props}
          />
        </div>
      )
    }

    // 2. Try to find standard setter
    const StandardSetter = StandardSetters[type]
    if (StandardSetter) {
      // For SELECT_INPUT (which is mapped to DynamicWidthRadioConfig via RADIO_DATA or similar logic in StandardSetters)
      // We need to ensure options are passed correctly. 
      // In StandardSetters map above: 
      // [CONFIG_TYPES.SELECT_INPUT]: ({ ..., options }) => ...
      // So we just need to pass options from item.options or item.props.options
      const options = item.options || item.props?.options;

      return (
        <div key={key || idx} style={{ marginBottom: 16 }}>
          <StandardSetter 
            label={name} 
            value={config[key]} 
            onChange={(v: any) => handleConfigChange(key, v)} 
            options={options} 
            sdk={sdk}
          />
        </div>
      )
    }

    // 3. Fallback for generic items with key (render simple input)
    if (key) {
      return (
        <Form.Item label={name || key} key={key || idx}>
           <Input 
             value={config[key]} 
             onChange={(v) => handleConfigChange(key, v)} 
             placeholder={`Edit ${key}`}
           />
        </Form.Item>
      )
    }

    return (
      <div key={idx} style={{ padding: 10, background: '#f5f5f5', marginBottom: 10, fontSize: 12, color: '#999' }}>
        Unsupported Setter: {typeof item === 'string' ? item : item.type}
      </div>
    )
  }

  const Comp = component.component

  // Mock value state for controlled components (like PluginOCR)
  const [value, setValue] = useState<any>(null);

  // If the component has `value` and `onChange` in its props (based on config or convention), we should wire them up
  // However, `config` contains props passed to the component. 
  // Let's check if we should intercept value/onChange.
  
  const compProps = { ...config, sdk };
  
  // Special handling for components that might need value/onChange but are not in a real Form
  // We simulate a controlled environment here.
  const handleValueChange = (v: any) => {
    setValue(v);
    console.log('[ComponentDebugger] Value Changed:', v);
  };

  return (
    <Layout style={{ height: '100%', flexDirection: 'row' }}>
      <Layout.Content style={{ padding: 20, flex: 1, overflow: 'auto', display: 'flex', flexDirection: 'column' }}>
        <Typography.Title heading={5}>组件预览: {component.template?.displayName || componentKey}</Typography.Title>
        <div style={{ 
          border: '1px dashed #ccc', 
          padding: 20, 
          minHeight: 200, 
          background: '#fff',
          borderRadius: 4
        }}>
          {Comp ? (
            <Form>
              <Comp 
                {...compProps} 
                value={value} 
                onChange={handleValueChange} 
              />
            </Form>
          ) : (
            <Empty description="No Component Implementation" />
          )}
        </div>
        
        <Divider orientation="left">Current Config Data</Divider>
        <Input.TextArea 
          value={JSON.stringify(config, null, 2)}
          autoSize={{ minRows: 3, maxRows: 10 }}
          readOnly
        />
      </Layout.Content>
      
      <Layout.Sider width={350} style={{ background: '#fff', padding: 20, borderLeft: '1px solid #eee', overflow: 'auto' }}>
        <Typography.Title heading={5}>属性配置</Typography.Title>
        <Form layout="vertical">
          {component.schema?.editData?.map((item: any, idx: number) => renderSetter(item, idx))}
        </Form>
      </Layout.Sider>
    </Layout>
  )
}
