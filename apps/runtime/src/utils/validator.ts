import { type ReactNode } from 'react';

export const filterSpace = (value: string) => {
  return value.replace(/\s+/g, '');
};

export const phoneReg = /^1[3-9]\d{9}$/;
export const phoneValidator = (value: string | undefined, callback: (error?: ReactNode) => void) => {
  if (value && !phoneReg.test(value)) {
    callback('请输入正确的手机号');
  } else {
    callback();
  }
};

export const emailReg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
export const emailValidator = (value: string | undefined, callback: (error?: ReactNode) => void) => {
  if (value && !emailReg.test(value)) {
    callback('请输入正确的邮箱地址');
  } else {
    callback();
  }
};
