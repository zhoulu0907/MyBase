export const isBlank = (v: any): boolean => v === undefined || v === null || (typeof v === 'string' && v === '');
export const isNotBlank = (v: any): boolean => !isBlank(v);
