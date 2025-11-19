import type { IIconBase } from '@icon-park/react/lib/runtime';

export interface DynamicIconProps extends IIconBase {
  IconComponent: React.ComponentType<any>;
  theme?: 'outline' | 'filled' | 'two-tone' | 'multi-color';
  size?: number | string;
  fill?: string;
  style?: React.CSSProperties;
}
export const DynamicIcon = ({ IconComponent, ...rest }: DynamicIconProps) => {
  if (!IconComponent) return null;
  return <IconComponent {...rest} />;
};
