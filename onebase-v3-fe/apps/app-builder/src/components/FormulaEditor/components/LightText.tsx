import styles from './LightText.module.less';

interface LightTextProps {
    text: string;//显示的文字
    searchValue: string; // 搜索框的值，用于过滤函数列表
    color?: string;
}

export default function LightText({ text, searchValue, color = '' }: LightTextProps) {

    return (
        <>
            {searchValue && searchValue.trim() !== '' ? (
                text.split(new RegExp(`(${searchValue})`, 'gi')).map((part, index) => (
                    part.toLowerCase() === searchValue.toLowerCase() ? (
                        <span key={index} style={{color}} className={styles.highlight}>{part}</span>
                    ) : (
                        <span key={index}>{part}</span>
                    )
                ))
            ) : (
                text
            )}
        </>
    );
}
