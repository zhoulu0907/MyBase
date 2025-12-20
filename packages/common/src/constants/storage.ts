/**
 * localStorage 存储 key 常量
 */

/**
 * 运行时表单草稿缓存 key 前缀
 * 完整格式：runtime_form_cache_{userId}_{viewId}
 */
export const RUNTIME_FORM_CACHE_KEY_PREFIX = 'runtime_form_cache_';

/**
 * 运行时表单载入草稿 key 前缀
 * 完整格式：runtime_form_load_draft_{userId}_{viewId}
 */
export const RUNTIME_FORM_LOAD_DRAFT_KEY_PREFIX = 'runtime_form_load_draft_';

/**
 * 获取运行时表单草稿缓存 key
 * @param userId 用户ID
 * @param viewId 视图ID
 * @returns 完整的缓存 key
 */
export const getRuntimeFormCacheKey = (userId: string, viewId: string): string => {
  return `${RUNTIME_FORM_CACHE_KEY_PREFIX}${userId}_${viewId}`;
};

/**
 * 获取运行时表单载入草稿 key
 * @param userId 用户ID
 * @param viewId 视图ID
 * @returns 完整的载入草稿 key
 */
export const getRuntimeFormLoadDraftKey = (userId: string, viewId: string): string => {
  return `${RUNTIME_FORM_LOAD_DRAFT_KEY_PREFIX}${userId}_${viewId}`;
};
