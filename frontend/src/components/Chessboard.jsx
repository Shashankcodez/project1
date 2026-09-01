import React from 'react';
import Piece from './Piece';
import { algebraicToCoords } from '../utils/fenUtils';

const FILES = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];
const RANKS = [8, 7, 6, 5, 4, 3, 2, 1];

export default function Chessboard({ board, bestMove }) {
  let moveFrom = null;
  let moveTo = null;

  if (bestMove && bestMove.length >= 4) {
    moveFrom = algebraicToCoords(bestMove.substring(0, 2));
    moveTo = algebraicToCoords(bestMove.substring(2, 4));
  }

  return (
    <div className="chessboard-wrapper">
      <div className="chessboard">
        {RANKS.map((rank, row) => (
          <div key={rank} className="board-row">
            {FILES.map((file, col) => {
              const isLight = (row + col) % 2 === 0;
              const piece = board?.[row]?.[col];
              const isFrom = moveFrom && moveFrom.row === row && moveFrom.col === col;
              const isTo = moveTo && moveTo.row === row && moveTo.col === col;

              return (
                <div
                  key={`${file}${rank}`}
                  className={`square ${isLight ? 'light' : 'dark'} ${
                    isFrom ? 'highlight-from' : ''
                  } ${isTo ? 'highlight-to' : ''}`}
                >
                  {/* File label on bottom rank (row 7) */}
                  {row === 7 && <span className="coord-file">{file}</span>}

                  {/* Rank label on leftmost file (col 0) */}
                  {col === 0 && <span className="coord-rank">{rank}</span>}

                  {piece && <Piece symbol={piece} />}
                </div>
              );
            })}
          </div>
        ))}
      </div>
    </div>
  );
}
