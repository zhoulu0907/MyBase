import { Card, Image, Tag, Typography } from '@arco-design/web-react';
import { memo } from 'react';
import type { XCanvasCardConfig } from '../schema';
import '../index.css';

const { Text } = Typography;

const CanvasCardType2 = memo((props: XCanvasCardConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { status, runtime = true, config } = props;
  const {
    imageUrl = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cartoon%20character%20avatar&image_size=portrait_square',
    tags = ['标签1', '标签2', '标签3'],
    title = '主标题'
  } = config || {};
  
  // 为 tag 变量添加类型注解
  const typedTags: string[] = tags;

  return (
    <div className="canvas-card-body-type2">
      <div className="canvas-card-image-type2">
        <Image
          src={imageUrl}
          alt="card image"
          width={80}
          height={80}
          style={{ '--fit': 'cover' } as React.CSSProperties}
          preview={false}
        />
      </div>
      
      <div className="canvas-card-content-wrapper-type2">
        <div className="canvas-card-header-type2">
          <Text className="canvas-card-title-type2">
            {title}
          </Text>
          <div className="canvas-card-tags-type2">
            {typedTags.map((tag, index) => (
              <Tag
                key={index}
                className="canvas-card-tag-type2"
                color={index === 0 ? 'red' : index === 1 ? 'blue' : index === 2 ? 'green' : undefined}
              >
                {tag}
              </Tag>
            ))}
          </div>
        </div>
        
        <div className="canvas-card-fields-type2">
          <div className="canvas-card-field-row">
            <div className="canvas-card-field">
              <Text type="secondary">字段1</Text>
              <Text>{'字段1'}</Text>
            </div>
            <div className="canvas-card-field">
              <Text type="secondary">字段2</Text>
              <Text>{'字段2'}</Text>
            </div>
            <div className="canvas-card-field">
              <Text type="secondary">字段3</Text>
              <Text>{'字段3'}</Text>
            </div>
          </div>
          
          <div className="canvas-card-field-row">
            <div className="canvas-card-field">
              <Text type="secondary">字段4</Text>
              <Text>{'字段4'}</Text>
            </div>
            <div className="canvas-card-field">
              <Text type="secondary">字段5</Text>
              <Text>{'字段5'}</Text>
            </div>
            <div className="canvas-card-field">
              <Text type="secondary">字段6</Text>
              <Text>{'字段6'}</Text>
            </div>
          </div>
          
          <div className="canvas-card-field-row">
            <div className="canvas-card-field">
              <Text type="secondary">字段7</Text>
              <Text>{'字段7'}</Text>
            </div>
            <div className="canvas-card-field">
              <Text type="secondary">字段8</Text>
              <Text>{'字段8'}</Text>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
});

export default CanvasCardType2;