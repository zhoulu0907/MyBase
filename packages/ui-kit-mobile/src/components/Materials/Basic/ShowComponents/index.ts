import XImage from './Image';
import XFile from './File';
import XInfoNotice from './InfoNotice';
import XText from './Text';
import XWebView from './WebView';
import XDivider from './Divider';
import XPlaceholder from './Placeholder';

export const ShowComp: any = {
  XImage,
  XFile,
  XText,
  XInfoNotice,
  XWebView,
  XDivider,
  XPlaceholder
};

export { XImage, XFile, XText, XInfoNotice, XWebView, XDivider, XPlaceholder };

export type FormComponentType = typeof ShowComp;
