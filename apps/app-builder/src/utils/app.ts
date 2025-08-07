export const getAppCode = () => {
  const searchParams = new URLSearchParams(location.search);
  const appCode = searchParams.get('appCode');
  return appCode;
};
