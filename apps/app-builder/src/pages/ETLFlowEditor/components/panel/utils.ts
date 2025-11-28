import { ETLNodeType } from '@onebase/common';
import { v4 as uuidv4 } from 'uuid';

export const generateNodeId = (nodeType: ETLNodeType) => {
  const uuid = uuidv4().replaceAll('-', '');
  return `${nodeType}_${uuid}`;
};
