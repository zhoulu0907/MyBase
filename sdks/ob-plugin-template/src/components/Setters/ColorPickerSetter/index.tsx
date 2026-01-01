/**
 * ColorPickerSetter（颜色选择配置器）
 *
 * 作用：在搭建器中提供基础颜色选择能力。
 * 说明：
 * - 左侧为预设色，右侧为自由取色器
 * - `value/onChange` 采用受控模式，由宿主驱动
 */
import { Radio, Form, ColorPicker } from '@arco-design/web-react'

const ColorPickerSetter = (props: any) => {
  const { value, onChange, label } = props
  
  const colors = [
    { label: '默认', value: 'inherit' },
    { label: '红色', value: 'red' },
    { label: '蓝色', value: 'blue' },
    { label: '绿色', value: 'green' }
  ]

  return (
    <Form.Item label={label}>
      <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
        <Radio.Group
          type="button"
          value={value}
          onChange={onChange}
          options={colors}
          style={{ width: '100%' }}
        />
        <ColorPicker
          showText={!!value}
          value={value}
          onChange={onChange}
        />
      </div>
    </Form.Item>
  )
}

export default ColorPickerSetter
