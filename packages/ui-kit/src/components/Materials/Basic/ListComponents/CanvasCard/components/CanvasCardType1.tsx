import { Card, Image, Tag, Typography } from '@arco-design/web-react';
import { IconEye } from '@arco-design/web-react/icon';
import { memo, useEffect, useState } from 'react';
import { attachmentDownload, menuSignal } from '@onebase/app';
import type { XCanvasCardConfig } from '../schema';
import '../index.css';

const { Text, Paragraph } = Typography;

const DEFAULT_IMAGE = '/CanvasCardType1Pic.jpg';

interface CanvasCardType1Props extends XCanvasCardConfig {
  runtime?: boolean;
  detailMode?: boolean;
  record?: Record<string, unknown>;
  displayFields?: {
    mainImage?: string;
    categoryTags?: string[];
    mainTitle?: string;
    cardContent?: string;
    auxiliaryInfo?: string[];
    countHint?: string;
  };
  fieldList?: Array<{ fieldName: string; displayName: string }>;
}

const TAG_COLORS = ['pink', 'purple', 'arcoblue', 'cyan'];

const CanvasCardType1 = memo((props: CanvasCardType1Props) => {
  const { status, runtime = true, record, displayFields, fieldList = [], tableName } = props;
  const { curMenu } = menuSignal;
  const [imageUrl, setImageUrl] = useState(DEFAULT_IMAGE);
  
  useEffect(() => {
    const loadImage = async () => {
      const imageField = displayFields?.mainImage;
      if (runtime && record && imageField && curMenu.value?.id && tableName) {
        const fieldValue = record[imageField];
        let imageId = '';
        
        if (Array.isArray(fieldValue) && fieldValue.length > 0) {
          imageId = fieldValue[0]?.id || '';
        } else if (typeof fieldValue === 'object' && fieldValue !== null) {
          imageId = (fieldValue as any).id || '';
        } else if (typeof fieldValue === 'string') {
          imageId = fieldValue;
        }
        
        if (imageId) {
          try {
            const lastIndexOf = imageField.lastIndexOf('.');
            const curFieldName = lastIndexOf === -1 ? imageField : imageField.slice(lastIndexOf + 1);
            const param = {
              menuId: curMenu.value?.id,
              id: (record as any).id || '',
              fieldName: curFieldName,
              fileId: imageId
            };
            const url = await attachmentDownload(tableName, param);
            if (url) {
              setImageUrl(url);
            }
          } catch (error) {
            console.error('加载图片失败:', error);
          }
        }
      }
    };
    
    loadImage();
  }, [runtime, record, displayFields, tableName, curMenu.value?.id]);

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
    }
    return defaultValue || '';
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
            <Tag key={index} className="canvas-card-tag" color={TAG_COLORS[index % TAG_COLORS.length]}>
              {tagValue || renderFieldPreview(tagField, '')}
            </Tag>
          );
        });
      }
    }
    return null;
  };

  const renderAuxiliaryInfo = () => {
    const auxiliaryInfo = displayFields?.auxiliaryInfo || [];
    const infoList = auxiliaryInfo.filter((info: string) => info);

    if (infoList.length > 0) {
      const getInfoValue = (infoField: string) => {
        if (runtime && record) {
          const value = getFieldValue(infoField);
          if (value) return value;
        }
        return '';
      };

      return infoList.map((infoField: string, index: number) => {
        const infoValue = getInfoValue(infoField);
        const label = `辅助信息${index + 1}`;
        return (
          <div key={index} className="canvas-card-info-item">
            <Text type="secondary">{label}</Text>
            <Text>{infoValue || renderFieldPreview(infoField, '')}</Text>
          </div>
        );
      });
    }
    return null;
  };

  return (
    <div className="canvas-card-body">
      <div className="canvas-card-image">
        <Image
          src={imageUrl}
          alt="card image"
          width={229}
          height={129}
          style={{ '--fit': 'cover' } as React.CSSProperties}
          preview={false}
        />
      </div>
      
       <div className="canvas-card-content-wrapper">
        <div className="canvas-card-tags">
          {renderTags()}
        </div>
        
        <Text className="canvas-card-title" ellipsis={{ rows: 1 }}>
          {renderContent(displayFields?.mainTitle, renderFieldPreview(displayFields?.mainTitle, ''))}
        </Text>
        
        <Paragraph className="canvas-card-content" ellipsis={{ rows: 2 }}>
          {renderContent(displayFields?.cardContent, renderFieldPreview(displayFields?.cardContent, ''))}
        </Paragraph>
        
        <div className="canvas-card-footer">
          <div className="canvas-card-meta">
            {renderAuxiliaryInfo()}
          </div>
          {displayFields?.countHint ? (
            <div className="canvas-card-stats">
              <Text type="secondary">
                <IconEye /> {renderContent(displayFields?.countHint, renderFieldPreview(displayFields?.countHint, ''))}
              </Text>
            </div>
          ) : null}
        </div>
      </div>
    </div>
  );
});

export default CanvasCardType1;
