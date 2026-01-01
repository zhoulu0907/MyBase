export type ComponentCategory = 'custom'

export interface ComponentTemplate {
  h: number
  w: number
  displayName: string
  icon: string
  category: ComponentCategory
}

export interface PluginComponent {
  name?: string
  type?: string
  schema?: any
  template?: ComponentTemplate
  fieldMap?: string[]
  entityMap?: string[]
  component?: (props: any, sdk: any) => any
}
