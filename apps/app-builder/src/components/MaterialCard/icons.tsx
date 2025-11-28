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

import QuickEntryIcon from '@/assets/images/cp/quick_entry_cp.svg';
import TodoCenterIcon from '@/assets/images/cp/todo_center_cp.svg';
import TodoListIcon from '@/assets/images/cp/todo_list_cp.svg';
import WelcomeCardIcon from '@/assets/images/cp/welcome_card_cp.svg';
import InformationListIcon from '@/assets/images/cp/infomation_list_cp.svg';

export const ICON_Map: Record<string, React.ReactNode> = {
  // 表单组件
  'text_input_cp.svg': <img src={TextInputIcon} alt="XInputText" />,
  'textarea_input_cp.svg': <img src={TextareaInputIcon} alt="XInputTextArea" />,
  'carousel_cp.svg': <img src={CarouselIcon} alt="XCarouselForm" />,
  'number_input_cp.svg': <img src={NumberInputIcon} alt="XInputNumber" />,
  'email_input_cp.svg': <img src={EmailInputIcon} alt="XInputEmail" />,
  'phone_input_cp.svg': <img src={PhoneInputIcon} alt="XInputPhone" />,
  'date_picker_cp.svg': <img src={DatePickerIcon} alt="XDatePicker" />,
  'time_picker_cp.svg': <img src={TimePickerIcon} alt="XTimePicker" />,
  'radio_cp.svg': <img src={RadioIcon} alt="XRadio" />,
  'switch_cp.svg': <img src={SwitchIcon} alt="XSwitch" />,
  'checkbox_cp.svg': <img src={CheckboxIcon} alt="XCheckbox" />,
  'select_one_cp.svg': <img src={SelectOneIcon} alt="XSelectOne" />,
  'select_mutiple_cp.svg': <img src={SelectMutipleIcon} alt="XSelectMutiple" />,
  'user_select_cp.svg': <img src={UserSelectIcon} alt="XUserSelect" />,
  'dept_select_cp.svg': <img src={DeptSelectIcon} alt="XDeptSelect" />,
  'upload_file_cp.svg': <img src={FileUploadIcon} alt="XFileUpload" />,
  'upload_image_cp.svg': <img src={ImageUploadIcon} alt="XImgUpload" />,
  'readonly_cp.svg': <img src={ReadonlyIcon} alt="XAutoCode" />,
  'display_text_cp.svg': <img src={DisplayTextIcon} alt="XDisplayText" />,
  'pwd_input_cp.svg': <img src={PwdInputIcon} alt="XPwdInput" />,
  'related_form_cp.svg': <img src={RelatedFormIcon} alt="XRelatedForm" />,
  'rich_text_cp.svg': <img src={RichTextIcon} alt="XRichText" />,
  'data_select_cp.svg': <img src={DataSelectIcon} alt="XDataSelect" />,
  'sub_table_cp.svg': <img src={SubTableIcon} alt="XSubTable" />,

  // 列表组件
  'table_cp.svg': <img src={TableIcon} alt="XTable" />,
  'calendar_cp.svg': <img src={CalendarIcon} alt="XCalendar" />,
  'timeline_cp.svg': <img src={TimelineIcon} alt="XTimeline" />,
  'directory_cp.svg': <img src={DirectoryIcon} alt="XDirectory" />,
  'list_container_cp.svg': <img src={ListContainerIcon} alt="XListContainer" />,
  'canvas_list_cp.svg': <img src={CanvasListIcon} alt="XCanvasList" />,
  'canvas_card_cp.svg': <img src={CanvasCardIcon} alt="XCanvasCard" />,

  // 展示组件
  'kanban_cp.svg': <img src={KanbanIcon} alt="XKanban" />,
  'info_notice_cp.svg': <img src={InfoNoticeIcon} alt="XInfoNotice" />,
  'display_image_cp.svg': <img src={CarouselIcon} alt="XImage" />,
  'static_text_cp.svg': <img src={StaticTextIcon} alt="XText" />,
  'static_image_cp.svg': <img src={StaticImageIcon} alt="XImage" />,
  'static_file_cp.svg': <img src={StaticFileIcon} alt="XFile" />,
  'web_component_cp.svg': <img src={WebComponentIcon} alt="XWebView" />,
  'divider_cp.svg': <img src={DividerIcon} alt="XDivider" />,
  'placeholder_cp.svg': <img src={PlaceHolderIcon} alt="XPlaceholder" />,

  // 布局组件
  'col_layout_cp.svg': <img src={ColumnLayoutIcon} alt="XColumnLayout" />,
  'tabs_layout_cp.svg': <img src={TabsLayoutIcon} alt="XTabsLayout" />,
  'colpase_layout_cp.svg': <img src={ColpaseLayoutIcon} alt="XColpaseLayout" />,

  // 工作台基础组件
  'quick_entry_cp.svg': <img src={QuickEntryIcon} alt="XQuickEntry" />,
  'todo_center_cp.svg': <img src={TodoCenterIcon} alt="XTodoCenter" />,
  'todo_list_cp.svg': <img src={TodoListIcon} alt="XTodoList" />,
  'welcome_card_cp.svg': <img src={WelcomeCardIcon} alt="XWelcomeCard" />,
  'infomation_list_cp.svg': <img src={InformationListIcon} alt="XInformationList" />
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
  XDateTimePicker: <img src={TimePickerIcon} alt="XDateTimePicker" />,
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
  XColpaseLayout: <img src={ColpaseLayoutIcon} alt="XColpaseLayout" />,

  // 工作台基础组件
  XQuickEntry: <img src={QuickEntryIcon} alt="XQuickEntry" />,
  XTodoCenter: <img src={TodoCenterIcon} alt="XTodoCenter" />,
  XTodoList: <img src={TodoListIcon} alt="XTodoList" />,
  XWelcomeCard: <img src={WelcomeCardIcon} alt="XWelcomeCard" />,
  XInformationList: <img src={InformationListIcon} alt="XInformationList" />
};
