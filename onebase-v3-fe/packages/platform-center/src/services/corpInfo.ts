import { CorpDetailResponse } from '../types';
import { runtimeCorpService } from './clients';

//获得企业详情
export const getCorpDetailByIdApiInCorp = (id: string): CorpDetailResponse => runtimeCorpService.get(`/get?id=${id}`);
