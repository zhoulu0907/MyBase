import XAutoCodeValidate from './FormComponents/AutoCode/validate';
import XCheckboxValidate from './FormComponents/Checkbox/validate';
import XDataSelectValidate from './FormComponents/DataSelect/validate';
import XDatePickerValidate from './FormComponents/DatePicker/validate';
import XDateRangePickerValidate from './FormComponents/DateRangePicker/validate';
import XDateTimePickerValidate from './FormComponents/DateTimePicker/validate';
import XDeptSelectValidate from './FormComponents/DeptSelect/validate';
import XFileUploadValidate from './FormComponents/FileUpload/validate';
import XImgUploadValidate from './FormComponents/ImgUpload/validate';
import XInputEmailValidate from './FormComponents/InputEmail/validate';
import XInputNumberValidate from './FormComponents/InputNumber/validate';
import XInputPhoneValidate from './FormComponents/InputPhone/validate';
import XInputTextValidate from './FormComponents/InputText/validate';
import XInputTextAreaValidate from './FormComponents/InputTextArea/validate';
import XRadioValidate from './FormComponents/Radio/validate';
import XRelatedFormValidate from './FormComponents/RelatedForm/validate';
import XRichTextValidate from './FormComponents/RichTextEditor/validate';
import XSelectMutipleValidate from './FormComponents/SelectMutiple/validate';
import XSelectOneValidate from './FormComponents/SelectOne/validate';
import XStaticTextValidate from './FormComponents/StaticText/validate';
import XSubTableValidate from './FormComponents/SubTable/validate';
import XSwitchValidate from './FormComponents/Switch/validate';
import XTimePickerValidate from './FormComponents/TimePicker/validate';
import XUserSelectValidate from './FormComponents/UserSelect/validate';
import XRateValidate from './FormComponents/Rate/validate';
import XCheckItemValidate from './FormComponents/CheckItem/validate';

import XCollapseLayoutValidate from './LayoutComponents/CollapseLayout/validate';
import XColumnLayoutValidate from './LayoutComponents/ColumnLayout/validate';
import XTabsLayoutValidate from './LayoutComponents/TabsLayout/validate';
import XStepsLayoutValidate from './LayoutComponents/StepsLayout/validate';

import XCalendarValidate from './ListComponents/Calendar/validate';
import XCarouselValidate from './ListComponents/Carousel/validate';
import XCollapseValidate from './ListComponents/Collapse/validate';
import XListValidate from './ListComponents/List/validate';
import XTableValidate from './ListComponents/Table/validate';
import XTimelineValidate from './ListComponents/Timeline/validate';
import XCardValidate from './ListComponents/Card/validate';
import XTreeValidate from './ListComponents/Tree/validate';

import XAlertValidate from './ShowComponents/Alert/validate';
import XCarouselFormValidate from './ShowComponents/Carousel/validate';
import XDividerValidate from './ShowComponents/Divider/validate';
import XFileValidate from './ShowComponents/File/validate';
import XImageValidate from './ShowComponents/Image/validate';
import XInfoNoticeValidate from './ShowComponents/InfoNotice/validate';
import XPlaceholderValidate from './ShowComponents/Placeholder/validate';
import XTextValidate from './ShowComponents/Text/validate';
import XWebViewValidate from './ShowComponents/WebView/validate';

export const baseValidate = {
  XInputText: XInputTextValidate,
  XInputTextArea: XInputTextAreaValidate,
  XInputEmail: XInputEmailValidate,
  XInputPhone: XInputPhoneValidate,
  XInputNumber: XInputNumberValidate,
  XDatePicker: XDatePickerValidate,
  XDateRangePicker: XDateRangePickerValidate,
  XDateTimePicker: XDateTimePickerValidate,
  XTimePicker: XTimePickerValidate,
  XSwitch: XSwitchValidate,
  XRadio: XRadioValidate,
  XCheckbox: XCheckboxValidate,
  XSelectOne: XSelectOneValidate,
  XSelectMutiple: XSelectMutipleValidate,
  XUserSelect: XUserSelectValidate,
  XDeptSelect: XDeptSelectValidate,
  XFileUpload: XFileUploadValidate,
  XImgUpload: XImgUploadValidate,
  XRelatedForm: XRelatedFormValidate,
  XStaticText: XStaticTextValidate,
  XRichText: XRichTextValidate,
  XSubTable: XSubTableValidate,
  XDataSelect: XDataSelectValidate,
  XRate: XRateValidate,
  XCheckItem: XCheckItemValidate,

  XTable: XTableValidate,
  XCalendar: XCalendarValidate,
  XTimeline: XTimelineValidate,
  XAutoCode: XAutoCodeValidate,
  XCarousel: XCarouselValidate,
  XCollapse: XCollapseValidate,
  XList: XListValidate,
  XCard: XCardValidate,
  XTree: XTreeValidate,

  XColumnLayout: XColumnLayoutValidate,
  XCollapseLayout: XCollapseLayoutValidate,
  XTabsLayout: XTabsLayoutValidate,
  XStepsLayout: XStepsLayoutValidate,

  XAlert: XAlertValidate,
  XInfoNotice: XInfoNoticeValidate,
  XImage: XImageValidate,
  XFile: XFileValidate,
  XText: XTextValidate,
  XWebView: XWebViewValidate,
  XDivider: XDividerValidate,
  XPlaceholder: XPlaceholderValidate,
  XCarouselForm: XCarouselFormValidate
}