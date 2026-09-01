export function parseFenToBoard(fen) {
  const emptyBoard = Array(8).fill(null).map(() => Array(8).fill(null));

  if (!fen || typeof fen !== 'string') {
    return { board: emptyBoard, isValid: false, activeColor: 'w' };
  }

  const parts = fen.trim().split(/\s+/);
  const placement = parts[0];
  const activeColor = parts[1] || 'w';
  const castling = parts[2] || '-';
  const enPassant = parts[3] || '-';
  const halfmove = parts[4] || '0';
  const fullmove = parts[5] || '1';

  const rows = placement.split('/');
  if (rows.length !== 8) {
    return { board: emptyBoard, isValid: false, activeColor };
  }

  const board = Array(8).fill(null).map(() => Array(8).fill(null));

  for (let r = 0; r < 8; r++) {
    const rowStr = rows[r];
    let c = 0;

    for (let i = 0; i < rowStr.length; i++) {
      const char = rowStr[i];
      if (char >= '1' && char <= '8') {
        c += parseInt(char, 10);
      } else if ('pnbrqkPNBRQK'.includes(char)) {
        if (c < 8) {
          board[r][c] = char;
        }
        c++;
      } else {
        return { board: emptyBoard, isValid: false, activeColor };
      }
    }

    if (c !== 8) {
      return { board: emptyBoard, isValid: false, activeColor };
    }
  }

  return {
    board,
    activeColor,
    castling,
    enPassant,
    halfmove,
    fullmove,
    isValid: true,
  };
}

export function algebraicToCoords(squareStr) {
  if (!squareStr || squareStr.length !== 2) return null;
  const file = squareStr[0].toLowerCase();
  const rank = parseInt(squareStr[1], 10);

  if (file < 'a' || file > 'h' || isNaN(rank) || rank < 1 || rank > 8) {
    return null;
  }

  const col = file.charCodeAt(0) - 'a'.charCodeAt(0);
  const row = 8 - rank;
  return { row, col };
}
