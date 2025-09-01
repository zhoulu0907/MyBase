/**
 * 简化的Formula.js库，实现基本的Excel函数
 * 支持LEFT函数和SUM函数
 */
var FormulaJS = (function() {
    'use strict';

    /**
     * LEFT函数：从文本字符串的第一个字符开始返回指定个数的字符
     * @param {string} text 要提取字符的文本字符串
     * @param {number} numChars 要提取的字符数
     * @returns {string} 提取的字符串
     */
    function LEFT(text, numChars) {
        if (typeof text !== 'string') {
            text = String(text);
        }
        if (typeof numChars !== 'number' || numChars < 0) {
            throw new Error('Invalid number of characters');
        }
        return text.substring(0, numChars);
    }

    /**
     * SUM函数：计算数字的和
     * @param {...number} args 要求和的数字参数
     * @returns {number} 数字的和
     */
    function SUM() {
        var sum = 0;
        for (var i = 0; i < arguments.length; i++) {
            var arg = arguments[i];
            if (Array.isArray(arg)) {
                // 如果参数是数组，递归计算
                sum += SUM.apply(null, arg);
            } else if (typeof arg === 'number' && !isNaN(arg)) {
                sum += arg;
            } else if (typeof arg === 'string') {
                var num = parseFloat(arg);
                if (!isNaN(num)) {
                    sum += num;
                }
            }
        }
        return sum;
    }

    /**
     * RIGHT函数：从文本字符串的最后一个字符开始返回指定个数的字符
     * @param {string} text 要提取字符的文本字符串
     * @param {number} numChars 要提取的字符数
     * @returns {string} 提取的字符串
     */
    function RIGHT(text, numChars) {
        if (typeof text !== 'string') {
            text = String(text);
        }
        if (typeof numChars !== 'number' || numChars < 0) {
            throw new Error('Invalid number of characters');
        }
        return text.substring(text.length - numChars);
    }

    /**
     * MID函数：从文本字符串的指定位置开始返回指定个数的字符
     * @param {string} text 要提取字符的文本字符串
     * @param {number} startNum 起始位置（从1开始）
     * @param {number} numChars 要提取的字符数
     * @returns {string} 提取的字符串
     */
    function MID(text, startNum, numChars) {
        if (typeof text !== 'string') {
            text = String(text);
        }
        if (typeof startNum !== 'number' || startNum < 1) {
            throw new Error('Invalid start position');
        }
        if (typeof numChars !== 'number' || numChars < 0) {
            throw new Error('Invalid number of characters');
        }
        return text.substring(startNum - 1, startNum - 1 + numChars);
    }

    /**
     * LEN函数：返回文本字符串的长度
     * @param {string} text 要计算长度的文本字符串
     * @returns {number} 字符串长度
     */
    function LEN(text) {
        if (typeof text !== 'string') {
            text = String(text);
        }
        return text.length;
    }

    /**
     * UPPER函数：将文本转换为大写
     * @param {string} text 要转换的文本字符串
     * @returns {string} 大写文本
     */
    function UPPER(text) {
        if (typeof text !== 'string') {
            text = String(text);
        }
        return text.toUpperCase();
    }

    /**
     * LOWER函数：将文本转换为小写
     * @param {string} text 要转换的文本字符串
     * @returns {string} 小写文本
     */
    function LOWER(text) {
        if (typeof text !== 'string') {
            text = String(text);
        }
        return text.toLowerCase();
    }

    /**
     * AVERAGE函数：计算数字的平均值
     * @param {...number} args 要计算平均值的数字参数
     * @returns {number} 平均值
     */
    function AVERAGE() {
        var sum = SUM.apply(null, arguments);
        var count = 0;
        for (var i = 0; i < arguments.length; i++) {
            var arg = arguments[i];
            if (Array.isArray(arg)) {
                count += arg.length;
            } else if (typeof arg === 'number' && !isNaN(arg)) {
                count++;
            } else if (typeof arg === 'string' && !isNaN(parseFloat(arg))) {
                count++;
            }
        }
        return count > 0 ? sum / count : 0;
    }

    /**
     * MAX函数：返回最大值
     * @param {...number} args 要比较的数字参数
     * @returns {number} 最大值
     */
    function MAX() {
        var max = -Infinity;
        for (var i = 0; i < arguments.length; i++) {
            var arg = arguments[i];
            if (Array.isArray(arg)) {
                var arrayMax = MAX.apply(null, arg);
                if (arrayMax > max) {
                    max = arrayMax;
                }
            } else if (typeof arg === 'number' && !isNaN(arg)) {
                if (arg > max) {
                    max = arg;
                }
            } else if (typeof arg === 'string') {
                var num = parseFloat(arg);
                if (!isNaN(num) && num > max) {
                    max = num;
                }
            }
        }
        return max === -Infinity ? 0 : max;
    }

    /**
     * MIN函数：返回最小值
     * @param {...number} args 要比较的数字参数
     * @returns {number} 最小值
     */
    function MIN() {
        var min = Infinity;
        for (var i = 0; i < arguments.length; i++) {
            var arg = arguments[i];
            if (Array.isArray(arg)) {
                var arrayMin = MIN.apply(null, arg);
                if (arrayMin < min) {
                    min = arrayMin;
                }
            } else if (typeof arg === 'number' && !isNaN(arg)) {
                if (arg < min) {
                    min = arg;
                }
            } else if (typeof arg === 'string') {
                var num = parseFloat(arg);
                if (!isNaN(num) && num < min) {
                    min = num;
                }
            }
        }
        return min === Infinity ? 0 : min;
    }

    // 导出所有函数
    return {
        LEFT: LEFT,
        RIGHT: RIGHT,
        MID: MID,
        LEN: LEN,
        UPPER: UPPER,
        LOWER: LOWER,
        SUM: SUM,
        AVERAGE: AVERAGE,
        MAX: MAX,
        MIN: MIN
    };
})();

// 为全局环境提供函数
if (typeof module !== 'undefined' && module.exports) {
    module.exports = FormulaJS;
}
