import { dataSetParams } from '../types';
import { dashbordDataSetService } from './clients';

// 数据集列表
export const DataSetList = (params?: any) => {
  return dashbordDataSetService.post('/datasetTree/tree', params);
};
//数据集编辑
export const EditDataSet= (id?: string) => {
  return dashbordDataSetService.post(`/datasetTree/barInfo/${id}`);
};
//数据集删除
export const DelDataSetList = (id?: string) => {
  return dashbordDataSetService.post(`/datasetTree/delete/${id}`);
};
