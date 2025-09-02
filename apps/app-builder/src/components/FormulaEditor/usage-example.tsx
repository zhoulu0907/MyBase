import { useState } from 'react';
import { Button, Input, Space, Typography } from '@arco-design/web-react';
import { FormulaEditor } from './index';

/**
 * 公式编辑器使用示例
 * 展示如何在父组件中集成和使用公式编辑器
 */
export function FormulaEditorUsageExample() {
  const [visible, setVisible] = useState(false);
  const [formula, setFormula] = useState('');

  // 打开公式编辑器
  const handleOpenEditor = () => {
    setVisible(true);
  };

  // 关闭公式编辑器
  const handleCloseEditor = () => {
    setVisible(false);
  };

  // 确认公式
  const handleConfirmFormula = (newFormula: string) => {
    setFormula(newFormula);
    setVisible(false);
    console.log('公式已更新:', newFormula);
  };

  return (
    <div style={{ padding: '20px' }}>
      <Typography.Title level={4}>公式编辑器集成示例</Typography.Title>
      
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        {/* 公式显示区域 */}
        <div>
          <Typography.Text strong>当前公式：</Typography.Text>
          <Input.TextArea
            value={formula}
            placeholder="请使用公式编辑器设置公式"
            readOnly
            style={{ marginTop: '8px' }}
          />
        </div>

        {/* 操作按钮 */}
        <Space>
          <Button type="primary" onClick={handleOpenEditor}>
            编辑公式
          </Button>
          <Button onClick={() => setFormula('')}>
            清空公式
          </Button>
        </Space>

        {/* 使用提示 */}
        <Typography.Paragraph type="secondary">
          点击"编辑公式"按钮打开公式编辑器，选择字段和函数构建公式
        </Typography.Paragraph>
      </Space>

      {/* 公式编辑器弹窗 */}
      <FormulaEditor
        visible={visible}
        onCancel={handleCloseEditor}
        onConfirm={handleConfirmFormula}
        initialFormula={formula}
      />
    </div>
  );
}
