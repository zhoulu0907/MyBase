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