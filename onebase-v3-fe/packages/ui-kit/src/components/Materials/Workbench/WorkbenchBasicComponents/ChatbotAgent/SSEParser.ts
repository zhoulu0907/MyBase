export interface SSEData {
  delta?: string;
  content?: string;
  done?: boolean;
  error?: string;
}

export class SSEParser {
  private decoder = new TextDecoder();

  async parse(
    response: Response,
    onMessage: (data: SSEData) => void,
    onError?: (error: Error) => void
  ): Promise<void> {
    const reader = response.body?.getReader();
    if (!reader) {
      onError?.(new Error('No response body'));
      return;
    }

    let buffer = '';

    try {
      while (true) {
        const { done, value } = await reader.read();
        if (done) break;

        buffer += this.decoder.decode(value, { stream: true });
        const lines = buffer.split('\n');
        buffer = lines.pop() || '';

        for (const line of lines) {
          if (line.startsWith('data: ')) {
            const data = line.slice(6).trim();
            if (data === '[DONE]') {
              onMessage({ done: true });
              return;
            }
            try {
              const parsed = JSON.parse(data) as SSEData;
              onMessage(parsed);
              if (parsed.done) {
                return;
              }
            } catch (e) {
              // ignore parse error for non-JSON data
            }
          }
        }
      }
    } catch (error: any) {
      onError?.(error);
    }
  }
}