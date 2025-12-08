export const displayCorpLogo = (logoName?: string) => {
  return logoName ? logoName.slice(0, 4) : '';
};

export const filterSpace = (value: string) => {
  return value.replace(/\s+/g, '');
};
