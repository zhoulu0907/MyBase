export const isOpen = (status: number) => {
    return status === 0 ? false : true;
}

export const convertName = (status: number) => {
    return isOpen(status) ? "禁用": "启用";
}