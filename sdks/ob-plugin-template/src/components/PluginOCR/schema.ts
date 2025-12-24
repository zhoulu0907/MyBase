import { CONFIG_TYPES, WIDTH_VALUES, WIDTH_OPTIONS } from '@ob/plugin/sdk';
import { OCR_TYPES } from './constants';

export const PluginOCRSchema = {
  schema: {
    editData: [
      // 1. 组件ID (只读)
      {
        type: CONFIG_TYPES.TEXT_INPUT,
        key: 'id',
        name: '组件ID',
        props: {
          readOnly: true,
          copyable: true
        }
      },
      // 显示方式
      {
        type: CONFIG_TYPES.RADIO_DATA,
        key: 'displayMode',
        name: '显示方式',
        range: [
          { key: 'click', value: 'click', text: '点击' },
          { key: 'list', value: 'list', text: '列表' },
          { key: 'card', value: 'card', text: '卡片' }
        ]
      },
      // 识别触发
      {
        type: CONFIG_TYPES.RADIO_DATA,
        key: 'triggerMode',
        name: '识别触发',
        range: [
          { key: 'auto', value: 'auto', text: '自动识别' },
          { key: 'button', value: 'button', text: '点击识别' }
        ]
      },
      // 2. 标题 (控制显示/隐藏 + 文本修改)
      CONFIG_TYPES.LABEL_INPUT,
      // 3. 字段描述
      {
        type: CONFIG_TYPES.TEXT_AREA_INPUT,
        key: 'description',
        name: '字段描述',
        props: {
          placeholder: '请输入字段描述'
        }
      },
      // 4. 图片回显
      {
        type: CONFIG_TYPES.SWITCH_INPUT,
        key: 'previewEnabled',
        name: '图片回显',
        props: {
          checkedText: '开启',
          uncheckedText: '关闭'
        }
      },
      // 5. 识别内容 - 是否指定
      {
        type: CONFIG_TYPES.STATUS_RADIO,
        key: 'recognitionMode',
        name: '识别内容-模式',
        range: [
          { key: 'fixed', value: 'fixed', text: '指定类型' }
          // { key: 'linked', value: 'linked', text: '数据联动' } // 暂不显示
        ]
      },
      // 6. 识别内容 - 识别类型 (仅在 fixed 模式下生效)
      {
        type: CONFIG_TYPES.STATUS_RADIO,
        key: 'recognitionType',
        name: '识别内容-类型',
        range: OCR_TYPES.map((o) => ({ key: String(o.value), value: o.value, text: o.label })),
        // 暂时移除依赖，确保始终显示，避免线上环境依赖判断异常
        // dependency: {
        //   key: 'recognitionMode',
        //   value: 'fixed'
        // }
      },
      // 7. 识别内容 - 数据绑定规则配置 (自定义复杂配置)
      {
        type: 'OCRBindingSetter',
        key: 'bindingRules',
        name: '识别内容-数据绑定'
      },
      // 8. 校验
      CONFIG_TYPES.VERIFY,
      // 9. 显示状态
      CONFIG_TYPES.STATUS_RADIO,
      // 10. 字段宽度
      CONFIG_TYPES.WIDTH_RADIO
    ],
    config: {
      id: '', // Will be generated
      label: { text: '文字识别', display: true },
      description: '图片类型支持PNG、JPG、JPEG、BMP，大小不超过5M',
      previewEnabled: true,
      recognitionMode: 'fixed',
      recognitionType: 'general',
      displayMode: 'click',
      triggerMode: 'button',
      bindingRules: [],
      verify: {
        required: false
      },
      status: 'default',
      width: WIDTH_VALUES[WIDTH_OPTIONS.FULL]
    }
  },
  template: {
    h: 36,
    w: 118,
    displayName: 'OCR识别',
    icon: 'ocr_icon.svg', // Assuming an icon exists or default
    category: 'form',
    isPlugin: true
  },
  // Maps to standard UI-Kit capabilities if needed
  fieldMap: ['ocr'],
  entityMap: []
};
