/**
 * 插件首页页面（HomePage）
 *
 * 作用：演示如何在插件内部使用宿主提供的 SDK 与示例组件。
 * 关键点：
 * - 通过 `sdk.context.storage` 读写插件私有存储
 * - 通过 `sdk.ui.notify` 进行提示
 * - 通过 `sdk.context.events.emit` 触发宿主事件
 */
import { Card, Button, Space, Typography, Form } from '@arco-design/web-react'
import type { HostSDK } from '@ob/plugin/sdk'
type ExtendedSDK = HostSDK & { ui: any; context: any }
import PluginInputText from '../components/InputText'

const { Title, Text } = Typography

export const HomePage = ({ sdk }: { sdk: ExtendedSDK }) => {
  const handleCallSDK = () => {
    // 向宿主提供的存储写入并读取数据
    sdk.context.storage.set('plugin-demo-key', { time: new Date().toISOString() })
    const data = sdk.context.storage.get('plugin-demo-key')
    // 使用宿主的通知能力进行提示
    sdk.ui.notify('success', `SDK调用成功：${JSON.stringify(data)}`)
  }
  return (
    <div>
      <Title heading={2}>示例插件首页</Title>
      <Text>这是一个完全隔离的插件，不依赖主工程代码</Text>
      {/* 已移除 Demo 组件示例 */}
      <Card style={{ margin: '20px 0' }}>
        <Title heading={4}>XInputText（物料对齐）</Title>
        <Form>
          <PluginInputText
            label={{ text: '单行文本', display: true }}
            placeholder="请输入文本"
            tooltip=""
            dataField={[]}
            defaultValueConfig={{ type: 'CUSTOM', customValue: '', formulaValue: '' }}
            verify={{ required: false, lengthLimit: true, minLength: 0, maxLength: 20 }}
            status="default"
            align="left"
            layout="vertical"
            runtime
            detailMode={false}
          />
        </Form>
      </Card>
      <Space>
        <Button onClick={handleCallSDK}>调用平台SDK</Button>
        <Button type="primary" onClick={() => sdk.context.events.emit('plugin-demo-event', '来自插件的事件')}>触发平台事件</Button>
      </Space>
    </div>
  )
}
