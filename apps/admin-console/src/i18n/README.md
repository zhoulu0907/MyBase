# 国际化 (i18n) 使用指南

本项目使用 `react-i18next` 实现国际化支持，支持中文和英文两种语言。

## 快速开始

### 1. 在组件中使用翻译

```tsx
import { useTranslation } from 'react-i18next';

const MyComponent = () => {
  const { t } = useTranslation();

  return (
    <div>
      <h1>{t('header.title')}</h1>
      <p>{t('common.loading')}</p>
    </div>
  );
};
```

### 2. 使用自定义 Hook

```tsx
import { useI18n } from '../hooks/useI18n';

const MyComponent = () => {
  const { t, changeLanguage, language } = useI18n();

  return (
    <div>
      <p>当前语言: {language}</p>
      <button onClick={() => changeLanguage('en-US')}>切换到英文</button>
    </div>
  );
};
```

### 3. 使用语言切换器组件

```tsx
import LanguageSwitcher from '../components/LanguageSwitcher';

const Header = () => {
  return (
    <header>
      <h1>ONE BASE</h1>
      <LanguageSwitcher />
    </header>
  );
};
```

## 语言包结构

语言包文件位于 `src/i18n/locales/` 目录下：

- `zh-CN.json` - 中文语言包
- `en-US.json` - 英文语言包

### 语言包格式

```json
{
  "common": {
    "loading": "加载中...",
    "error": "错误"
  },
  "header": {
    "title": "ONE BASE",
    "profile": "个人资料"
  }
}
```

## 添加新的翻译

### 1. 在语言包中添加新的键值

在 `zh-CN.json` 和 `en-US.json` 中添加对应的翻译：

```json
// zh-CN.json
{
  "newFeature": {
    "title": "新功能",
    "description": "这是一个新功能"
  }
}

// en-US.json
{
  "newFeature": {
    "title": "New Feature",
    "description": "This is a new feature"
  }
}
```

### 2. 在组件中使用

```tsx
const { t } = useTranslation();

return (
  <div>
    <h2>{t('newFeature.title')}</h2>
    <p>{t('newFeature.description')}</p>
  </div>
);
```

## 插值使用

### 1. 简单插值

```json
{
  "welcome": "欢迎，{{name}}！"
}
```

```tsx
const { t } = useTranslation();
return <p>{t('welcome', { name: '张三' })}</p>;
```

### 2. 复数形式

```json
{
  "item": "{{count}} 个项目",
  "item_plural": "{{count}} 个项目"
}
```

```tsx
const { t } = useTranslation();
return <p>{t('item', { count: 5 })}</p>;
```

## 格式化工具

### 数字格式化

```tsx
import { formatNumber } from '../utils/i18n';

const { i18n } = useTranslation();
const formattedNumber = formatNumber(1234.56, i18n.language);
```

### 日期格式化

```tsx
import { formatDate } from '../utils/i18n';

const { i18n } = useTranslation();
const formattedDate = formatDate(new Date(), i18n.language);
```

### 货币格式化

```tsx
import { formatCurrency } from '../utils/i18n';

const { i18n } = useTranslation();
const formattedCurrency = formatCurrency(1234.56, i18n.language);
```

## 支持的语言

- `zh-CN` - 中文（简体）
- `en-US` - 英文（美式）

## 配置说明

国际化配置在 `src/i18n/index.ts` 文件中：

- 自动检测浏览器语言
- 支持 localStorage 持久化
- 默认语言为中文
- 开发环境启用调试模式

## 最佳实践

1. **命名规范**：使用点分隔的命名空间，如 `header.title`、`auth.login`
2. **组织结构**：按功能模块组织翻译键
3. **默认值**：为所有翻译键提供默认值
4. **类型安全**：使用 TypeScript 确保翻译键的类型安全
5. **测试**：为关键翻译内容编写测试
