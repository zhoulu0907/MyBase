import { Button, Select } from '@arco-design/web-react';
import { FilterEntityFields, type AppEntityField } from '@onebase/app';
import {
  COMPONENT_MAP,
  COMPONENT_TYPE_DISPLAY_NAME_MAP,
  FORM_COMPONENT_TYPES,
  useAppEntityStore
} from '@onebase/ui-kit';
import React, { useRef, useState, useEffect, useCallback } from 'react';
import styles from './components.module.less';

interface TagInputProps {
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
}

interface FieldItem {
  id: string;
  displayName: string;
  label: string;
  type: string;
  fieldName: string;
  tableName: string;
  isSystemField: number;
}

interface ContentItem {
  type: 'text' | 'tag';
  content: string;
  fieldName?: string;
}

const TagInput: React.FC<TagInputProps> = ({
  value,
  onChange,
  placeholder = '请输入文字或添加字段，至少添加一个字段'
}) => {
  const contentRef = useRef<HTMLDivElement>(null);
  const [showFieldSelector, setShowFieldSelector] = useState(false);
  const [customFields, setCustomFields] = useState<FieldItem[]>([]);
  const [systemFields, setSystemFields] = useState<FieldItem[]>([]);
  const [content, setContent] = useState<ContentItem[]>([{ type: 'text', content: '' }]);

  const { mainEntity } = useAppEntityStore();

  useEffect(() => {
    if (mainEntity.fields.length > 0) {
      const fields = mainEntity.fields
        .filter((field: AppEntityField) => !FilterEntityFields.includes(field.fieldName))
        .map((field: AppEntityField, index: number) => {
          let cpType = COMPONENT_MAP[field.fieldType];
          if (!cpType) {
            cpType = FORM_COMPONENT_TYPES.INPUT_TEXT;
          }
          return {
            id: `${cpType}-${index}-${Date.now()}`,
            displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[cpType] || '',
            label: field.displayName,
            type: cpType,
            tableName: mainEntity.tableName,
            fieldName: field.fieldName,
            isSystemField: field.isSystemField
          };
        });

      const custom = fields.filter((item) => item.isSystemField === 0);
      const system = fields.filter((item) => item.isSystemField === 1);

      setCustomFields(custom);
      setSystemFields(system);
    }
  }, [mainEntity]);

  const allFields = [...customFields, ...systemFields];

  const addField = useCallback(
    (fieldName: string) => {
      const field = allFields.find((f) => f.fieldName === fieldName);
      if (!field) return;

      setContent((prevContent) => {
        const newContent = [...prevContent];
        const lastItem = newContent[newContent.length - 1];

        if (lastItem.type === 'text' && lastItem.content === '') {
          newContent[newContent.length - 1] = { type: 'tag', content: field.label, fieldName: field.fieldName };
        } else {
          newContent.push({ type: 'tag', content: field.label, fieldName: field.fieldName });
          newContent.push({ type: 'text', content: '' });
        }

        const textValue = newContent
          .map((item) => (item.type === 'tag' ? `【${item.content}】` : item.content))
          .join('');
        onChange(textValue);

        return newContent;
      });

      setShowFieldSelector(false);

      setTimeout(() => {
        if (contentRef.current) {
          const textNodes = contentRef.current.querySelectorAll('span:last-child');
          const lastTextNode = textNodes[textNodes.length - 1];
          if (lastTextNode) {
            const range = document.createRange();
            range.selectNodeContents(lastTextNode);
            range.collapse(true);
            const selection = window.getSelection();
            if (selection) {
              selection.removeAllRanges();
              selection.addRange(range);
            }
          }
          contentRef.current.focus();
        }
      }, 0);
    },
    [allFields, onChange]
  );

  const removeTag = useCallback(
    (index: number) => {
      setContent((prevContent) => {
        const newContent = [...prevContent];
        newContent.splice(index, 1);

        if (newContent.length === 0) {
          newContent.push({ type: 'text', content: '' });
        }

        const textValue = newContent
          .map((item) => (item.type === 'tag' ? `【${item.content}】` : item.content))
          .join('');
        onChange(textValue);

        return newContent;
      });
    },
    [onChange]
  );

  const updateText = useCallback(
    (text: string) => {
      setContent((prevContent) => {
        const newContent = [...prevContent];
        const lastItem = newContent[newContent.length - 1];

        if (lastItem.type === 'text') {
          lastItem.content = text;
        } else {
          newContent.push({ type: 'text', content: text });
        }

        const textValue = newContent
          .map((item) => (item.type === 'tag' ? `【${item.content}】` : item.content))
          .join('');
        onChange(textValue);

        return newContent;
      });
    },
    [onChange]
  );

  const handleInput = useCallback(
    (e: React.FormEvent<HTMLDivElement>) => {
      const div = e.currentTarget;
      const selection = window.getSelection();
      if (!selection || selection.rangeCount === 0) return;

      const range = selection.getRangeAt(0);
      const node = range.startContainer;

      if (node.nodeType !== Node.TEXT_NODE) return;

      const textContent = node.textContent || '';

      setContent((prevContent) => {
        const newContent = [...prevContent];
        const textIndex = Array.from(div.childNodes).indexOf(node);

        if (textIndex >= 0 && textIndex < newContent.length && newContent[textIndex].type === 'text') {
          newContent[textIndex].content = textContent;

          const textValue = newContent
            .map((item) => (item.type === 'tag' ? `【${item.content}】` : item.content))
            .join('');
          onChange(textValue);
        }

        return newContent;
      });
    },
    [onChange]
  );

  const handleKeyDown = useCallback(
    (e: React.KeyboardEvent<HTMLDivElement>) => {
      if (e.key === 'Backspace') {
        const selection = window.getSelection();
        if (selection && selection.rangeCount > 0) {
          const range = selection.getRangeAt(0);
          const node = range.startContainer;

          if (node.nodeType === Node.TEXT_NODE) {
            const textContent = node.textContent || '';

            if (range.startOffset === 0 && textContent.length === 0) {
              const prevSibling = node.previousSibling;
              if (prevSibling && prevSibling.nodeType === Node.ELEMENT_NODE) {
                const tagIndex = Array.from(contentRef.current?.childNodes || []).indexOf(prevSibling);
                if (tagIndex >= 0) {
                  e.preventDefault();
                  removeTag(tagIndex);
                }
              }
            }
          }
        }
      }
    },
    [removeTag]
  );

  useEffect(() => {
    if (contentRef.current) {
      contentRef.current.focus();
    }
  }, []);

  const getUsedFieldNames = useCallback(() => {
    return content.filter((item) => item.type === 'tag').map((item) => item.fieldName);
  }, [content]);

  const renderContent = () => {
    return content.map((item, index) => {
      if (item.type === 'text') {
        return (
          <span
            key={`text-${index}`}
            contentEditable
            suppressContentEditableWarning
            onInput={handleInput}
          >
            {item.content}
          </span>
        );
      } else {
        return (
          <span
            key={`tag-${index}`}
            className={styles.tagItem}
            onClick={() => removeTag(index)}
          >
            【{item.content}】
          </span>
        );
      }
    });
  };

  return (
    <div className={styles.tagInputWrapper}>
      <div
        ref={contentRef}
        className={styles.tagInputContent}
        contentEditable
        suppressContentEditableWarning
        onKeyDown={handleKeyDown}
        data-placeholder={content.length === 1 && content[0].content === '' ? placeholder : ''}
      >
        {renderContent()}
      </div>
      <div style={{ position: 'relative', marginTop: '8px' }}>
        <Button type="primary" onClick={() => setShowFieldSelector(!showFieldSelector)}>
          + 添加字段
        </Button>
        {showFieldSelector && (
          <div className={styles.fieldSelector}>
            <Select
              placeholder="请选择字段"
              options={allFields.map((field) => ({
                label: field.label,
                value: field.fieldName,
                disabled: getUsedFieldNames().includes(field.fieldName)
              }))}
              onChange={addField}
              style={{ width: '100%' }}
              autoFocus
              showSearch
            />
          </div>
        )}
      </div>
    </div>
  );
};

export default TagInput;
