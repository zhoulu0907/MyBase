// 掩码类型
export const SECURITY_ENCODE_TYPES = {
  NAME: 'name',
  PHONE: 'phone',
  EMAIL: 'email',
  MONEY: 'money',
  ID: 'id',
  ADDRESS: 'address',
  IP: 'ip',
  CAR_ID: 'car_id'
};

interface SecureEncode {
  display?: boolean;
  type?: string;
}

const convertToString = (text: any): string | null => {
  if (text === null || text === undefined || text === '') {
    return null;
  }

  if (typeof text === 'object') {
    if (Array.isArray(text)) {
      if (text.length === 0) {
        return null;
      }
    } else if (Object.keys(text).length === 0) {
      return null;
    }

    try {
      return JSON.stringify(text);
    } catch {
      return String(text);
    }
  }

  return String(text);
};

const encodeName = (strText: string): string => {
  if (strText.length < 3) {
    return strText.substring(0, 1) + '*';
  }
  const centerName = strText.substring(1, strText.length - 1).replaceAll(/./g, '*');
  return strText.substring(0, 1) + centerName + strText.substring(strText.length - 1);
};

const encodePhone = (strText: string): string => {
  if (strText.length < 7) {
    return strText;
  }
  const centerPhone = strText.substring(3, strText.length - 4).replaceAll(/./g, '*');
  return strText.substring(0, 3) + centerPhone + strText.substring(strText.length - 4);
};

const encodeEmail = (strText: string): string => {
  const lastIndex = strText.lastIndexOf('@');
  if (lastIndex === -1) {
    return strText;
  }
  if (lastIndex <= 3) {
    return strText.substring(0, lastIndex) + '*' + strText.substring(lastIndex);
  }
  const centerEmail = strText.substring(3, lastIndex).replaceAll(/./g, '*');
  return strText.substring(0, 3) + centerEmail + strText.substring(lastIndex);
};

const encodeId = (strText: string): string => {
  if (strText.length < 4) {
    return strText;
  }
  const centerId = strText.substring(0, strText.length - 4).replaceAll(/./g, '*');
  return centerId + strText.substring(strText.length - 4);
};

const encodeAddress = (strText: string): string => {
  if (strText.length < 8) {
    return strText;
  }
  const centerAddress = strText.substring(4, strText.length - 4).replaceAll(/./g, '*');
  return strText.substring(0, 4) + centerAddress + strText.substring(strText.length - 4);
};

const encodeIp = (strText: string): string => {
  const index = strText.indexOf('.');
  if (index === -1) {
    return strText;
  }
  const centerIp = strText.substring(index).replaceAll(/./g, '*');
  return strText.substring(0, index) + centerIp;
};

const encodeCarId = (strText: string): string => {
  if (strText.length < 4) {
    return strText.substring(0, 1) + '**';
  }
  const centerCar = strText.substring(1, strText.length - 2).replaceAll(/./g, '*');
  return strText.substring(0, 1) + centerCar + strText.substring(strText.length - 2);
};

const encodeHandlers: Record<string, (strText: string) => string> = {
  [SECURITY_ENCODE_TYPES.NAME]: encodeName,
  [SECURITY_ENCODE_TYPES.PHONE]: encodePhone,
  [SECURITY_ENCODE_TYPES.EMAIL]: encodeEmail,
  [SECURITY_ENCODE_TYPES.MONEY]: () => '*****',
  [SECURITY_ENCODE_TYPES.ID]: encodeId,
  [SECURITY_ENCODE_TYPES.ADDRESS]: encodeAddress,
  [SECURITY_ENCODE_TYPES.IP]: encodeIp,
  [SECURITY_ENCODE_TYPES.CAR_ID]: encodeCarId
};

export const securityEncodeText = (security: SecureEncode, text: any) => {
  const strText = convertToString(text);
  if (strText === null) {
    return '--';
  }

  if (!security?.display || !security?.type) {
    return strText;
  }

  const handler = encodeHandlers[security.type];
  return handler ? handler(strText) : strText;
};
