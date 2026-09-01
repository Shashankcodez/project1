export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

export async function evaluatePosition(fen, depth = 15) {
  const url = `${API_BASE_URL}/api/evaluate`;
  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ fen, depth }),
  });

  const data = await response.json();

  if (!response.ok) {
    throw new Error(data.message || data.error || `Error: ${response.status}`);
  }

  return data;
}

export async function analyzePgn(pgn, depth = 12) {
  const url = `${API_BASE_URL}/api/analyze/pgn`;
  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ pgn, depth }),
  });

  const data = await response.json();

  if (!response.ok) {
    throw new Error(data.message || data.error || `Error: ${response.status}`);
  }

  return data;
}
