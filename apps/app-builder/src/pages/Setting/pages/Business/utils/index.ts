import type { industryTypeOption } from "../types/appItem";

export const isOpen = (status: number) => {
    return status === 0 ? false : true;
}

export const convertName = (status: number) => {
    return isOpen(status) ? "禁用": "启用";
}

export const formatIndustryType = (optionList:industryTypeOption[], value?: string) => {
    const filteredOption =  optionList.find(item => item.value === value);
    return filteredOption ? filteredOption.label : value;
}