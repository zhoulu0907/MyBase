// ========== 组件导入区域 ==========
import XInputText from './InputText';
import XInputTextArea from './InputTextArea';
import XInputEmail from './InputEmail';
import XInputPhone from './InputPhone';
import XInputNumber from './InputNumber';
import XSelectOne from './SelectOne';

// ========== Schema导入区域 ==========
import XInputTextSchema from './InputText/schema';
import XInputTextAreaSchema from './InputTextArea/schema';
import XInputEmailSchema from './InputEmail/schema';
import XInputPhoneSchema from './InputPhone/schema';
import XInputNumberSchema from './InputNumber/schema';
import XSelectOneSchema from './SelectOne/schema';

// ========== 导出对象 ==========
export const FormComp = {
  XInputText,
  XInputTextArea,
  XInputEmail,
  XInputPhone,
  XInputNumber,
  XSelectOne
} as const;

export const FormSchema = {
  XInputTextSchema,
  XInputTextAreaSchema,
  XInputEmailSchema,
  XInputPhoneSchema,
  XInputNumberSchema,
  XSelectOne
} as const;
