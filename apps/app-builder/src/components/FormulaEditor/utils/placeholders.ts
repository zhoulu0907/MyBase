// 实现插入标签
import type { DecorationSet } from '@codemirror/view';
import { Decoration, EditorView, MatchDecorator, ViewPlugin, ViewUpdate, WidgetType } from '@codemirror/view';

/**
 * 占位符插件，用于在编辑器中显示占位符
 * @param themes - 主题配置，用于自定义占位符的样式
 * @param mode - 占位符的模式，可选值为 'name'（默认）或 'code'，用于指定占位符的显示格式
 * @returns 插件实例
 */
export const placeholdersPlugin = (
  themes?: Record<string, { backgroudColor: string; borderColor: string; textColor: string }>,
  mode = 'name'
) => {
  /**
   * 占位符小部件，用于在编辑器中显示占位符
   */
  class PlaceholderWidget extends WidgetType {
    //占位符的类型 ID
    textId = '';
    //要显示的占位符文本内容
    text = '';

    //解析输入文本，提取 textId 和显示文本
    constructor(text: string) {
      super();
      if (text) {
        const [textId, ...texts] = text.split('.');
        if (textId && texts.length) {
          this.text = texts.map((t) => t.split(':')[mode === 'code' ? 1 : 0]).join('.');
          if(this.text.includes("$")) {
            const textTemp = this.text.split("$");
            if(textTemp[1]) {
              this.text = `$${textTemp[1]}`
            }
          }
          this.textId = textId;
          console.log('this.text', this.text, this.textId);
        }
      }
    }

    //判断两个占位符是否相等（用于优化渲染）
    eq(other: PlaceholderWidget) {
      return this.text === other.text;
    }

    //创建占位符的 DOM 元素，应用样式并设置文本内容
    toDOM() {
      const elt = document.createElement('span');
      if (!this.text) return elt;

      const { backgroudColor, borderColor, textColor } = themes?.[this.textId] || themes?.name || {};

      elt.style.cssText = `
        border: 1px solid ${borderColor || 'rgba(79, 174, 123, 0.1)'};
        border-radius: 4px;
        line-height: 20px;
        background: ${backgroudColor || 'rgba(79, 174, 123, 0.1)'};
        color: ${textColor || '#4FAE7B'};
        font-size: 12px;
        padding: 2px 7px;
        user-select: none;
        margin: 0 2px;
      `;
      elt.textContent = this.text;
      return elt;
    }

    //返回 true，表示忽略在此占位符上的事件
    //这是为了防止占位符上的点击事件触发编辑器的交互行为
    ignoreEvent() {
      return true;
    }
  }

  //公式小部件，用于在编辑器中显示公式
  //用于渲染 {{...}} 格式的公式文本
  class FormulaWidget extends WidgetType {
    //要显示的公式名称
    text = '';

    //解析输入文本，提取公式名称
    constructor(text: string) {
      super();
      if (text) {
        const [textId, texts] = text.split('.');
        if (textId && texts) {
          this.text = texts.split('(')[0];
        }
      }
    }

    //判断两个公式小部件是否相等（用于优化渲染）
    eq(other: FormulaWidget) {
      return this.text === other.text;
    }

    //创建公式的 DOM 元素，应用粉色样式并设置文本内容
    toDOM() {
      const elt = document.createElement('span');
      if (!this.text) return elt;

      elt.style.cssText = `
        color: #ff69b4;
        font-size: 12px;
        padding: 2px 4px;
        user-select: none;
        margin: 0 2px;
      `;
      elt.textContent = this.text;
      return elt;
    }

    //返回 true，表示忽略在此公式上的事件
    //这是为了防止公式上的点击事件触发编辑器的交互行为
    ignoreEvent() {
      return true;
    }
  }

  //组合匹配器，用于同时匹配占位符和公式
  //使用正则表达式匹配编辑器中的特殊格式文本，并为匹配项创建相应的装饰器
  const combinedMatcher = new MatchDecorator({
    regexp: /(\[\[.+?\]\]|\{\{.+?\}\})/g,
    decoration: (match) => {
      const text = match[1];
      if (text.startsWith('[[') && text.endsWith(']]')) {
        return Decoration.replace({
          widget: new PlaceholderWidget(text.slice(2, -2))
        });
      } else if (text.startsWith('{{') && text.endsWith('}}')) {
        return Decoration.replace({
          widget: new FormulaWidget(text.slice(2, -2))
        });
      }
      return Decoration.mark({});
    }
  });

  //创建插件实例，用于在编辑器中应用占位符和公式的装饰器
  return ViewPlugin.fromClass(
    class {
      //插件实例的 decorations 属性，用于存储当前的装饰器集合
      decorations: DecorationSet;
      //初始化 decorations，使用 combinedMatcher 创建初始装饰
      constructor(view: EditorView) {
        this.decorations = combinedMatcher.createDeco(view);
      }

      //当编辑器内容发生变化时，更新 decorations，使用 combinedMatcher 重新创建装饰
      update(update: ViewUpdate) {
        this.decorations = combinedMatcher.updateDeco(update, this.decorations);
      }
    },
    {
      //提供装饰器集合，用于编辑器渲染
      decorations: (instance) => instance.decorations,
      //提供原子范围，用于确定哪些区域需要应用装饰器
      //这里返回的是一个函数，用于根据视图返回当前插件实例的 decorations 或 Decoration.none
      provide: (plugin) =>
        EditorView.atomicRanges.of((view) => {
          const instance = view.plugin(plugin);
          return instance?.decorations || Decoration.none;
        })
    }
  );
};
