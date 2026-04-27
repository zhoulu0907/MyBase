// import { useState, useEffect } from 'react';
import { Input } from '@arco-design/web-react';

interface IProps {
  defaultValue?: string;
  placeholder?: string;
  width?: string | number;
  onPressEnter: (e: any) => Promise<void>;
  onBlur?: (e: any) => void;
}

// 角色名称编辑
const InputRoleName = (props: IProps) => {
  const { defaultValue, placeholder = '未命名角色', width = 194, onPressEnter, onBlur } = props;

  return (
    <Input
      autoFocus
      defaultValue={defaultValue}
      style={{ width }}
      placeholder={placeholder}
      onPressEnter={onPressEnter}
      onBlur={onBlur}
    />
  );
};

export default InputRoleName;
