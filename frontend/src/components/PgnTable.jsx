import React from 'react';

export default function PgnTable({ positions, selectedIndex, onSelectPosition }) {
  if (!positions || positions.length === 0) return null;

  return (
    <div className="pgn-table-container">
      <div className="table-wrapper">
        <table className="pgn-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Side</th>
              <th>Move</th>
              <th>Eval</th>
              <th>Change</th>
              <th>Best Move</th>
            </tr>
          </thead>
          <tbody>
            {positions.map((pos, idx) => {
              const isSelected = idx === selectedIndex;

              let evalText = '0.00';
              if (pos.mate !== null && pos.mate !== undefined) {
                evalText = `#${pos.mate > 0 ? '+' + pos.mate : pos.mate}`;
              } else if (pos.evaluation !== null && pos.evaluation !== undefined) {
                evalText = pos.evaluation > 0 ? `+${pos.evaluation.toFixed(2)}` : pos.evaluation.toFixed(2);
              }

              let changeText = '—';
              let changeClass = '';
              if (pos.evaluationChange !== null && pos.evaluationChange !== undefined) {
                if (pos.evaluationChange > 0) {
                  changeText = `+${pos.evaluationChange.toFixed(2)}`;
                  changeClass = 'delta-positive';
                } else if (pos.evaluationChange < 0) {
                  changeText = pos.evaluationChange.toFixed(2);
                  changeClass = 'delta-negative';
                } else {
                  changeText = '0.00';
                  changeClass = 'delta-neutral';
                }
              }

              return (
                <tr
                  key={idx}
                  className={`table-row ${isSelected ? 'selected' : ''}`}
                  onClick={() => onSelectPosition(idx)}
                >
                  <td className="col-num">{pos.moveNumber}</td>
                  <td className="col-side">
                    <span className={`side-tag ${pos.color.toLowerCase()}`}>
                      {pos.color === 'WHITE' ? 'W' : 'B'}
                    </span>
                  </td>
                  <td className="col-move">
                    <strong>{pos.move}</strong>
                  </td>
                  <td className="col-eval">
                    <span className={`eval-pill ${pos.mate ? 'mate-pill' : ''}`}>{evalText}</span>
                  </td>
                  <td className={`col-change ${changeClass}`}>{changeText}</td>
                  <td className="col-bestmove">
                    <code>{pos.bestMove || '—'}</code>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}
