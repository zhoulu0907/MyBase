/**
 * OCR 类型与字段映射
 *
 * 用途：为搭建器与运行态提供识别类型选项与结果字段标准化映射。
 * - `OCR_TYPES` 提供下拉选项
 * - `OCR_FIELDS` 规范各识别类型的结构化结果字段
 */
export const OCR_TYPES = [
  // { label: '通用文本识别', value: 'general' },
  { label: '身份证识别(正面)', value: 'id_card_front' },
  { label: '身份证识别(双面)', value: 'id_card_both' }
];

export const OCR_FIELDS = {
  general: [
    { key: 'content', label: '通用文本信息' }
  ],
  id_card_front: [
    { key: 'name', label: '姓名' },
    { key: 'gender', label: '性别' },
    { key: 'ethnicity', label: '民族' },
    { key: 'birthday', label: '出生' },
    { key: 'address', label: '住址' },
    { key: 'id_number', label: '公民身份号码' }
  ],
  id_card_back: [
    { key: 'issue_authority', label: '签发机关' },
    { key: 'valid_from', label: '签发日期' },
    { key: 'valid_to', label: '失效日期' }
  ]
};
