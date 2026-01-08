/**
 * 工具方法：OCRSettingsSetter
 */

/**
 * 解析绑定规则，统一数组/对象两种入参
 *
 * @param rules 任意规则输入，可能是数组或对象
 * @returns 规范化后的规则对象：{ bindings, bindingsFront, bindingsBack, autoCreate }
 */
export const parseBindingRules = (rules: any) => {
  const defaults = {
    bindings: [],
    bindingsFront: [],
    bindingsBack: [],
    autoCreate: false
  }
  if (Array.isArray(rules)) return { ...defaults, bindings: rules }
  if (rules && typeof rules === 'object') {
    return {
      bindings: Array.isArray(rules.bindings) ? rules.bindings : [],
      bindingsFront: Array.isArray(rules.bindingsFront) ? rules.bindingsFront : [],
      bindingsBack: Array.isArray(rules.bindingsBack) ? rules.bindingsBack : [],
      autoCreate: !!rules.autoCreate
    }
  }
  return defaults
}

/**
 * 获取实体与字段信息，基于配置与所在表环境选择目标实体
 *
 * 选择策略：
 * - 优先使用 `config.dataField[0]` 指向的表名
 * - 其次依据 `isInSubTable` 在子表/主表内选取
 * - 回退为第一个可用实体
 *
 * @param sdk 宿主 SDK
 * @param config 组件配置（含 dataField）
 * @param isInSubTable 是否处于子表环境
 * @returns { entityFields, currentTableName, currentEntityName, scopeText }
 */
export const getEntityInfo = (sdk: any, config: any, isInSubTable?: boolean) => {
  const info = {
    entityFields: [] as any[],
    currentTableName: '',
    currentEntityName: '',
    scopeText: ''
  }
  if (!sdk?.context?.entity?.getFields) return info
  const entities = sdk.context.entity.getEntities?.() || []
  if (entities.length === 0) return info
  const tableNameFromConfig = Array.isArray(config?.dataField) ? config.dataField[0] : ''
  let target = (entities || []).find((e: any) => String(e?.tableName || '') === String(tableNameFromConfig || ''))
  if (!target) {
    if (isInSubTable) {
      target = (entities || []).find((e: any) => e?.isSubEntity) || entities[0]
    } else {
      target = (entities || []).find((e: any) => !e?.isSubEntity) || entities[0]
    }
  }
  if (!target) return info
  const fields = sdk.context.entity.getFields(target.entityUuid)
  info.entityFields = Array.isArray(fields) ? fields : []
  info.currentTableName = String(target?.tableName || '')
  info.currentEntityName = String(target?.entityName || '')
  info.scopeText = String(target?.entityName || (target?.isSubEntity ? '子表' : '主表'))
  return info
}

/**
 * 计算联动字段的可选项，兼容多种字段定义结构
 *
 * @param currentLinkField 当前选中的联动字段
 * @returns 标准化选项数组：[{ label, value }]
 */
export const computeLinkFieldOptions = async (currentLinkField: any, sdk?: any, currentTableName?: string) => {
  const rawOptions = currentLinkField?.options || currentLinkField?.props?.options || []
  const local = (rawOptions || []).map((o: any) => ({
    label: o?.optionLabel || o?.label || o?.text || String(o?.value ?? ''),
    value: o?.id ?? o?.optionValue ?? o?.value
  }))
  if (Array.isArray(local) && local.length > 0) return local
  const loader = sdk?.context?.entity?.getFieldOptions
  const name = currentLinkField?.fieldName
  if (typeof loader === 'function' && name && currentTableName) {
    const remote = await Promise.resolve(loader([currentTableName, name]))
    if (Array.isArray(remote)) return remote
  }
  return []
}

/**
 * 从实体字段中过滤出单选型字段（SELECT/RADIO）
 *
 * @param entityFields 实体字段列表
 * @returns 单选字段数组
 */
export const computeSingleSelectFields = (entityFields: any[]) => {
  const filtered = (entityFields || []).filter((f: any) => ['SELECT', 'RADIO'].includes(String(f?.fieldType || '').toUpperCase()))
  return filtered
}

/**
 * 在单选字段集合中查找指定字段名的联动字段
 *
 * @param singleSelectFields 单选字段集合
 * @param linkFieldName 字段名
 * @returns 匹配的字段或 undefined
 */
export const findLinkField = (singleSelectFields: any[], linkFieldName: string) => {
  return (singleSelectFields || []).find((f: any) => f.fieldName === linkFieldName)
}

import { OCR_FIELDS } from '../../PluginOCR/constants'

/**
 * 初始化指定识别类型的绑定列表，结合已有绑定进行回填
 *
 * 类型处理：
 * - `id_card_both`：分别生成正面/反面两套绑定
 * - 其他类型：生成单套绑定（使用对应 `OCR_FIELDS[type]`，回退到 `general`）
 *
 * @param type 识别类型
 * @param existingBindings 现有单面绑定
 * @param existingFront 现有正面绑定
 * @param existingBack 现有反面绑定
 * @returns { bindings, bindingsFront, bindingsBack }
 */
export const initBindingsForType = (
  type: string,
  existingBindings: any[],
  existingFront: any[],
  existingBack: any[]
) => {
  if (type === 'id_card_both') {
    const fieldsFront = OCR_FIELDS['id_card_front'] || []
    const fieldsBack = OCR_FIELDS['id_card_back'] || []

    const existingMapFront = new Map()
    existingFront.forEach((v: any) => {
      if (v && v.ocrField) existingMapFront.set(v.ocrField, v.formField)
    })
    const existingMapBack = new Map()
    existingBack.forEach((v: any) => {
      if (v && v.ocrField) existingMapBack.set(v.ocrField, v.formField)
    })

    const newFront = fieldsFront.map((f: any) => ({
      id: f.key,
      ocrField: f.key,
      ocrLabel: f.label,
      formField: existingMapFront.get(f.key) || ''
    }))
    const newBack = fieldsBack.map((f: any) => ({
      id: f.key,
      ocrField: f.key,
      ocrLabel: f.label,
      formField: existingMapBack.get(f.key) || ''
    }))
    return { bindings: [], bindingsFront: newFront, bindingsBack: newBack }
  } else {
    const fields = (OCR_FIELDS as any)[type] || OCR_FIELDS.general
    const existingMap = new Map()
    ;(existingBindings || []).forEach((v: any) => {
      if (v && v.ocrField) existingMap.set(v.ocrField, v.formField)
    })
    const newBindings = fields.map((f: any) => ({
      id: f.key,
      ocrField: f.key,
      ocrLabel: f.label,
      formField: existingMap.get(f.key) || ''
    }))
    return { bindings: newBindings, bindingsFront: [], bindingsBack: [] }
  }
}
