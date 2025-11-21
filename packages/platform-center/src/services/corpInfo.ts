import { CorpDetailResponse, DictData, updateCorpParams } from "../types";
import { corpService } from "./clients";

//获得企业详情
export const getCorpDetailByIdApiInCorp = (id: number): CorpDetailResponse => corpService.get(`/corp/get?id=${id}`);

/**
 * 根据dict type获得字典数据列表
 */
export const getDictDataByTypeInCorp = (id: string): DictData[] => {
  return corpService.get(`/dict-data/simple-list-by-type?id=${id}`);
};

//更新企业
export const updateCorpApiInCorp = (data: updateCorpParams) => corpService.post('/corp/update', data);
