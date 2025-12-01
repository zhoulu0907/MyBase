import { Button } from '@arco-design/web-react';
import type { ButtonProps } from '@arco-design/web-react/es/Button';
import  PermissionControl, { type PermissionControlProps } from './Common';

interface PermissionButtonProps extends ButtonProps, Omit<PermissionControlProps, 'children'> {}

/**
 * 权限控制按钮组件
 * 基于 Arco Design 的 Button 组件封装，增加了权限控制功能
 * 只有当用户具有指定权限时，按钮才会显示，否则显示 fallback 内容或默认不显示
 * 
 * @example
 * // 基于单个权限控制
 * <PermissionButton permission="system:user:create" type="primary">
 *   新增用户
 * </PermissionButton>
 */
const PermissionButton: React.FC<PermissionButtonProps> = ({
  permission,
  anyPermissions,
  allPermissions,
  children,
  fallback = null,
  ...rest
}) => { 
  return (
    <PermissionControl
      permission={permission}
      anyPermissions={anyPermissions}
      allPermissions={allPermissions}
      fallback={fallback}
    >
      <Button {...rest}>{children}</Button>
    </PermissionControl>
  );
};

export default PermissionButton;