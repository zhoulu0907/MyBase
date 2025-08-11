### 1. 变量系统

在 `styles/variables.less` 中定义了完整的变量系统：

- **颜色变量**: 主色、文本色、边框色、背景色等
- **字体变量**: 字体大小、字重等
- **间距变量**: 统一的间距系统
- **布局变量**: 高度、宽度、圆角等
- **阴影变量**: 统一的阴影效果
- **过渡变量**: 统一的动画效果

### 2. Mixin 系统

创建了可复用的 mixin：

- **布局 mixin**: `.flex-center()`, `.flex-between()`, `.flex-column()`
- **文本 mixin**: `.text-ellipsis()`
- **边框 mixin**: `.border-bottom-light()`, `.border-right-light()`
- **组件 mixin**: `.card-style()`, `.header-style()`, `.table-container-style()`
- **交互 mixin**: `.list-item-style()`, `.drawer-style()`, `.button-style()`
