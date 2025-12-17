import { Input, Button, Space } from '@arco-design/web-react'
import { useState } from 'react'
import type { HostSDK } from '@ob/plugin/sdk'
type ExtendedSDK = HostSDK & { ui: any; context: any }

interface Props {
  message: string
  onUpdate: (value: string) => void
  sdk: ExtendedSDK
}

const DemoComponent = ({ message, onUpdate, sdk }: Props) => {
  const [value, setValue] = useState(message)
  return (
    <Space>
      <Input
        value={value}
        onChange={(e) => setValue((e as any).target.value)}
        placeholder="输入内容"
        style={{ width: 200 }}
      />
      <Button onClick={() => onUpdate(value)}>同步值</Button>
      <Button type="primary" onClick={() => sdk.ui.notify('info', `组件值：${value}`)}>提示值</Button>
    </Space>
  )
}

export default DemoComponent
