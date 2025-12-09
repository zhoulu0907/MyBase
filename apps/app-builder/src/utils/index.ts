export const displayCorpLogo = (logoName?: string) => {
  return logoName ? logoName.slice(0, 4) : '';
};

export const filterSpace = (value: string) => {
  return value ? value.replace(/\s+/g, '') : '';
};
