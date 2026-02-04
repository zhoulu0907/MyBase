import { Card, Image, Tag, Typography, Avatar } from '@arco-design/web-react';
import { memo, useEffect, useState } from 'react';
import { attachmentDownload, menuSignal } from '@onebase/app';
import type { XCanvasCardConfig } from '../schema';
import '../index.css';

const { Text, Paragraph } = Typography;

const DEFAULT_IMAGE = '/CanvasCardType2Pic.png';

interface CanvasCardType2Props extends XCanvasCardConfig {
  runtime?: boolean;
  detailMode?: boolean;
  record?: Record<string, unknown>;
  displayFields?: {
    avatar?: string;
    mainTitle?: string;
    categoryTags?: string[];
    cardFields?: string[];
  };
  fieldList?: Array<{ fieldName: string; displayName: string }>;
}

const TAG_COLORS = ['pink', 'purple', 'arcoblue', 'cyan'];

const CanvasCardType2 = memo((props: CanvasCardType2Props) => {
  const { status, runtime = true, record, displayFields, fieldList = [], tableName } = props;
  const { curMenu } = menuSignal;
  const [avatarUrl, setAvatarUrl] = useState(DEFAULT_IMAGE);
  
  const getFieldValue = (fieldName?: string): string => {
    if (!fieldName || !record) return '';
    const value = record[fieldName];
    if (value === null || value === undefined) return '';
    if (typeof value === 'object') return (value as any).id || '';
    return String(value);
  };

  const getFieldDisplayName = (fieldName?: string): string => {
    if (!fieldName) return '';
    const field = fieldList.find(f => f.fieldName === fieldName);
    const displayName = field?.displayName || fieldName;
    return `{${displayName}}`;
  };

  const renderFieldPreview = (fieldName?: string, defaultValue?: string) => {
    if (!fieldName) return defaultValue || '';
    return getFieldDisplayName(fieldName);
  };

  const renderContent = (fieldName?: string, defaultValue?: string) => {
    if (runtime && record && fieldName) {
      const value = getFieldValue(fieldName);
      if (value) return value;
      return '';
    }
    return defaultValue || '';
  };

  const getFieldName = (fieldName?: string): string => {
    if (!fieldName) return '';
    const field = fieldList.find(f => f.fieldName === fieldName);
    return field?.displayName || fieldName;
  };
  
  useEffect(() => {
    const loadAvatar = async () => {
      const avatarField = displayFields?.avatar;
      if (runtime && record && avatarField && curMenu.value?.id && tableName) {
        const fieldValue = record[avatarField];
        let fileId = '';
        
        if (Array.isArray(fieldValue) && fieldValue.length > 0) {
          fileId = fieldValue[0]?.id || '';
        } else if (typeof fieldValue === 'object' && fieldValue !== null) {
          fileId = (fieldValue as any).id || '';
        } else if (typeof fieldValue === 'string') {
          fileId = fieldValue;
        }
        
        if (fileId) {
          try {
            const lastIndexOf = avatarField.lastIndexOf('.');
            const curFieldName = lastIndexOf === -1 ? avatarField : avatarField.slice(lastIndexOf + 1);
            const param = {
              menuId: curMenu.value?.id,
              id: (record as any).id || '',
              fieldName: curFieldName,
              fileId: fileId
            };
            const url = await attachmentDownload(tableName, param);
            if (url) {
              setAvatarUrl(url);
            }
          } catch (error) {
            console.error('加载头像失败:', error);
          }
        }
      }
    };
    
    loadAvatar();
  }, [runtime, record, displayFields, tableName, curMenu.value?.id]);

  const cardFields = displayFields?.cardFields || [];

  const renderCardFields = () => {
    const renderList = cardFields.filter((field: string) => field);
    if (renderList.length > 0) {
      return (
        <div className="canvas-card-fields-type2">
          {renderList.map((fieldName: string, index: number) => (
            <div key={index} className="canvas-card-field-item">
              <Text type="secondary">{getFieldName(fieldName)}</Text>
              <Text>{renderContent(fieldName, renderFieldPreview(fieldName, ''))}</Text>
            </div>
          ))}
        </div>
      );
    }
    return null;
  };

  const renderTags = () => {
    const categoryTags = displayFields?.categoryTags || [];
    const renderList = categoryTags.filter((tag: string) => tag);

    const TAG_COLORS = ['pinkpurple', 'arcoblue', 'cyan'];

    if (renderList.length > 0) {
      const filteredList = runtime && record 
        ? renderList.filter(tagField => {
            const tagValue = getFieldValue(tagField);
            return tagValue && tagValue.trim() !== '';
          })
        : renderList;

      if (filteredList.length > 0) {
        return filteredList.map((tagField: string, index: number) => {
          const tagValue = runtime && record ? getFieldValue(tagField) : '';
          return (
            <Tag key={index} className="canvas-card-tag-type2" color={TAG_COLORS[index % TAG_COLORS.length]}>
              {tagValue || renderFieldPreview(tagField, `标签${index + 1}`)}
            </Tag>
          );
        });
      }
    }
    return null;
  };

  return (
    <div className="canvas-card-body-type2">
      <div className="canvas-card-image-type2">
        <Image
          src={avatarUrl}
          alt="card image"
          width={72}
          height={72}
          style={{ '--fit': 'cover' } as React.CSSProperties}
          preview={false}
        />
      </div>
      
      <div className="canvas-card-content-wrapper-type2">
        <div className="canvas-card-header-type2">
          <Text className="canvas-card-title-type2">
            {renderContent(displayFields?.mainTitle, renderFieldPreview(displayFields?.mainTitle, ''))}
          </Text>
          <div className="canvas-card-tags-type2">
            {renderTags()}
          </div>
        </div>
        
        {renderCardFields()}
      </div>
    </div>
  );
});

export default CanvasCardType2;
