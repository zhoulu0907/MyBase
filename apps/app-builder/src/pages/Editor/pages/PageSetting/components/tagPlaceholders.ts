import type { DecorationSet } from '@codemirror/view';
import { Decoration, EditorView, MatchDecorator, ViewPlugin, ViewUpdate, WidgetType } from '@codemirror/view';

export const tagPlaceholdersPlugin = () => {
  class TagWidget extends WidgetType {
    text = '';

    constructor(text: string) {
      super();
      if (text) {
        this.text = text;
      }
    }

    eq(other: TagWidget) {
      return this.text === other.text;
    }

    toDOM() {
      const elt = document.createElement('span');
      if (!this.text) return elt;

      const primaryColor = getComputedStyle(document.documentElement).getPropertyValue('--primary-6').trim() || '0, 158, 158';

      elt.style.cssText = `
        border-radius: 2px;
        line-height: 20px;
        background-color: #E8FFFE;
        color: rgb(${primaryColor});
        font-size: 14px;
        padding: 2px 8px;
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

  const tagMatcher = new MatchDecorator({
    regexp: /(\{\{.+?\}\})/g,
    decoration: (match) => {
      const text = match[1];
      return Decoration.replace({
        widget: new TagWidget(text.slice(2, -2))
      });
    }
  });

  return ViewPlugin.fromClass(
    class {
      decorations: DecorationSet;

      constructor(view: EditorView) {
        this.decorations = tagMatcher.createDeco(view);
      }

      update(update: ViewUpdate) {
        this.decorations = tagMatcher.updateDeco(update, this.decorations);
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
