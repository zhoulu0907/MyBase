/**
 * 字典值编码生成工具
 */
import pinyin from 'tiny-pinyin';

// 汉字转大写拼音
function charToPinyin(char: string): string {
  return pinyin.convertToPinyin(char).toUpperCase();
}

// 过滤字符，只保留英文字母、数字和下划线
function filterValidChars(str: string): string {
  return str.replace(/[^a-zA-Z0-9_]/g, '');
}

// 将字符串转换为编码
export function generateCode(input: string): string {
  if (!input || typeof input !== 'string') {
    return '';
  }

  let result = '';

  for (const char of input) {
    if (/[\u4e00-\u9fa5]/.test(char)) {
      // 汉字转拼音
      result += charToPinyin(char);
    } else {
      // 其他字符直接添加
      result += char;
    }
  }

  result = filterValidChars(result);

  return result.toUpperCase();
}

// 处理编码重名
export function handleDuplicateCode(codes: string[], newCode: string): string {
  if (!codes.includes(newCode)) {
    return newCode;
  }

  let counter = 1;
  let uniqueCode = `${newCode}${String(counter).padStart(2, '0')}`;

  while (codes.includes(uniqueCode)) {
    counter++;
    uniqueCode = `${newCode}${String(counter).padStart(2, '0')}`;
  }

  return uniqueCode;
}

// 批量生成编码
export function batchGenerateCodes<T extends { label: string; value: string }>(items: T[]): T[] {
  const existingCodes: string[] = [];
  const result: T[] = [];

  for (const item of items) {
    // 只处理字典值不为空但编码为空的项
    if (item.label && item.label.trim() && (!item.value || !item.value.trim())) {
      const generatedCode = generateCode(item.label);
      const uniqueCode = handleDuplicateCode(existingCodes, generatedCode);
      existingCodes.push(uniqueCode);

      result.push({
        ...item,
        value: uniqueCode
      });
    } else {
      if (item.value) {
        existingCodes.push(item.value);
      }
      result.push(item);
    }
  }

  return result;
}
