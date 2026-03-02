import { Card, Tag, Typography } from '@arco-design/web-react';
import { memo, useEffect, useState } from 'react';
import { attachmentDownload, menuSignal } from '@onebase/app';
import type { XCanvasCardConfig } from '../schema';
import '../index.css';
import defaultImage from '@/assets/images/cp/canvascard-default.svg';
import icon1 from '@/assets/images/cp/icon1.svg';
import icon2 from '@/assets/images/cp/icon2.svg';
import icon3 from '@/assets/images/cp/icon3.svg';
import icon4 from '@/assets/images/cp/icon4.svg';
import icon5 from '@/assets/images/cp/icon5.svg';
import icon6 from '@/assets/images/cp/icon6.svg';
import icon7 from '@/assets/images/cp/icon7.svg';
import icon8 from '@/assets/images/cp/icon8.svg';
import icon9 from '@/assets/images/cp/icon9.svg';
import icon10 from '@/assets/images/cp/icon10.svg';
import icon11 from '@/assets/images/cp/icon11.svg';
import icon12 from '@/assets/images/cp/icon12.svg';
import icon13 from '@/assets/images/cp/icon13.svg';

const { Text, Paragraph } = Typography;

const PREVIEW_IMAGE = '/CanvasCardType1Pic.jpg';

const COUNT_ICONS_MAP: Record<string, string> = {
  icon1,
  icon2,
  icon3,
  icon4,
  icon5,
  icon6,
  icon7,
  icon8,
  icon9,
  icon10,
  icon11,
  icon12,
  icon13
};

interface CanvasCardType1Props extends XCanvasCardConfig {
  runtime?: boolean;
  detailMode?: boolean;
  record?: Record<string, unknown>;
  displayFields?: {
    mainImage?: string;
    mainImageFill?: string;
    categoryTags?: string[];
    mainTitle?: string;
    cardContent?: string;
    auxiliaryInfo?: string[];
    countHint?: string;
    showCountIcon?: boolean;
    countIcon?: string;
  };
  fieldList?: Array<{ fieldName: string; displayName: string }>;
}

const CanvasCardType1 = memo((props: CanvasCardType1Props) => {
  const { status, runtime = true, record, displayFields, fieldList = [], tableName } = props;
  const { curMenu } = menuSignal;
  const [imageUrl, setImageUrl] = useState(runtime ? defaultImage : PREVIEW_IMAGE);
  
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
    if (typeof value === 'object') return (value as any).name || '';
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
        <img
          src={imageUrl}
          alt="card image"
          width={229}
          height={129}
          style={{ objectFit: displayFields?.mainImageFill || 'fill' }}
          onError={(e) => {
            (e.target as HTMLImageElement).src = defaultImage;
          }}
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
                {displayFields?.showCountIcon && displayFields?.countIcon && (
                  <img
                    src={COUNT_ICONS_MAP[displayFields.countIcon]}
                    alt="icon"
                    style={{ width: '16px', height: '16px', marginRight: '4px', verticalAlign: 'middle' }}
                  />
                )}
                {renderContent(displayFields?.countHint, renderFieldPreview(displayFields?.countHint, ''))}
              </Text>
            </div>
          ) : null}
        </div>
      </div>
    </div>
  );
});

export default CanvasCardType1;
