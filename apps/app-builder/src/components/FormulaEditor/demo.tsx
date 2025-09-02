import { useState } from 'react';
import { Button, Card, Space, Typography, Tag } from '@arco-design/web-react';
import { FormulaEditor } from './index';

export function FormulaEditorDemo() {
  const [visible, setVisible] = useState(false);
  const [formula, setFormula] = useState('');

  const handleOpen = () => setVisible(true);
  const handleCancel = () => setVisible(false);
  const handleConfirm = (newFormula: string) => {
    setFormula(newFormula);
    console.log('新公式:', newFormula);
  };

  return (
    <div style={{ padding: '24px' }}>
      <Card title="公式编辑器演示">
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          <div>
            <Typography.Title level={5}>当前公式</Typography.Title>
            <Typography.Text code style={{ fontSize: '16px' }}>
              {formula || '暂无公式'}
            </Typography.Text>
          </div>

          <Space>
            <Button type="primary" onClick={handleOpen}>
              打开公式编辑器
            </Button>
            <Button onClick={() => setFormula('')}>清空公式</Button>
          </Space>

          <div>
            <Typography.Title level={5}>新功能特性</Typography.Title>
            <Typography.Paragraph>公式编辑器现在支持以下新功能：</Typography.Paragraph>
            <ul>
              <li>
                <strong>字段标签样式：</strong>
                <Tag color="green" style={{ marginLeft: '8px' }}>
                  字段名称
                </Tag>
                以绿色标签形式展示，点击可在光标位置插入
              </li>
              <li>
                <strong>公式文字样式：</strong>
                <span
                  style={{
                    color: '#f53f3f',
                    fontWeight: 500,
                    backgroundColor: 'rgba(245, 63, 63, 0.1)',
                    padding: '2px 6px',
                    borderRadius: '3px'
                  }}
                >
                  函数名称
                </span>
                以粉色文字形式展示，点击可在光标位置插入
              </li>
              <li>
                <strong>智能复制粘贴：</strong>
                点击复制按钮可复制公式及数据，支持在其他公式编辑器中粘贴
              </li>
              <li>
                <strong>光标定位插入：</strong>
                点击字段或函数时，会在当前光标位置插入，而不是追加到末尾
              </li>
            </ul>
          </div>

          <div>
            <Typography.Title level={5}>使用说明</Typography.Title>
            <Typography.Paragraph>在公式编辑器中：</Typography.Paragraph>
            <ul>
              <li>在左侧面板选择变量和函数，点击即可在光标位置插入</li>
              <li>使用复制按钮复制当前公式和数据</li>
              <li>在其他公式编辑器中粘贴时，会自动插入公式内容</li>
              <li>在中间面板查看函数说明</li>
              <li>在右侧面板查看使用说明</li>
              <li>点击确定保存公式</li>
            </ul>
          </div>
        </Space>
      </Card>

      <FormulaEditor visible={visible} onCancel={handleCancel} onConfirm={handleConfirm} initialFormula={formula} />
    </div>
  );
}
