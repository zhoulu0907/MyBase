export const pluginMethods = {
  formatText: (text: string) => `[${new Date().toLocaleTimeString()}] ${text}`,
  processData: async (data: any) => new Promise(resolve => {
    setTimeout(() => {
      resolve({ ...data, processed: true, from: 'ob-plugin-template' })
    }, 1000)
  })
}
