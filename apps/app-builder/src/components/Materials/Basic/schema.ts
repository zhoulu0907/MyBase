import XAutoCode from "./FormComponents/AutoCode/schema";
import XCheckbox from "./FormComponents/Checkbox/schema";
import XDatePicker from "./FormComponents/DatePicker/schema";
import XDateRangePicker from "./FormComponents/DateRangePicker/schema";
import XDateTimePicker from "./FormComponents/DateTimePicker/schema";
import XDeptSelect from "./FormComponents/DeptSelect/schema";
import XFileUpload from "./FormComponents/FileUpload/schema";
import XImgUpload from "./FormComponents/ImgUpload/schema";
import XInputEmail from "./FormComponents/InputEmail/schema";
import XInputNumber from "./FormComponents/InputNumber/schema";
import XInputPhone from "./FormComponents/InputPhone/schema";
import XInputText from "./FormComponents/InputText/schema";
import XInputTextArea from "./FormComponents/InputTextArea/schema";
import XRadio from "./FormComponents/Radio/schema";
import XReadonlyBox from "./FormComponents/ReadonlyBox/schema";
import XSelectMutiple from "./FormComponents/SelectMutiple/schema";
import XSelectOne from "./FormComponents/SelectOne/schema";
import XSwitch from "./FormComponents/Switch/schema";
import XTimePicker from "./FormComponents/TimePicker/schema";
import XUserSelect from "./FormComponents/UserSelect/schema";
import XColumnLayout from "./LayoutComponents/ColumnLayout/schema";
import XTable from "./ListComponents/Table/schema";

const baseSchema = {
    XInputText,
    XInputTextArea,
    XInputEmail,
    XInputPhone,
    XInputNumber,
    XDatePicker,
    XDateRangePicker,
    XDateTimePicker,
    XTimePicker,
    XSwitch,
    XRadio,
    XCheckbox,
    XSelectOne,
    XSelectMutiple,
    XReadonlyBox,
    XUserSelect,
    XDeptSelect,
    XFileUpload,
    XImgUpload,
    XTable,
    XAutoCode,

    XColumnLayout,
}

export default baseSchema;