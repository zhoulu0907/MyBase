import { Typography } from '@arco-design/web-react';
import styles from './InfoPanel.module.less';
import type { info } from '../utils/types';
import LightText from './LightText';

const {Title, Paragraph}  = Typography;

// 解析函数表达式中的参数并高亮显示，保留括号和逗号
const renderParameters = (usage: string) => {
    // 提取函数名和括号内容
    const funcMatch = usage.match(/^([a-zA-Z0-9_]+)\((.*)\)$/);
    if (!funcMatch) {
        return <>{usage}</>;
    }
    
    // 提取函数名和括号内容
    const funcName = funcMatch[1];
    // 提取参数部分
    const paramsStr = funcMatch[2];
    
    // 如果没有参数
    if (!paramsStr.trim()) {
        return (
            <>
                {funcName}()
            </>
        );
    }
    
    // 按逗号分割参数，但保留逗号和空格
    const parts = [];
    
    // 使用更复杂的正则表达式来匹配参数和逗号，支持嵌套结构和任意数量的参数
    const paramRegex = /(\s*[^,()]+(?:\([^()]*\))*[^,()]*(?:\[[^\[\]]*\])?\s*)(,|$)/g;
    let match;
    let lastIndex = 0;
    
    // 遍历匹配结果
    while ((match = paramRegex.exec(paramsStr)) !== null) {
        if (match.index > lastIndex) {
            parts.push(
                <span key={`text-${parts.length}`}>
                    {paramsStr.slice(lastIndex, match.index)}
                </span>
            );
        }
        
        // 高亮显示参数
        parts.push(
            <span key={`param-${parts.length}`} className={styles.paramName}>
                {match[1]}
            </span>
        );
        
        // 保留逗号
        if (match[2] === ',') {
            parts.push(
                <span key={`comma-${parts.length}`}>
                    {match[2]}
                </span>
            );
        }
        
        //更新lastIndex为当前匹配的结束位置
        lastIndex = match.index + match[0].length;
    }
    
    // 返回括号和括号中的参数部分，去掉函数名
    return (
        <>
            ({parts})
        </>
    );
}

export function InfoPanel({ info }: { info: info | null }) {
  return (
    <div className={styles.infoPanel}>
      <Typography.Title heading={6}>{info?.name}</Typography.Title>
      {
        info?.usage &&
        <Typography>
          <Title heading={6}>用法:</Title>
          <Paragraph>
            {/* 高亮函数名 */}
            <LightText text={info?.name} searchValue={info?.name} color='#ff69b4' />
            {/* 内部高亮标签 */}
            {renderParameters(info.usage)}
            <br />
            {/* 函数简介 */}
            {/* {info?.summary} */}
          </Paragraph>
        </Typography>
      }

      {
        info?.example &&
        <Typography>
          <Title heading={6}>示例:</Title>
          <Paragraph>
            {/* 高亮函数名 */}
            <LightText text={info?.example} searchValue={info?.name} color='#ff69b4' />
          </Paragraph>
        </Typography>
      }
    </div>
  );
}
