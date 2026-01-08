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

export const securityEncodeText = (security: SecureEncode, text: any) => {
    if (text === null || text === undefined || text === '') {
        return '--';
    }

    let strText = text;
    if (typeof text === 'object') {
        if (Array.isArray(text)) {
            if (text.length === 0) {
                return '--';
            }
        } else if (Object.keys(text).length === 0) {
            return '--';
        }

        try {
            strText = JSON.stringify(text);
        } catch (e) {
            strText = String(text);
        }
    } else {
        strText = String(text);
    }

    if (!security?.display || !security?.type) {
        return strText;
    }

    switch (security.type) {
        case SECURITY_ENCODE_TYPES.NAME:
            // 姓名
            if (strText.length < 3) {
                return strText.substring(0, 1) + '*';
            }
            const centerName = strText.substring(1, strText.length - 1).replace(/./g, '*');
            return strText.substring(0, 1) + centerName + strText.substring(strText.length - 1);
        case SECURITY_ENCODE_TYPES.PHONE:
            // 手机号
            if (strText.length < 7) {
                return strText;
            }
            const centerPhone = strText.substring(3, strText.length - 4).replace(/./g, '*');
            return strText.substring(0, 3) + centerPhone + strText.substring(strText.length - 4);
        case SECURITY_ENCODE_TYPES.EMAIL:
            // 邮箱
            const lastIndex = strText.lastIndexOf('@');
            // 未找到 直接返回
            if (lastIndex === -1) {
                return strText;
            }
            if (lastIndex <= 3) {
                return strText.substring(0, lastIndex) + '*' + strText.substring(lastIndex);
            }
            const centerEmail = strText.substring(3, lastIndex).replace(/./g, '*');
            return strText.substring(0, 3) + centerEmail + strText.substring(lastIndex);
        case SECURITY_ENCODE_TYPES.MONEY:
            // 金额
            return '*****';
        case SECURITY_ENCODE_TYPES.ID:
            // 身份证号
            if (strText.length < 4) {
                return strText;
            }
            const centeId = strText.substring(0, strText.length - 4).replace(/./g, '*');
            return centeId + strText.substring(strText.length - 4);
        case SECURITY_ENCODE_TYPES.ADDRESS:
            // 住址
            if (strText.length < 8) {
                return strText;
            }
            const centeAddress = strText.substring(0, strText.length - 4).replace(/./g, '*');
            return strText.substring(0, 4) + centeAddress + strText.substring(strText.length - 4);
        case SECURITY_ENCODE_TYPES.IP:
            // IP地址
            const index = strText.indexOf('.');
            if (index === -1) {
                return strText;
            }
            const centerIp = strText.substring(index).replace(/./g, '*');
            return strText.substring(0, index) + centerIp;
        case SECURITY_ENCODE_TYPES.CAR_ID:
            // 车牌号
            if (strText.length < 4) {
                return strText.substring(0, 1) + '**';
            }
            const centerCar = strText.substring(1, strText.length - 2).replace(/./g, '*');
            return strText.substring(0, 1) + centerCar + strText.substring(strText.length - 2);
        default:
            return strText;
    }
}