export const formatIndustryType = (value: number) =>{
    let newValue: string = "";
    switch(value) {
        case 1: 
            newValue =  "大交通";
            break;
        case 0:
            newValue =  "";
            break;
        default:
            newValue =  "";
            break;
    }
    return newValue;
}

export const convertIndustryType = (value: string) =>{
    let newValue: number = 1;
    switch(value) {
        case "大交通": 
            newValue = 1 ;
            break;
        default:
            newValue = 1;
            break;
    }
    return newValue;
}

export const isOpen = (status: number) => {
    return status === 0 ? false : true;
}

export const convertName = (status: number) => {
    return isOpen(status) ? "禁用": "启用";
}