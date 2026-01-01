/**
 * 常量定义（Plugin Constants）
 *
 * 说明：集中放置插件全局常量，避免硬编码散落在代码中。
 * 使用：通过统一导入确保命名一致性与更易维护。
 */
import pkg from '../package.json'

export const PLUGIN_NAME = 'ob-plugin-ocr'
export const PLUGIN_DISPLAY_NAME = 'OCR插件'
export const PLUGIN_VERSION = pkg.version
export const PLUGIN_ROUTE_PREFIX = '/' + PLUGIN_NAME

export const PLUGIN_MANIFEST = {
  name: PLUGIN_NAME,
  displayName: PLUGIN_DISPLAY_NAME,
  version: PLUGIN_VERSION,
  routePrefix: PLUGIN_ROUTE_PREFIX
}
