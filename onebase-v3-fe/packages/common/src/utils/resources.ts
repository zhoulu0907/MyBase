import { getCorpResourceURL, getResourceURL } from './env';

export const getResourceById = (resourceId: string) => {
  const resourceUrl = getResourceURL();
  return `${resourceUrl}/${resourceId}`;
};

export const getCorpResourceById = (resourceId: string) => {
  const resourceUrl = getCorpResourceURL();
  return `${resourceUrl}/${resourceId}`;
};
