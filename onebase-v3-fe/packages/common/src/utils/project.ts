/**
 * 项目编码存储工具类
 * 用于在 sessionStorage 中存储和管理 projectCode
 */

const PROJECT_CODE_KEY = 'projectCode';

export const ProjectStorage = {
  /**
   * 获取项目编码
   */
  get(): string | null {
    return sessionStorage.getItem(PROJECT_CODE_KEY);
  },

  /**
   * 设置项目编码
   */
  set(projectCode: string): void {
    sessionStorage.setItem(PROJECT_CODE_KEY, projectCode);
  },

  /**
   * 移除项目编码
   */
  remove(): void {
    sessionStorage.removeItem(PROJECT_CODE_KEY);
  }
};