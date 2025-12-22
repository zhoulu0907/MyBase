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
import XFileUpload from './FileUpload';
import XAutoCode from './AutoCode';
import XDeptSelect from './DeptSelect';
import XUserSelect from './UserSelect';
import XSubTable from './SubTable';
import XDataSelect from './DataSelect';

export const FormComp: any = {
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
  XFileUpload,
  XAutoCode,
  XDeptSelect,
  XUserSelect,
  XSubTable,
  XDataSelect,
};

export { XInputText, XInputTextArea, XInputEmail, XInputPhone, XInputNumber, XDatePicker, XDateRangePicker, XDateTimePicker, XTimePicker, XSelectOne, XSelectMutiple, XCheckbox, XRadio, XSwitch, XImgUpload, XCarouselForm, XFileUpload, XAutoCode, XDeptSelect, XUserSelect, XSubTable, XDataSelect };

export type FormComponentType = typeof FormComp;
