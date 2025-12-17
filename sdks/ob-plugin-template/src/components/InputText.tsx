import { Form, Input } from '@arco-design/web-react'
import { memo, useEffect, useMemo, useState } from 'react'

const genId = () => `XInputText_${Math.random().toString(36).slice(2, 8)}`

const XInputText = memo((props: any) => {
  const {
    label,
    dataField = [],
    placeholder,
    tooltip,
    status = 'default',
    defaultValueConfig,
    verify = {},
    align = 'left',
    layout = 'vertical',
    runtime = true,
    detailMode
  } = props || {}

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

  const hidden = runtime && status === 'hidden'
  const readonly = status === 'readonly' || !!detailMode

  return (
    <div className="formWrapper">
      <Form.Item
        label={
          label?.display && label?.text ? (
            <span className={tooltip ? 'tooltipLabelText' : 'labelText'}>{label.text}</span>
          ) : undefined
        }
        field={fieldId ? fieldId : genId()}
        layout={layout}
        tooltip={tooltip}
        labelCol={layout === 'horizontal' ? { span: 10 } : {}}
        rules={[
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
        ]}
        hidden={hidden}
        style={{ margin: 0, opacity: status === 'hidden' ? 0.4 : 1 }}
        initialValue={initial}
      >
        {readonly ? (
          <div>{initial || '--'}</div>
        ) : (
          <Input
            placeholder={placeholder}
            maxLength={verify?.lengthLimit ? verify?.maxLength : undefined}
            style={{ width: '100%', textAlign: align as any, pointerEvents: runtime ? 'unset' : 'none' }}
          />
        )}
      </Form.Item>
    </div>
  )
})

export default XInputText
