import { Card, Image, Tag, Typography } from '@arco-design/web-react';
import { IconEye } from '@arco-design/web-react/icon';
import { memo } from 'react';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import type { XCanvasCardConfig } from './schema';
import './index.css';

const { Text, Paragraph } = Typography;

const XCanvasCard = memo((props: XCanvasCardConfig & { runtime?: boolean; detailMode?: boolean }) => {
  const { status, runtime = true, config } = props;
  const {
    imageUrl = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=beautiful%20mountain%20lake%20reflection%20scenery&image_size=landscape_16_9',
    tags = ['标签标签', 'default', 'default'],
    title = '卡片标题字段',
    content = '这是一段长文本这是一段长文本这是一段长文本这是一段长文本这是一段长文本这是一段长文本这是一段长文本这是一段长文本这是一段长文本这是一段长文本这是一段长文本这是一段长文本',
    source = '华尔街日报',
    publishDate = '2026年1月11日 22:22',
    viewCount = '888'
  } = config || {};
  
  // 为 tag 变量添加类型注解
  const typedTags: string[] = tags;

  return (
    <div className="canvas-card-container" style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1,
        display: runtime && status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 'none' : 'block'
      }}>
      <Card className="XCanvasCard">
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
            {typedTags.map((tag, index) => (
              <Tag
                key={index}
                className="canvas-card-tag"
                color={index === 0 ? 'gray' : index === 1 ? 'arcoblue' : index === 2 ? 'green' : undefined}
              >
                {tag}
              </Tag>
            ))}
          </div>
          
          <Text className="canvas-card-title" ellipsis={{ rows: 1 }}>
            {title}
          </Text>
          
          <Paragraph className="canvas-card-content" ellipsis={{ rows: 2 }}>
            {content}
          </Paragraph>
          
          <div className="canvas-card-footer">
            <div className="canvas-card-meta">
              <Text type="secondary">
                辅助信息1：{source}
              </Text>
              <Text type="secondary">
                辅助信息2：{publishDate}
              </Text>
            </div>
            <div className="canvas-card-stats">
              <Text type="secondary">
                <IconEye /> {viewCount}
              </Text>
            </div>
          </div>
        </div>
      </div>
    </Card>
  </div>
  );
});

export default XCanvasCard;