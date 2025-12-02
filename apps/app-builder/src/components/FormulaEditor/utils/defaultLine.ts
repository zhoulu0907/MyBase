import { EditorState } from '@codemirror/state';
import { Decoration, EditorView, WidgetType } from '@codemirror/view';

class DefaultLineWidget extends WidgetType {
  onCopy: any;
  onDebug: any;
  value: string;
  fieldName?: string;
  constructor(onCopy: any, onDebug: any, value: string, fieldName?: string) {
    super();
    this.onCopy = onCopy;
    this.onDebug = onDebug;
    this.value = value;
    this.fieldName = fieldName;
  }
  //公式编辑器第一行显示单行文本以及两个按钮
  toDOM() {
    const container = document.createElement('div');
    container.style.cssText = `
        display: flex;
        align-items: center;
        background: #f2f3f5;
        height:30px;
        font-size: 14px;
        color: #1d2129;
        font-weight: bold;
        margin-bottom: 12px;
      `;
    const textSpan = document.createElement('span');
    textSpan.textContent = this.fieldName ? `${this.fieldName} =` : '=';
    textSpan.style.flexGrow = '1';

    const createButton = (
      text: string | null,
      onClick: ((this: GlobalEventHandlers, ev: PointerEvent) => any) | null
    ) => {
      const btn = document.createElement('button');
      btn.innerHTML = `<span>${text}</span>`;
      btn.style.cssText = `
        padding:2px 8px;
        border-radius: 3px;
        border: none;
        `;
      btn.onclick = onClick;
      btn.disabled = !this.value;
      return btn;
    };
    container.append(textSpan, createButton('复制', this.onCopy), createButton('调试', this.onDebug));
    return container;
  }
  eq(widget: WidgetType): boolean {
    return false;
  }
  ignoreEvent(event: Event): boolean {
    return false;
  }
}

export const defaultExtenstion = (onCopy: any, onDebug: any, value: string, fieldName?: string) => {
  const defaultLineExtenstion = [
    EditorState.changeFilter.of((tr: any): any => {
      if (tr?.doc?.length === 0) {
        return { changes: { from: 0, insert: '\n' } };
      }
      return true;
    }),
    EditorView.decorations.of(
      Decoration.set([
        Decoration.widget({
          widget: new DefaultLineWidget(onCopy, onDebug, value, fieldName),
          side: -1
        }).range(0)
      ])
    ),
    EditorView.editable.of((view: any) => {
      return view.state.selection.main.head > 1;
    })
  ];

  return defaultLineExtenstion;
};
