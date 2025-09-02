# 公式编辑器 (FormulaEditor)

一个用于编辑数学公式的弹窗组件，支持字段选择、函数插入和公式编辑。

## 功能特性

- 🎯 **弹窗形式**：以 Modal 弹窗形式展示，不占用页面空间
- 📝 **公式编辑**：支持文本输入和自动插入字段/函数
- 🔍 **智能搜索**：支持变量和函数的快速搜索
- 📋 **分类管理**：按类别组织变量和函数
- 💡 **使用说明**：内置帮助文档和示例
- 🎨 **响应式设计**：适配不同屏幕尺寸

## 使用方法

### 基础用法

```tsx
import { useState } from 'react';
import { Button } from '@arco-design/web-react';
import { FormulaEditor } from '@/components/FormulaEditor';

function MyComponent() {
  const [visible, setVisible] = useState(false);
  const [formula, setFormula] = useState('');

  const handleOpen = () => setVisible(true);
  const handleCancel = () => setVisible(false);
  const handleConfirm = (newFormula: string) => {
    setFormula(newFormula);
    console.log('新公式:', newFormula);
  };

  return (
    <div>
      <Button onClick={handleOpen}>打开公式编辑器</Button>
      <p>当前公式: {formula}</p>
      
      <FormulaEditor
        visible={visible}
        onCancel={handleCancel}
        onConfirm={handleConfirm}
        initialFormula={formula}
      />
    </div>
  );
}
```

### 高级用法

```tsx
import { FormulaEditor, type Variable, type FunctionItem } from '@/components/FormulaEditor';

// 自定义变量和函数数据
const customVariables: Variable[] = [
  { id: '1', name: '销售额', type: '数值', category: '财务' },
  { id: '2', name: '成本', type: '数值', category: '财务' },
];

const customFunctions: FunctionItem[] = [
  { id: '1', name: 'SUM', description: '求和函数', category: '数学函数' },
  { id: '2', name: 'AVERAGE', description: '平均值函数', category: '数学函数' },
];

// 在组件中使用
<FormulaEditor
  visible={visible}
  onCancel={handleCancel}
  onConfirm={handleConfirm}
  initialFormula=""
/>
```

## API 接口

### FormulaEditor Props

| 参数 | 说明 | 类型 | 默认值 |
|------|------|------|--------|
| visible | 是否显示弹窗 | boolean | false |
| onCancel | 取消回调函数 | () => void | - |
| onConfirm | 确认回调函数 | (formula: string) => void | - |
| initialFormula | 初始公式内容 | string | '' |

### 事件回调

- `onCancel`: 用户点击取消或关闭按钮时触发
- `onConfirm`: 用户点击确定按钮时触发，返回编辑后的公式

## 组件结构

```
FormulaEditor/
├── index.tsx              # 主组件
├── index.module.less      # 主样式
├── components/            # 子组件
│   ├── FormulaInput/      # 公式输入区
│   ├── VariableList/      # 变量列表
│   ├── FunctionList/      # 函数列表
│   ├── InfoPanel/         # 说明面板
│   └── index.ts          # 组件导出
└── README.md              # 使用说明
```

## 样式定制

组件使用 CSS Modules，可以通过修改对应的 `.module.less` 文件来自定义样式：

```less
// 自定义主题色
.formulaEditor {
  :global(.arco-modal-content) {
    border-radius: 12px;
  }
}

// 自定义面板样式
.panelsSection {
  .panel {
    background-color: #fafafa;
    border-radius: 8px;
  }
}
```

## 注意事项

1. **依赖要求**：需要安装 `@arco-design/web-react` 组件库
2. **浏览器兼容**：复制功能使用 `navigator.clipboard` API，需要现代浏览器支持
3. **数据格式**：变量和函数数据需要符合定义的接口格式
4. **样式隔离**：使用 CSS Modules 避免样式冲突

## 扩展功能

- 支持更多数学运算符
- 添加公式验证功能
- 支持公式模板保存
- 集成代码高亮显示
- 添加撤销/重做功能
