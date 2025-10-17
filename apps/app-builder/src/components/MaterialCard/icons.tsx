import CarouselIcon from '@/assets/images/cp/carousel_cp.svg';
import CheckboxIcon from '@/assets/images/cp/checkbox_cp.svg';
import DataSelectIcon from '@/assets/images/cp/data_select_cp.svg';
import DatePickerIcon from '@/assets/images/cp/date_picker_cp.svg';
import DeptSelectIcon from '@/assets/images/cp/dept_select_cp.svg';
import DisplayTextIcon from '@/assets/images/cp/display_text_cp.svg';
import EmailInputIcon from '@/assets/images/cp/email_input_cp.svg';
import NumberInputIcon from '@/assets/images/cp/number_input_cp.svg';
import PhoneInputIcon from '@/assets/images/cp/phone_input_cp.svg';
import PwdInputIcon from '@/assets/images/cp/pwd_input_cp.svg';
import RadioIcon from '@/assets/images/cp/radio_cp.svg';
import ReadonlyIcon from '@/assets/images/cp/readonly_cp.svg';
import RelatedFormIcon from '@/assets/images/cp/related_form_cp.svg';
import RichTextIcon from '@/assets/images/cp/rich_text_cp.svg';
import SelectMutipleIcon from '@/assets/images/cp/select_mutiple_cp.svg';
import SelectOneIcon from '@/assets/images/cp/select_one_cp.svg';
import StaticFileIcon from '@/assets/images/cp/static_file_cp.svg';
import SubTableIcon from '@/assets/images/cp/sub_table_cp.svg';
import SwitchIcon from '@/assets/images/cp/switch_cp.svg';
import TextInputIcon from '@/assets/images/cp/text_input_cp.svg';
import TextareaInputIcon from '@/assets/images/cp/textarea_input_cp.svg';
import TimePickerIcon from '@/assets/images/cp/time_picker_cp.svg';
import FileUploadIcon from '@/assets/images/cp/upload_file_cp.svg';
import ImageUploadIcon from '@/assets/images/cp/upload_image_cp.svg';
import UserSelectIcon from '@/assets/images/cp/user_select_cp.svg';

import CalendarIcon from '@/assets/images/cp/calendar_cp.svg';
import CanvasCardIcon from '@/assets/images/cp/canvas_card_cp.svg';
import CanvasListIcon from '@/assets/images/cp/canvas_list_cp.svg';
import DirectoryIcon from '@/assets/images/cp/directory_cp.svg';
import KanbanIcon from '@/assets/images/cp/kanban_cp.svg';
import ListContainerIcon from '@/assets/images/cp/list_container_cp.svg';
import TableIcon from '@/assets/images/cp/table_cp.svg';
import TimelineIcon from '@/assets/images/cp/timeline_cp.svg';

import DividerIcon from '@/assets/images/cp/divider_cp.svg';
import InfoNoticeIcon from '@/assets/images/cp/info_notice_cp.svg';
import PlaceHolderIcon from '@/assets/images/cp/placeholder_cp.svg';
import StaticImageIcon from '@/assets/images/cp/static_image_cp.svg';
import StaticTextIcon from '@/assets/images/cp/static_text_cp.svg';
import WebComponentIcon from '@/assets/images/cp/web_component_cp.svg';

import ColumnLayoutIcon from '@/assets/images/cp/col_layout_cp.svg';
import ColpaseLayoutIcon from '@/assets/images/cp/colpase_layout_cp.svg';
import TabsLayoutIcon from '@/assets/images/cp/tabs_layout_cp.svg';

export const ICON_Map: Record<string, React.ReactNode> = {
  // 表单组件
  'text_input_cp.svg': <img src={TextInputIcon} />,
  'textarea_input_cp.svg': <img src={TextareaInputIcon} />,
  'carousel_cp.svg': <img src={CarouselIcon} />,
  'number_input_cp.svg': <img src={NumberInputIcon} />,
  'email_input_cp.svg': <img src={EmailInputIcon} />,
  'phone_input_cp.svg': <img src={PhoneInputIcon} />,
  'date_picker_cp.svg': <img src={DatePickerIcon} />,
  'time_picker_cp.svg': <img src={TimePickerIcon} />,
  'radio_cp.svg': <img src={RadioIcon} />,
  'switch_cp.svg': <img src={SwitchIcon} />,
  'checkbox_cp.svg': <img src={CheckboxIcon} />,
  'select_one_cp.svg': <img src={SelectOneIcon} />,
  'select_mutiple_cp.svg': <img src={SelectMutipleIcon} />,
  'user_select_cp.svg': <img src={UserSelectIcon} />,
  'dept_select_cp.svg': <img src={DeptSelectIcon} />,
  'upload_file_cp.svg': <img src={FileUploadIcon} />,
  'upload_image_cp.svg': <img src={ImageUploadIcon} />,
  'readonly_cp.svg': <img src={ReadonlyIcon} />,
  'display_text_cp.svg': <img src={DisplayTextIcon} />,
  'pwd_input_cp.svg': <img src={PwdInputIcon} />,
  'related_form_cp.svg': <img src={RelatedFormIcon} />,
  'rich_text_cp.svg': <img src={RichTextIcon} />,
  'data_select_cp.svg': <img src={DataSelectIcon} />,
  'sub_table_cp.svg': <img src={SubTableIcon} />,

  // 列表组件
  'table_cp.svg': <img src={TableIcon} />,
  'calendar_cp.svg': <img src={CalendarIcon} />,
  'timeline_cp.svg': <img src={TimelineIcon} />,
  'directory_cp.svg': <img src={DirectoryIcon} />,
  'list_container_cp.svg': <img src={ListContainerIcon} />,
  'canvas_list_cp.svg': <img src={CanvasListIcon} />,
  'canvas_card_cp.svg': <img src={CanvasCardIcon} />,

  // 展示组件
  'kanban_cp.svg': <img src={KanbanIcon} />,
  'info_notice_cp.svg': <img src={InfoNoticeIcon} />,
  'display_image_cp.svg': <img src={CarouselIcon} />,
  'static_text_cp.svg': <img src={StaticTextIcon} />,
  'static_image_cp.svg': <img src={StaticImageIcon} />,
  'static_file_cp.svg': <img src={StaticFileIcon} />,
  'web_component_cp.svg': <img src={WebComponentIcon} />,
  'divider_cp.svg': <img src={DividerIcon} />,
  'placeholder_cp.svg': <img src={PlaceHolderIcon} />,

  // 布局组件
  'col_layout_cp.svg': <img src={ColumnLayoutIcon} />,
  'tabs_layout_cp.svg': <img src={TabsLayoutIcon} />,
  'colpase_layout_cp.svg': <img src={ColpaseLayoutIcon} />
};

// TODO 图标更新
export const ICON_Map_By_Type: Record<string, React.ReactNode> = {
  // 表单组件
  XInputText: <img src={TextInputIcon} alt="XInputText" />,
  XInputTextArea: <img src={TextareaInputIcon} alt="XInputTextArea" />,
  XInputNumber: <img src={NumberInputIcon} alt="XInputNumber" />,
  XInputEmail: <img src={EmailInputIcon} alt="XInputEmail" />,
  XInputPhone: <img src={PhoneInputIcon} alt="XInputPhone" />,
  XDatePicker: <img src={DatePickerIcon} alt="XDatePicker" />,
  XDateRangePicker: <img src={DatePickerIcon} alt="XDateRangePicker" />,
  XTimePicker: <img src={TimePickerIcon} alt="XTimePicker" />,
  XRadio: <img src={RadioIcon} alt="XRadio" />,
  XSwitch: <img src={SwitchIcon} alt="XSwitch" />,
  XCheckbox: <img src={CheckboxIcon} alt="XCheckbox" />,
  XSelectOne: <img src={SelectOneIcon} alt="XSelectOne" />,
  XSelectMutiple: <img src={SelectMutipleIcon} alt="XSelectMutiple" />,
  XUserSelect: <img src={UserSelectIcon} alt="XUserSelect" />,
  XDeptSelect: <img src={DeptSelectIcon} alt="XDeptSelect" />,
  XFileUpload: <img src={FileUploadIcon} alt="XFileUpload" />,
  XImgUpload: <img src={ImageUploadIcon} alt="XImgUpload" />,
  XAutoCode: <img src={ReadonlyIcon} alt="XAutoCode" />,
  'display_text_cp.svg': <img src={DisplayTextIcon} alt="XDisplayText" />,
  'pwd_input_cp.svg': <img src={PwdInputIcon} alt="XPwdInput" />,
  XRelatedForm: <img src={RelatedFormIcon} alt="XRelatedForm" />,
  XRichText: <img src={RichTextIcon} alt="XRichText" />,
  XSubTable: <img src={SubTableIcon} alt="XSubTable" />,
  XDataSelect: <img src={DataSelectIcon} alt="XDataSelect" />,
  XCarouselForm: <img src={CarouselIcon} alt="XCarouselForm" />,

  // 列表组件
  XTable: <img src={TableIcon} alt="XTable" />,
  XCalendar: <img src={CalendarIcon} alt="XCalendar" />,
  XTimeline: <img src={TimelineIcon} alt="XTimeline" />,
  XCollapse: <img src={KanbanIcon} alt="XCollapse" />,
  XCarousel: <img src={CarouselIcon} alt="XCarousel" />,
  XList: <img src={CanvasListIcon} alt="XList" />,
  'canvas_card_cp.svg': <img src={CanvasCardIcon} alt="XCanvasCard" />,
  // 'directory_cp.svg': <img src={DirectoryIcon} />,

  // 展示组件
  XInfoNotice: <img src={InfoNoticeIcon} alt="XInfoNotice" />,
  XText: <img src={StaticTextIcon} alt="XText" />,
  XImage: <img src={StaticImageIcon} alt="XImage" />,
  XFile: <img src={StaticFileIcon} alt="XFile" />,
  XWebView: <img src={WebComponentIcon} alt="XWebView" />,
  XDivider: <img src={DividerIcon} alt="XDivider" />,
  XPlaceholder: <img src={PlaceHolderIcon} alt="XPlaceholder" />,

  // 布局组件
  XColumnLayout: <img src={ColumnLayoutIcon} alt="XColumnLayout" />,
  XTabsLayout: <img src={TabsLayoutIcon} alt="XTabsLayout" />,
  XColpaseLayout: <img src={ColpaseLayoutIcon} alt="XColpaseLayout" />
};
