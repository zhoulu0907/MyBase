/**
 * LinkedRulesModal（联动规则配置弹窗）
 *
 * 用途：当选择联动模式时，配置联动字段与各分支识别类型及其绑定入口
 */
import React from 'react'
import { Modal, Form, Select, Divider, Card, Typography, Button, Input } from '@arco-design/web-react'

interface LinkedRulesModalProps {
  visible: boolean
  onCancel: () => void
  linkConfig: any
  singleSelectFields: any[]
  currentLinkFieldOptions: any[]
  updateLinkField: (val: string) => void
  updateLinkRule: (index: number, patch: any) => void
  addLinkRule: () => void
  onEditRule: (idx: number) => void
  onDeleteRule: (idx: number) => void
  OCR_TYPES: any[]
}

const LinkedRulesModal: React.FC<LinkedRulesModalProps> = (props) => {
  const {
    visible,
    onCancel,
    linkConfig,
    singleSelectFields,
    currentLinkFieldOptions,
    updateLinkField,
    updateLinkRule,
    addLinkRule,
    onEditRule,
    onDeleteRule,
    OCR_TYPES
  } = props

  return (
    <Modal
      title="数据联动规则配置"
      visible={visible}
      onOk={onCancel}
      onCancel={onCancel}
      style={{ width: 880, zIndex: 2000 }}
      footer={
        <div style={{ textAlign: 'right' }}>
          <Button onClick={onCancel} style={{ marginRight: 8 }}>取消</Button>
          <Button type="primary" onClick={onCancel}>确定</Button>
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
              <Select.Option key={f.fieldName} value={f.fieldName}>
                {f.displayName}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
      </div>
      <Divider style={{ margin: '12px 0' }} />

      {(Array.isArray(linkConfig?.rules) ? linkConfig.rules : []).map((rule: any, idx: number) => (
        <Card key={idx} size="small" bordered style={{ marginBottom: 12 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
            <Typography.Text style={{ fontWeight: 500 }}>规则 {idx + 1}</Typography.Text>
            <Button type="text" status="danger" size="mini" onClick={() => onDeleteRule(idx)}>
              删除
            </Button>
          </div>
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'min-content minmax(220px, 1fr) min-content minmax(220px, 1fr) auto',
              gap: 12,
              alignItems: 'center'
            }}
          >
            <Typography.Text style={{ whiteSpace: 'nowrap' }}>当字段值为</Typography.Text>
            {currentLinkFieldOptions.length > 0 ? (
              <Select
                placeholder="请选择字段值"
                value={rule.whenValue}
                onChange={(v) => updateLinkRule(idx, { whenValue: v })}
              >
                {currentLinkFieldOptions.map((opt: any) => (
                  <Select.Option key={String(opt.value)} value={opt.value}>
                    {opt.label}
                  </Select.Option>
                ))}
              </Select>
            ) : (
              <Input
                placeholder="请输入字段值"
                value={rule.whenValue}
                onChange={(v) => updateLinkRule(idx, { whenValue: v })}
              />
            )}
            <Typography.Text style={{ whiteSpace: 'nowrap' }}>时，识别类型设为</Typography.Text>
            <Select
              placeholder="请选择识别类型"
              value={rule.recognitionType}
              onChange={(v) => updateLinkRule(idx, { recognitionType: v })}
              options={OCR_TYPES}
            />
            <Button size="small" onClick={() => onEditRule(idx)}>
              数据绑定规则配置
            </Button>
          </div>
        </Card>
      ))}

      <div>
        <Button type="text" onClick={addLinkRule}>
          + 添加联动规则
        </Button>
      </div>
    </Modal>
  )
}

export default LinkedRulesModal
