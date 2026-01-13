export interface PluginPage {
  path: string
  component: (props: { sdk: any }) => any
  title: string
}
