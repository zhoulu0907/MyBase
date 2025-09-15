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
      console.log('text', text, mode);
      if (text) {
        const [textId, ...texts] = text.split('.');
        console.log('textId', textId, texts);
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

  const placeholderMatcher = new MatchDecorator({
    regexp: /\[\[(.+?)\]\]/g,
    decoration: (match) => {
      console.log('match', match);
      return Decoration.replace({
        widget: new PlaceholderWidget(match[1])
      });
    }
  });

  return ViewPlugin.fromClass(
    class {
      placeholders: DecorationSet;

      constructor(view: EditorView) {
        this.placeholders = placeholderMatcher.createDeco(view);
        console.log('view', view, this.placeholders);
      }

      update(update: ViewUpdate) {
        this.placeholders = placeholderMatcher.updateDeco(update, this.placeholders);
      }
    },
    {
      decorations: (instance) => instance.placeholders,
      provide: (plugin) => EditorView.atomicRanges.of((view) => view.plugin(plugin)?.placeholders || Decoration.none)
    }
  );
};
