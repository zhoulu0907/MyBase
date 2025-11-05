// ========== 组件导入区域 ==========
import XInputText from './InputText';
import XInputTextArea from './InputTextArea';
import XInputEmail from './InputEmail';
import XInputPhone from './InputPhone';
import XInputNumber from './InputNumber';
import XDatePicker from './DatePicker';
import XTimePicker from './TimePicker';
import XSelectOne from './SelectOne';
import XSelectMutiple from './SelectMutiple';
import XCheckbox from './Checkbox';
import XRadio from './Radio';
import XSwitch from './Switch';

// ========== Schema导入区域 ==========
import XInputTextSchema from './InputText/schema';
import XInputTextAreaSchema from './InputTextArea/schema';
import XInputEmailSchema from './InputEmail/schema';
import XInputPhoneSchema from './InputPhone/schema';
import XInputNumberSchema from './InputNumber/schema';
import XDatePickerSchema from './DatePicker/schema';
import XTimePickerSchema from './TimePicker/schema';
import XSelectOneSchema from './SelectOne/schema';
import XSelectMutipleSchema from './SelectMutiple/schema';
import XCheckboxSchema from './Checkbox/schema';
import XRadioSchema from './Radio/schema';
import XSwitchSchema from './Switch/schema';

// ========== 导出对象 ==========
export const FormComp = {
  XInputText,
  XInputTextArea,
  XInputEmail,
  XInputPhone,
  XInputNumber,
  XDatePicker,
  XTimePicker,
  XSelectOne,
  XSelectMutiple,
  XCheckbox,
  XRadio,
  XSwitch
} as const;

export const FormSchema = {
  XInputTextSchema,
  XInputTextAreaSchema,
  XInputEmailSchema,
  XInputPhoneSchema,
  XInputNumberSchema,
  XDatePickerSchema,
  XTimePickerSchema,
  XSelectOneSchema,
  XSelectMutipleSchema,
  XCheckboxSchema,
  XRadioSchema,
  XSwitchSchema
} as const;
