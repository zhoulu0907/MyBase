package com.cmsr.onebase.plugin.service;

import java.util.List;
import java.util.Map;

/**
 * 数据操作服务
 * <p>
 * 提供对平台业务数据的CRUD操作能力。
 * 插件可以通过此服务读写低代码平台管理的业务实体数据。
 * </p>
 *
 * @author matianyu
 * @date 2025-11-29
 */
public interface DataService {

    // ==================== 基础 CRUD ====================

    /**
     * 根据ID获取单条数据
     *
     * @param entityCode 实体编码（业务实体的唯一标识）
     * @param id         数据ID
     * @return 数据Map，不存在返回null
     */
    Map<String, Object> getById(String entityCode, Long id);

    /**
     * 根据条件查询单条数据
     *
     * @param entityCode 实体编码
     * @param query      查询条件
     * @return 数据Map，不存在返回null
     */
    Map<String, Object> getOne(String entityCode, Map<String, Object> query);

    /**
     * 根据条件查询列表
     *
     * @param entityCode 实体编码
     * @param query      查询条件
     * @return 数据列表
     */
    List<Map<String, Object>> list(String entityCode, Map<String, Object> query);

    /**
     * 分页查询
     *
     * @param entityCode 实体编码
     * @param query      查询条件
     * @param pageNum    页码（从1开始）
     * @param pageSize   每页大小
     * @return 分页结果
     */
    PageResult<Map<String, Object>> page(String entityCode, Map<String, Object> query, int pageNum, int pageSize);

    /**
     * 统计数量
     *
     * @param entityCode 实体编码
     * @param query      查询条件
     * @return 数量
     */
    long count(String entityCode, Map<String, Object> query);

    /**
     * 创建数据
     *
     * @param entityCode 实体编码
     * @param data       数据
     * @return 创建后的数据ID
     */
    Long create(String entityCode, Map<String, Object> data);

    /**
     * 批量创建数据
     *
     * @param entityCode 实体编码
     * @param dataList   数据列表
     * @return 创建后的数据ID列表
     */
    List<Long> batchCreate(String entityCode, List<Map<String, Object>> dataList);

    /**
     * 更新数据
     *
     * @param entityCode 实体编码
     * @param id         数据ID
     * @param data       更新的数据
     */
    void update(String entityCode, Long id, Map<String, Object> data);

    /**
     * 根据条件批量更新
     *
     * @param entityCode 实体编码
     * @param query      查询条件
     * @param data       更新的数据
     * @return 更新的记录数
     */
    int updateByQuery(String entityCode, Map<String, Object> query, Map<String, Object> data);

    /**
     * 删除数据
     *
     * @param entityCode 实体编码
     * @param id         数据ID
     */
    void delete(String entityCode, Long id);

    /**
     * 批量删除数据
     *
     * @param entityCode 实体编码
     * @param ids        数据ID列表
     */
    void batchDelete(String entityCode, List<Long> ids);

    /**
     * 根据条件删除
     *
     * @param entityCode 实体编码
     * @param query      查询条件
     * @return 删除的记录数
     */
    int deleteByQuery(String entityCode, Map<String, Object> query);

    // ==================== 高级查询 ====================

    /**
     * 执行自定义SQL查询
     * <p>
     * 注意：出于安全考虑，只支持SELECT语句
     * </p>
     *
     * @param sql    SQL语句
     * @param params 参数
     * @return 查询结果
     */
    List<Map<String, Object>> executeSql(String sql, Object... params);

    /**
     * 分页结果
     *
     * @param <T> 数据类型
     */
    class PageResult<T> {
        private List<T> list;
        private long total;
        private int pageNum;
        private int pageSize;

        public PageResult() {
        }

        public PageResult(List<T> list, long total, int pageNum, int pageSize) {
            this.list = list;
            this.total = total;
            this.pageNum = pageNum;
            this.pageSize = pageSize;
        }

        public List<T> getList() {
            return list;
        }

        public void setList(List<T> list) {
            this.list = list;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        /**
         * 获取总页数
         *
         * @return 总页数
         */
        public int getTotalPages() {
            return (int) Math.ceil((double) total / pageSize);
        }
    }
}
