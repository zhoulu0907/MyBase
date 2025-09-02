// 实现插入标签
import { ViewUpdate } from '@codemirror/view';
import type { DecorationSet } from '@codemirror/view';
import { Decoration, ViewPlugin, MatchDecorator, EditorView, WidgetType } from '@codemirror/view';

export const placeholdersPlugin = (
  themes?: Record<string, { backgroudColor: string; borderColor: string; textColor: string }>,
  mode = 'name'
) => {
  class PlaceholderWidget extends WidgetType {
    textId = '';
    text = '';

    constructor(text: string) {
      super();
      if (text) {
        const [textId, ...texts] = text.split('.');
        if (textId && texts.length) {
          this.text = texts.map((t) => t.split(':')[mode === 'code' ? 1 : 0]).join('.');
          this.textId = textId;
          console.log('this.text', this.text, this.textId);
        }
      }
    }

    eq(other: PlaceholderWidget) {
      return this.text === other.text;
    }

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

    ignoreEvent() {
      return true;
    }
  }

  class FormulaWidget extends WidgetType {
    text = '';

    constructor(text: string) {
      super();
      if(text) {
        const [textId, texts] = text.split('.');
        if (textId && texts) {
          this.text = texts.split('(')[0];
        }
      }
    }

    eq(other: FormulaWidget) {
      return this.text === other.text;
    }

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

    ignoreEvent() {
      return true;
    }
  }

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

  return ViewPlugin.fromClass(
    class {
      decorations: DecorationSet;

      constructor(view: EditorView) {
        this.decorations = combinedMatcher.createDeco(view);
        console.log('view', view, this.decorations);
      }

      update(update: ViewUpdate) {
        this.decorations = combinedMatcher.updateDeco(update, this.decorations);
      }
    },
    {
      decorations: (instance) => instance.decorations,
      provide: (plugin) =>
        EditorView.atomicRanges.of((view) => {
          const instance = view.plugin(plugin);
          return instance?.decorations || Decoration.none;
        })
    }
  );
};
