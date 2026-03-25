import type { ISchema } from '@formily/react';

/** HTTP 动作创建 - 第三步：动作对外出入参定义（用于调用方只填写少量字段） */
export const step3Schema: ISchema = {
  type: 'object',
  properties: {
    ioTitle: {
      type: 'void',
      title: '动作对外出入参定义',
      'x-component': 'SectionTitle'
    },
    ioGenerateButton: {
      type: 'void',
      'x-component': 'IOGenerateButton'
    },
    ioDesc: {
      type: 'void',
      'x-component': 'div',
      'x-component-props': {
        style: { color: '#666', fontSize: 12, marginBottom: 16 }
      },
      'x-content': '定义动作对外暴露的入参和出参，调用方只需填写这些字段即可调用动作。'
    },
    inputsTitle: {
      type: 'void',
      title: '动作入参',
      'x-component': 'SectionTitle'
    },
    inputs: {
      type: 'array',
      'x-component': 'ActionInputArrayTable'
    },
    outputsTitle: {
      type: 'void',
      title: '动作出参',
      'x-component': 'SectionTitle'
    },
    outputs: {
      type: 'array',
      'x-component': 'ActionOutputArrayTable'
    }
  }
};
