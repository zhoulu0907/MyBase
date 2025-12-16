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
};

export const securityEncodeText = (security: SecureEncode, text: string) => {
    if (!text) {
        return '--';
    }
    if (!security.display || !security.type) {
        return text;
    }

    switch (security.type) {
        case SECURITY_ENCODE_TYPES.NAME:
            // 姓名
            if (text.length < 3) {
                return text.substring(0, 1) + '*';
            }
            const centerName = text.substring(1, text.length - 1).replace(/./g, '*');
            return text.substring(0, 1) + centerName + text.substring(text.length - 1);
        case SECURITY_ENCODE_TYPES.PHONE:
            // 手机号
            if (text.length < 7) {
                return text;
            }
            const centerPhone = text.substring(3, text.length - 4).replace(/./g, '*');
            return text.substring(0, 3) + centerPhone + text.substring(text.length - 4);
        case SECURITY_ENCODE_TYPES.EMAIL:
            // 邮箱
            const lastIndex = text.lastIndexOf('@');
            // 未找到 直接返回
            if (lastIndex === -1) {
                return text;
            }
            if (lastIndex <= 3) {
                return text.substring(0, lastIndex) + '*' + text.substring(lastIndex);
            }
            const centerEmail = text.substring(3, lastIndex).replace(/./g, '*');
            return text.substring(0, 3) + centerEmail + text.substring(lastIndex);
        case SECURITY_ENCODE_TYPES.MONEY:
            // 金额
            return '*****';
        case SECURITY_ENCODE_TYPES.ID:
            // 身份证号
            if (text.length < 4) {
                return text;
            }
            const centeId = text.substring(0, text.length - 4).replace(/./g, '*');
            return centeId + text.substring(text.length - 4);
        case SECURITY_ENCODE_TYPES.ADDRESS:
            // 住址
            if (text.length < 8) {
                return text;
            }
            const centeAddress = text.substring(0, text.length - 4).replace(/./g, '*');
            return text.substring(0, 4) + centeAddress + text.substring(text.length - 4);
        case SECURITY_ENCODE_TYPES.IP:
            // IP地址
            const index = text.indexOf('.');
            if (index === -1) {
                return text;
            }
            const centerIp = text.substring(index).replace(/./g, '*');
            return text.substring(0, index) + centerIp;
        case SECURITY_ENCODE_TYPES.CAR_ID:
            // 车牌号
            if (text.length < 4) {
                return text.substring(0, 1) + '**';
            }
            const centerCar = text.substring(1, text.length - 2).replace(/./g, '*');
            return text.substring(0, 1) + centerCar + text.substring(text.length - 2);
        default:
            return text;
    }
}