import CheckboxIcon from "@/assets/images/cp/checkbox_cp.svg";
import ColumnLayoutIcon from "@/assets/images/cp/col_layout_cp.svg";
import DatePickerIcon from "@/assets/images/cp/date_picker_cp.svg";
import DeptSelectIcon from "@/assets/images/cp/dept_select_cp.svg";
import EmailInputIcon from "@/assets/images/cp/email_input_cp.svg";
import NumberInputIcon from "@/assets/images/cp/number_input_cp.svg";
import PhoneInputIcon from "@/assets/images/cp/phone_input_cp.svg";
import RadioIcon from "@/assets/images/cp/radio_cp.svg";
import ReadonlyIcon from "@/assets/images/cp/readonly_cp.svg";
import SelectMutipleIcon from "@/assets/images/cp/select_mutiple_cp.svg";
import SelectOneIcon from "@/assets/images/cp/select_one_cp.svg";
import SwitchIcon from "@/assets/images/cp/switch_cp.svg";
import TextInputIcon from "@/assets/images/cp/text_input_cp.svg";
import TextareaInputIcon from "@/assets/images/cp/textarea_input_cp.svg";
import TimePickerIcon from "@/assets/images/cp/time_picker_cp.svg";
import UploadIcon from "@/assets/images/cp/upload_cp.svg";
import UserSelectIcon from "@/assets/images/cp/user_select_cp.svg";

export const ICON_Map: Record<string, React.ReactNode> = {
    "text_input_cp.svg": <img src={TextInputIcon} />,
    "textarea_input_cp.svg": <img src={TextareaInputIcon} />,
    "number_input_cp.svg": <img src={NumberInputIcon} />,
    "email_input_cp.svg": <img src={EmailInputIcon} />,
    "phone_input_cp.svg": <img src={PhoneInputIcon} />,
    "date_picker_cp.svg": <img src={DatePickerIcon} />,
    "time_picker_cp.svg": <img src={TimePickerIcon} />,
    "radio_cp.svg": <img src={RadioIcon} />,
    "switch_cp.svg": <img src={SwitchIcon} />,
    "checkbox_cp.svg": <img src={CheckboxIcon} />,
    "select_one_cp.svg": <img src={SelectOneIcon} />,
    "select_mutiple_cp.svg": <img src={SelectMutipleIcon} />,
    "user_select_cp.svg": <img src={UserSelectIcon} />,
    "dept_select_cp.svg": <img src={DeptSelectIcon} />,
    "upload_cp.svg": <img src={UploadIcon} />,
    "readonly_cp.svg": <img src={ReadonlyIcon} />,
    "col_layout_cp.svg": <img src={ColumnLayoutIcon} />,
};
