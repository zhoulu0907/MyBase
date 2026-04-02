export const isArtifexDomain = (): boolean => {
  if (typeof window === 'undefined') return false;
  return window.location.hostname.includes('artifex-cmcc');
};
