/**
 * 防抖函数配置项
 */
interface DebounceOptions {
  delay: number; // 延迟时间（毫秒）
  immediate?: boolean; // 是否立即执行
  maxDelay?: number; // 最大延迟时间（毫秒）
  trailing?: boolean; // 是否在延迟期间保留最后一次调用的参数
}

/**
 * 节流函数配置项
 */
interface ThrottleOptions {
  delay: number; // 延迟时间（毫秒）
  immediate?: boolean; // 是否立即执行
  maxDelay?: number; // 最大延迟时间（毫秒）
  trailing?: boolean; // 是否在延迟期间保留最后一次调用的参数
}

/**
 * 防抖
 * @param func 要执行的函数
 * @param options 配置选项
 * @returns 防抖后的函数
 */
export function debounce<T extends (...args: any[]) => any>(
  func: T,
  options: DebounceOptions | number
): (...args: Parameters<T>) => void {
  const config = typeof options === 'number' 
    ? { delay: options, immediate: false, trailing: true } 
    : { immediate: false, trailing: true, ...options };
  
  let timeoutId: NodeJS.Timeout | null = null;
  let lastCallTime = 0;
  let lastArgs: Parameters<T> | null = null;
  let isImmediateCalled = false;
  
  return (...args: Parameters<T>) => {
    const now = Date.now();
    lastArgs = args;
    
    // 是否超过最大延迟时间
    if (config.maxDelay && now - lastCallTime > config.maxDelay) {
      if (timeoutId) {
        clearTimeout(timeoutId);
        timeoutId = null;
      }
      isImmediateCalled = false;
    }
    
    // 立即执行
    if (config.immediate && !isImmediateCalled) {
      func(...args);
      isImmediateCalled = true;
      lastCallTime = now;
      return;
    }
    
    if (timeoutId) {
      clearTimeout(timeoutId);
    }
    
    timeoutId = setTimeout(() => {
      if (config.trailing && lastArgs) {
        func(...lastArgs);
      }
      timeoutId = null;
      isImmediateCalled = false;
      lastArgs = null;
    }, config.delay);
    
    lastCallTime = now;
  };
}

/**
 * 节流函数 - 在指定时间内只执行一次
 * @param func 要执行的函数
 * @param options 配置选项
 * @returns 节流后的函数
 */
export function throttle<T extends (...args: any[]) => any>(
  func: T,
  options: ThrottleOptions | number
): (...args: Parameters<T>) => void {
  const config = typeof options === 'number' 
    ? { delay: options, immediate: true, trailing: true } 
    : { immediate: true, trailing: true, ...options };
  
  let lastCall = 0;
  let timeoutId: NodeJS.Timeout | null = null;
  let lastArgs: Parameters<T> | null = null;
  let isImmediateCalled = false;
  
  return (...args: Parameters<T>) => {
    const now = Date.now();
    lastArgs = args;
    
    // 是否超过最大延迟时间
    if (config.maxDelay && now - lastCall > config.maxDelay) {
      if (timeoutId) {
        clearTimeout(timeoutId);
        timeoutId = null;
      }
      isImmediateCalled = false;
    }
    
    // 立即执行
    if (config.immediate && !isImmediateCalled && now - lastCall >= config.delay) {
      func(...args);
      lastCall = now;
      isImmediateCalled = true;
      return;
    }
    
    // 延迟执行
    if (config.trailing) {
      if (timeoutId) {
        clearTimeout(timeoutId);
      }
      
      timeoutId = setTimeout(() => {
        if (lastArgs) {
          func(...lastArgs);
        }
        timeoutId = null;
        isImmediateCalled = false;
        lastArgs = null;
      }, config.delay - (now - lastCall));
    }
  };
}

/**
 * 可取消的防抖函数
 * @param func 要执行的函数
 * @param options 配置选项
 * @returns 防抖后的函数和取消函数
 */
export function debounceWithCancel<T extends (...args: any[]) => any>(
  func: T,
  options: DebounceOptions | number
): [debouncedFunc: (...args: Parameters<T>) => void, cancel: () => void] {
  const debouncedFunc = debounce(func, options);
  let timeoutId: NodeJS.Timeout | null = null;
  
  const cancel = () => {
    if (timeoutId) {
      clearTimeout(timeoutId);
      timeoutId = null;
    }
  };
  
  const wrappedFunc = (...args: Parameters<T>) => {
    debouncedFunc(...args);
  };
  
  return [wrappedFunc, cancel];
}

/**
 * 可取消的节流函数
 * @param func 要执行的函数
 * @param options 配置选项
 * @returns 节流后的函数和取消函数
 */
export function throttleWithCancel<T extends (...args: any[]) => any>(
  func: T,
  options: ThrottleOptions | number
): [throttledFunc: (...args: Parameters<T>) => void, cancel: () => void] {
  const throttledFunc = throttle(func, options);
  let timeoutId: NodeJS.Timeout | null = null;
  
  const cancel = () => {
    if (timeoutId) {
      clearTimeout(timeoutId);
      timeoutId = null;
    }
  };
  
  const wrappedFunc = (...args: Parameters<T>) => {
    throttledFunc(...args);
  };
  
  return [wrappedFunc, cancel];
}


