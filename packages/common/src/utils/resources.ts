import { getCorpResourceURL } from './env';

export const getCorpResourceById = (resourceId: string) => {
  const resourceUrl = getCorpResourceURL();
  return `${resourceUrl}/${resourceId}`;
};
