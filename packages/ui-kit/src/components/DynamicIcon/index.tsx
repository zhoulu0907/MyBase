import type { IIconBase } from '@icon-park/react/lib/runtime';

interface DynamicIconProps extends IIconBase {
  IconComponent: React.ComponentType<any>;
  theme?: 'outline' | 'filled' | 'two-tone' | 'multi-color';
  size?: number | string;
  fill?: string;
  style?: React.CSSProperties;
}

const DynamicIcon = ({ IconComponent, ...rest }: DynamicIconProps) => {
  if (!IconComponent) return null;
  return <IconComponent {...rest} />;
};

export default DynamicIcon;
