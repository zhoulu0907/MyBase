export const OCR_TYPES = [
  { label: '通用文本识别', value: 'general' },
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
    { key: 'valid_period', label: '有效期限' }
  ]
};
