import { getTokens } from './auth'

// POST SSE 流式请求（fetch + ReadableStream 逐块解析）
// 后端格式：每个事件一行 data:<文本>，流结束追加 data:[DONE]
// onMessage：逐个收到文本片段；流正常结束 resolve；出错 reject
export function streamSSE(url, body, { onMessage, signal } = {}) {
  return new Promise((resolve, reject) => {
    const tokens = getTokens()
    fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(tokens?.accessToken ? { Authorization: `Bearer ${tokens.accessToken}` } : {})
      },
      body: JSON.stringify(body),
      signal
    })
      .then(async (res) => {
        if (!res.ok) {
          const errText = await res.text().catch(() => '')
          throw new Error(errText || `HTTP ${res.status}`)
        }
        const reader = res.body.getReader()
        const decoder = new TextDecoder()
        let buffer = ''

        const handleBlock = (block) => {
          // 一个事件块内可能有多个 data: 行（多行文本），用 \n 拼接
          const data = block
            .split('\n')
            .filter((line) => line.startsWith('data:'))
            .map((line) => line.slice(5).replace(/^ /, ''))
            .join('\n')
          if (data) onMessage && onMessage(data)
        }

        while (true) {
          const { done, value } = await reader.read()
          if (done) break
          buffer += decoder.decode(value, { stream: true }).replace(/\r/g, '')
          let sep
          while ((sep = buffer.indexOf('\n\n')) !== -1) {
            const block = buffer.slice(0, sep)
            buffer = buffer.slice(sep + 2)
            handleBlock(block)
          }
        }
        if (buffer) handleBlock(buffer)
        resolve()
      })
      .catch(reject)
  })
}
