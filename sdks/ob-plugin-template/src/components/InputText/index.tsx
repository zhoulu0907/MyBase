// ===== 导入 begin =====
import { Form, Input } from '@arco-design/web-react'
import { memo, useEffect, useMemo, useState } from 'react'
import { genId, WIDTH_VALUES, WIDTH_OPTIONS } from '@ob/plugin/sdk'
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
    status = 'default',
    defaultValueConfig,
    verify = {},
    align = 'left',
    layout = 'vertical',
    runtime = true,
    detailMode,
    width = WIDTH_VALUES[WIDTH_OPTIONS.HALF]
  } = props || {}
  // ===== 外部 props end =====

  // ===== 内部状态 & 回显begin =====
  const [fieldId, setFieldId] = useState('')

  useEffect(() => {
    if (Array.isArray(dataField) && dataField.length > 0) {
      setFieldId(dataField[dataField.length - 1] as string)
    }
  }, [dataField])

  const initial = useMemo(() => {
    if (defaultValueConfig?.type === 'CUSTOM') return defaultValueConfig?.customValue ?? ''
    return ''
  }, [defaultValueConfig])
  // ===== 内部状态 & 回显 end =====

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
  const renderRuntime = (isReadonly: boolean) => {
    const hidden = status === 'hidden'
    
    return (
      <Form.Item
        label={
          label?.display && label?.text ? (
            <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
          ) : undefined
        }
        field={fieldId ? fieldId : genId('XInputText')}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={helpers.getRules()}
        hidden={hidden}
        style={{ margin: 0, opacity: status === 'hidden' ? 0.4 : 1 }}
        initialValue={initial}
      >
        {isReadonly ? (
          <div>{initial || '--'}</div>
        ) : (
          <Input
            prefix={prefix}
            placeholder={placeholder}
            maxLength={verify?.lengthLimit ? verify?.maxLength : undefined}
            style={{ width: '100%', textAlign: align as any }}
          />
        )}
      </Form.Item>
    )
  }

  const renderBuilder = () => {
    return (
       <Form.Item
        label={
          label?.display && label?.text ? (
            <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
          ) : undefined
        }
        field={fieldId ? fieldId : genId('XInputText')}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={helpers.getRules()}
        hidden={false} // Builder always shows
        style={{ margin: 0, opacity: status === 'hidden' ? 0.4 : 1 }}
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

  const isReadonly = status === 'readonly' || !!detailMode

  return (
    <div className="formWrapper" style={{ width, display: 'inline-block', verticalAlign: 'top', paddingRight: 12 }}>
      {runtime ? renderRuntime(isReadonly) : renderBuilder()}
    </div>
  )
})
// ===== 组件定义 end =====

export default PluginInputText
