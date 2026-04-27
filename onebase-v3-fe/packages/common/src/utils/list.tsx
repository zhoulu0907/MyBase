/**
 * 通用分页列表方法 最后一页没有数据问题
 * 请求方法  getList
 * 请求参数 req
 * 页码设置方法 setPageNo
 * 页码? pageNo 默认取req里的pageNo
 * total  list
 */
interface PageParam {
  pageNo: number;
  pageSize: number;
  [key: string]: any;
}
export const getCommonPaginationList = async (
  getList: (param: PageParam) => any,
  req: PageParam,
  setPageNo: (num: number) => void
) => {
  const res = await getList(req);
  if (res && (!res.list || res.list.length === 0) && res.total != 0 && req.pageNo > 1) {
    const newPageNo = req.pageNo - 1;
    setPageNo(newPageNo);
    return null;
  } else {
    return res;
  }
};