import {
  CreateDataSourceReq,
  CreateETLFlowReq,
  FlinkFunctionListReq,
  ListAppETLDatasourceParams,
  ListTableColumnsReq,
  ListTablesReq,
  PageDatasourceReq,
  PageETLFlowReq,
  PingDatasourceReq,
  PreviewDatasourceReq,
  UpdateDataSourceReq,
  UpdateETLFlowReq,
  UpdateWorkflowScheduleInfoReq
} from '../types';
import { etlService } from './clients';

export const pingETLDataSource = (params: PingDatasourceReq) => etlService.post(`/datasource/ping`, params);

export const createETLDataSource = (params: CreateDataSourceReq) => etlService.post(`/datasource/create`, params);

export const updateETLDatasource = (params: UpdateDataSourceReq) => etlService.post(`/datasource/update`, params);

export const getETLDatasource = (datasourceId: string) => etlService.get(`/datasource/${datasourceId}`);

export const pageETLDatasource = (params: PageDatasourceReq) => etlService.get(`/datasource/page`, params);

export const previewETLDatasource = (params: PreviewDatasourceReq) => etlService.post(`/datasource/preview`, params);

export const listAppETLDatasource = (params: ListAppETLDatasourceParams) => etlService.get(`/datasource/list`, params);

export const listETLTables = (params: ListTablesReq) => etlService.get(`/datasource/tables`, params);

export const listETLTableColumns = (params: ListTableColumnsReq) => etlService.get(`/datasource/table/columns`, params);

export const pageETLFlow = (params: PageETLFlowReq) => etlService.get(`/workflow/page`, params);

export const craeteETLFlow = (params: CreateETLFlowReq) => etlService.post(`/workflow/create`, params);

export const updateETLFlow = (params: UpdateETLFlowReq) => etlService.post(`/workflow/update`, params);

export const deleteETLFlow = (id: string) => etlService.post(`/workflow/delete?id=${id}`);

export const getETLFlow = (worlflowId: string) => etlService.get(`/workflow/${worlflowId}`);

// export const getETLFlowLogs = (id: string) => etlService.get(`/workflow/logs?id=${id}`);

export const startETLFlow = (id: string) => etlService.post(`/workflow/${id}/start`);

export const enableETLFlow = (id: string) => etlService.post(`/workflow/${id}/enable`);

export const disableETLFlow = (id: string) => etlService.post(`/workflow/${id}/disable`);

export const getETLFlowScheduleInfo = (id: string) => etlService.get(`/workflow/schedule/${id}`);

export const updateETLFlowScheduleInfo = (params: UpdateWorkflowScheduleInfoReq) =>
  etlService.post(`/workflow/schedule`, params);

export const flinkFunctionList = (params: FlinkFunctionListReq) => etlService.get(`/flink-function/list`, params);

export const flinkFunctionTypeList = () => etlService.get(`/flink-function/list-type`);
