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
  { label: '身份证识别(双面)', value: 'id_card_both' },
  { label: '港澳台通行证识别', value: 'exitentrypermit' },
  { label: '护照识别', value: 'passport' }
];

export const EXIT_ENTRY_PERMIT_TYPES = [
  { label: '港澳居民来往内地通行证', value: 'hk_mc_passport' },
  { label: '台湾居民来往大陆通行证', value: 'tw_passport' },
  { label: '台湾居民居留证', value: 'tw_return_passport' },
  { label: '港澳居民居住证', value: 'hk_mc_return_passport' }
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
  ],
  exitentrypermit: [
     { key: 'name', label: '姓名' },
     { key: 'name_en', label: '英文姓名' },
     { key: 'birthday', label: '出生日期' },
     { key: 'sex', label: '性别' },
     { key: 'valid_to', label: '有效期限' },
     { key: 'number', label: '证件号码' },
     { key: 'issue_authority', label: '签发机关' }
  ],
  passport: [
    { key: 'country_code', label: '国家码' },
    { key: 'name', label: '姓名' },
    { key: 'name_en', label: '英文姓名' },
    { key: 'sex', label: '性别' },
    { key: 'birthday', label: '出生日期' },
    { key: 'valid_to', label: '有效期至' },
    { key: 'number', label: '护照号码' },
    { key: 'issue_place', label: '签发地点' },
    { key: 'issue_date', label: '签发日期' }
  ]
};
