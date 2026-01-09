/**
 * BindingsModal（字段绑定规则弹窗）
 *
 * 用途：为当前识别类型配置 OCR 结果字段到表单字段的绑定关系
 * - 单面：使用 `bindings`
 * - 双面：使用 `bindingsFront` 与 `bindingsBack`
 */
import React from 'react'
import { Modal, Table, Select, Empty, Typography, Button } from '@arco-design/web-react'

/**
 * 字段绑定规则弹窗属性
 *
 * visible: 是否可见
 * onOk/onCancel: 确认/取消回调
 * type: 当前识别类型（single 或 both）
 * bindings/bindingsFront/bindingsBack: 各面绑定列表
 * updateBinding/updateBindingFront/updateBindingBack: 列表项更新
 * entityFields/currentEntityName/scopeText/currentTableName: 绑定范围信息与候选字段
 */
interface BindingsModalProps {
  visible: boolean
  onOk: () => void
  onCancel: () => void
  type: string
  bindings: any[]
  bindingsFront: any[]
  bindingsBack: any[]
  updateBinding: (index: number, val: string) => void
  updateBindingFront: (index: number, val: string) => void
  updateBindingBack: (index: number, val: string) => void
  entityFields: any[]
  currentEntityName: string
  scopeText: string
  currentTableName: string
}

const BindingsModal: React.FC<BindingsModalProps> = (props) => {
  const {
    visible,
    onOk,
    onCancel,
    type,
    bindings,
    bindingsFront,
    bindingsBack,
    updateBinding,
    updateBindingFront,
    updateBindingBack,
    entityFields,
    currentEntityName,
    scopeText,
    currentTableName
  } = props

  /**
   * 构造通用列定义
   *
   * @param onChangeFn 绑定字段更新函数（根据正面/反面/单面不同传入）
   * @param title 第一列标题（识别字段/识别字段(正面)/识别字段(反面)）
   */
  const makeColumns = (onChangeFn: (index: number, v: string) => void, title: string) => [
    {
      title,
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
      render: (val: string, _item: any, index: number) => (
        <Select
          value={val}
          onChange={(v) => onChangeFn(index, v)}
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
  ]

  const columns = makeColumns(updateBinding, '识别字段')
  const columnsFront = makeColumns(updateBindingFront, '识别字段(正面)')
  const columnsBack = makeColumns(updateBindingBack, '识别字段(反面)')

  const noData = <Empty description="暂无可用字段" />
  const scrollYSingle = 400
  const scrollYBoth = 300

  return (
    <Modal
      title="数据绑定规则配置"
      visible={visible}
      onOk={onOk}
      onCancel={onCancel}
      style={{ width: 600, zIndex: 2001 }}
      footer={
        <div style={{ textAlign: 'right' }}>
          <Button onClick={onCancel} style={{ marginRight: 8 }}>取消</Button>
          <Button type="primary" onClick={onOk}>确定</Button>
        </div>
      }
    >
      <div style={{ marginBottom: 16 }}>
        <Typography.Text type="secondary">
          当前识别类型: {type}
          ，绑定范围: {currentEntityName || scopeText}
          {currentTableName ? `(${currentTableName})` : ''}
        </Typography.Text>
      </div>

      {type === 'id_card_both' ? (
        <div style={{ display: 'flex', gap: 16 }}>
          <div style={{ flex: 1 }}>
            <Typography.Title heading={6}>正面</Typography.Title>
            <Table
              columns={columnsFront}
              data={bindingsFront}
              pagination={false}
              rowKey="id"
              noDataElement={noData}
              scroll={{ y: scrollYBoth }}
            />
          </div>
          <div style={{ flex: 1 }}>
            <Typography.Title heading={6}>反面</Typography.Title>
            <Table
              columns={columnsBack}
              data={bindingsBack}
              pagination={false}
              rowKey="id"
              noDataElement={noData}
              scroll={{ y: scrollYBoth }}
            />
          </div>
        </div>
      ) : (
        <Table
          columns={columns}
          data={bindings}
          pagination={false}
          rowKey="id"
          noDataElement={noData}
          scroll={{ y: scrollYSingle }}
        />
      )}
    </Modal>
  )
}

export default BindingsModal
