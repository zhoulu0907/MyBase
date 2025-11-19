// ========== 组件导入区域 ==========
import XInputText from './InputText';
import XInputTextArea from './InputTextArea';
import XInputEmail from './InputEmail';
import XInputPhone from './InputPhone';
import XInputNumber from './InputNumber';
import XDatePicker from './DatePicker';
import XDateRangePicker from './DateRangePicker';
import XDateTimePicker from './DateTimePicker';
import XTimePicker from './TimePicker';
import XSelectOne from './SelectOne';
import XSelectMutiple from './SelectMutiple';
import XCheckbox from './Checkbox';
import XRadio from './Radio';
import XSwitch from './Switch';
import XImgUpload from './ImgUpload';
import XCarouselForm from './Carousel';
import FileUpload from './FileUpload';
import XAutoCode from './AutoCode';
import XDeptSelect from './DeptSelect';
import XUserSelect from './UserSelect';
import XSubTable from './SubTable';

// ========== Schema导入区域 ==========
import XInputTextSchema from './InputText/schema';
import XInputTextAreaSchema from './InputTextArea/schema';
import XInputEmailSchema from './InputEmail/schema';
import XInputPhoneSchema from './InputPhone/schema';
import XInputNumberSchema from './InputNumber/schema';
import XDatePickerSchema from './DatePicker/schema';
import XDateRangePickerSchema from './DateRangePicker/schema';
import XDateTimePickerSchema from './DateTimePicker/schema';
import XTimePickerSchema from './TimePicker/schema';
import XSelectOneSchema from './SelectOne/schema';
import XSelectMutipleSchema from './SelectMutiple/schema';
import XCheckboxSchema from './Checkbox/schema';
import XRadioSchema from './Radio/schema';
import XSwitchSchema from './Switch/schema';
import XImgUploadSchema from './ImgUpload/schema';
import XCarouselSchema from './Carousel/schema';
import FileUploadSchema from './FileUpload/schema';
import XAutoCodeSchema from './AutoCode/schema';
import XDeptSelectSchema from './DeptSelect/schema';
import XUserSelectSchema from './UserSelect/schema';
import XSubTableSchema from './SubTable/schema';

// ========== 导出对象 ==========
export const FormComp = {
  XInputText,
  XInputTextArea,
  XInputEmail,
  XInputPhone,
  XInputNumber,
  XDatePicker,
  XDateRangePicker,
  XDateTimePicker,
  XTimePicker,
  XSelectOne,
  XSelectMutiple,
  XCheckbox,
  XRadio,
  XSwitch,
  XImgUpload,
  XCarouselForm,
  FileUpload,
  XAutoCode,
  XDeptSelect,
  XUserSelect,
  XSubTable
} as const;

export const FormSchema = {
  XInputTextSchema,
  XInputTextAreaSchema,
  XInputEmailSchema,
  XInputPhoneSchema,
  XInputNumberSchema,
  XDatePickerSchema,
  XDateRangePickerSchema,
  XDateTimePickerSchema,
  XTimePickerSchema,
  XSelectOneSchema,
  XSelectMutipleSchema,
  XCheckboxSchema,
  XRadioSchema,
  XSwitchSchema,
  XImgUploadSchema,
  XCarouselSchema,
  FileUploadSchema,
  XAutoCodeSchema,
  XDeptSelectSchema,
  XUserSelectSchema,
  XSubTableSchema
} as const;
