// ===== 导入 begin =====
import { Form, Input } from '@arco-design/web-react'
import { memo, useEffect, useMemo, useState } from 'react'
import {
  genId,
  WIDTH_VALUES,
  WIDTH_OPTIONS,
  STATUS_OPTIONS,
  STATUS_VALUES,
  isHidden,
  computeInteractive,
  formItemStyle,
  wrapperStyle
} from '@ob/plugin/sdk'
// ===== 导入 end =====

// ===== 组件定义 begin =====
const PluginInputText = memo((props: any) => {
  // ===== 外部 props begin =====
  const {
    label,
    prefix,
    dataField = [],
    placeholder,
    tooltip,
    status = STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    defaultValueConfig,
    verify = {},
    align = 'left',
    layout = 'vertical',
    runtime = true,
    detailMode,
    width = WIDTH_VALUES[WIDTH_OPTIONS.HALF],
    titleColor = 'inherit'
  } = props || {}
  // ===== 外部 props end =====

  // ===== 表单上下文与字段名与值读取 begin =====
  // 模拟 useFormField 的部分逻辑，获取 fieldName
  const [fieldId, setFieldId] = useState('')

  useEffect(() => {
    if (Array.isArray(dataField) && dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1] as string)
    }
  }, [dataField])
  // ===== 表单上下文与字段名与值读取 end =====

  // ===== 外部事件：选择数据 begin =====
  // 暂无外部事件
  // ===== 外部事件：选择数据 end =====

  // ===== 内部状态 & 回显begin =====
  const initial = useMemo(() => {
    if (defaultValueConfig?.type === 'CUSTOM') return defaultValueConfig?.customValue ?? ''
    return ''
  }, [defaultValueConfig])
  // ===== 内部状态 & 回显 end =====

  // ===== 内部事件 =====
  const internalEvents = {
    // 简单输入框通常由 Form.Item 自动接管 onChange，此处预留结构
  }
  // ===== 内部事件 =====

  // ===== 方法：帮助方法 begin =====
  const helpers = {
    getRules: () => [
      { required: !!verify?.required, message: `${label?.text ?? ''}是必填项` },
      {
        validator: (value: any, callback: (error?: any) => void) => {
          if (verify?.lengthLimit) {
            if (verify?.minLength && value && value.length < verify.minLength) {
              callback(`字数不能小于${verify.minLength}`)
            }
            if (verify?.maxLength && value && value.length > verify.maxLength) {
              callback(`字数不能大于${verify.maxLength}`)
            }
          }
        }
      }
    ]
  }
  // ===== 方法：帮助方法 end =====

  // ===== 渲染方法 begin =====
  const renderInteractiveContent = () => (
    <Input
      prefix={prefix}
      placeholder={placeholder}
      maxLength={verify?.lengthLimit ? verify?.maxLength : undefined}
      style={{ width: '100%', textAlign: align as any }}
    />
  )

  const renderReadonlyContent = () => {
    return <div>{initial || '--'}</div>
  }

  const renderRuntime = (interactive: boolean) => {
    const hidden = isHidden(status)

    return (
      <Form.Item
        label={
          label?.display && label?.text ? (
            <span
              className={tooltip ? 'tooltipLabelText' : 'labelText'}
              style={{ color: titleColor }}
            >
              {label.text}
            </span>
          ) : undefined
        }
        field={fieldId ? fieldId : genId('PluginInputText')}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={helpers.getRules()}
        hidden={hidden}
        style={formItemStyle(status)}
        initialValue={initial}
      >
        {interactive ? renderInteractiveContent() : renderReadonlyContent()}
      </Form.Item>
    )
  }

  const renderBuilder = () => {
    return (
       <Form.Item
        label={
          label?.display && label?.text ? (
            <span
              className={tooltip ? 'tooltipLabelText' : 'labelText'}
              style={{ color: titleColor }}
            >
              {label.text}
            </span>
          ) : undefined
        }
        field={fieldId ? fieldId : genId('PluginInputText')}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={helpers.getRules()}
        hidden={false} // Builder always shows
        style={formItemStyle(status)}
        initialValue={initial}
      >
          <Input
            prefix={prefix}
            placeholder={placeholder}
            maxLength={verify?.lengthLimit ? verify?.maxLength : undefined}
            style={{ width: '100%', textAlign: align as any, pointerEvents: 'none' }}
          />
      </Form.Item>
    )
  }
  // ===== 渲染方法 end =====

  const isInteractive = computeInteractive(status, runtime, detailMode)

  return (
    <div className="formWrapper" style={wrapperStyle(width)}>
      {runtime ? renderRuntime(isInteractive) : renderBuilder()}
    </div>
  )
})
// ===== 组件定义 end =====

export default PluginInputText
