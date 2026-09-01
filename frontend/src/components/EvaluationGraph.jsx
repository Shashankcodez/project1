import React, { useState } from 'react';

export default function EvaluationGraph({ positions, selectedIndex, onSelectMove }) {
  const [hoveredIndex, setHoveredIndex] = useState(null);

  if (!positions || positions.length === 0) return null;

  const width = 600;
  const height = 150;
  const padLeft = 40;
  const padRight = 20;
  const padTop = 15;
  const padBottom = 25;

  const plotWidth = width - padLeft - padRight;
  const plotHeight = height - padTop - padBottom;
  const centerY = padTop + plotHeight / 2;
  const maxScore = 5.0; // clamp at +/- 5 pawns

  const getScoreValue = (pos) => {
    if (pos.mate !== null && pos.mate !== undefined) {
      return pos.mate > 0 ? 5.5 : -5.5;
    }
    if (pos.evaluation !== null && pos.evaluation !== undefined) {
      return Math.max(-5.0, Math.min(5.0, pos.evaluation));
    }
    return 0;
  };

  const points = positions.map((pos, i) => {
    const x = positions.length === 1
      ? padLeft + plotWidth / 2
      : padLeft + (i / (positions.length - 1)) * plotWidth;
    const score = getScoreValue(pos);
    const y = centerY - (score / maxScore) * (plotHeight / 2);
    return { x, y, score, pos, index: i };
  });

  const linePath = points.reduce((acc, pt, i) => `${acc} ${i === 0 ? 'M' : 'L'} ${pt.x} ${pt.y}`, '');

  // Positive fill path (above zero line)
  const posPoints = points.map((p) => ({ x: p.x, y: Math.min(centerY, p.y) }));
  const posAreaPath = `M ${points[0].x} ${centerY} ${posPoints.map((p) => `L ${p.x} ${p.y}`).join(' ')} L ${points[points.length - 1].x} ${centerY} Z`;

  // Negative fill path (below zero line)
  const negPoints = points.map((p) => ({ x: p.x, y: Math.max(centerY, p.y) }));
  const negAreaPath = `M ${points[0].x} ${centerY} ${negPoints.map((p) => `L ${p.x} ${p.y}`).join(' ')} L ${points[points.length - 1].x} ${centerY} Z`;

  const activeIndex = hoveredIndex !== null ? hoveredIndex : selectedIndex;
  const activePoint = points[activeIndex];

  return (
    <div className="eval-graph-container">
      <div className="graph-header">
        <span className="graph-title">Evaluation Timeline</span>
        {activePoint && (
          <span className="graph-point-info">
            Move {activePoint.pos.moveNumber}. {activePoint.pos.move} ({activePoint.pos.color}) :{' '}
            <strong>
              {activePoint.pos.mate !== null && activePoint.pos.mate !== undefined
                ? `#${activePoint.pos.mate > 0 ? '+' + activePoint.pos.mate : activePoint.pos.mate}`
                : activePoint.pos.evaluation > 0
                ? `+${activePoint.pos.evaluation.toFixed(2)}`
                : activePoint.pos.evaluation?.toFixed(2) || '0.00'}
            </strong>
          </span>
        )}
      </div>

      <div className="svg-wrapper">
        <svg viewBox={`0 0 ${width} ${height}`} className="eval-svg">
          <defs>
            <linearGradient id="whiteGrad" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor="#10b981" stopOpacity="0.45" />
              <stop offset="100%" stopColor="#10b981" stopOpacity="0.05" />
            </linearGradient>
            <linearGradient id="blackGrad" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor="#f43f5e" stopOpacity="0.05" />
              <stop offset="100%" stopColor="#f43f5e" stopOpacity="0.45" />
            </linearGradient>
          </defs>

          {/* Background Grid & Axis Lines */}
          <line x1={padLeft} y1={padTop} x2={width - padRight} y2={padTop} stroke="#334155" strokeDasharray="3,3" />
          <line x1={padLeft} y1={centerY} x2={width - padRight} y2={centerY} stroke="#64748b" strokeWidth="1.2" />
          <line x1={padLeft} y1={height - padBottom} x2={width - padRight} y2={height - padBottom} stroke="#334155" strokeDasharray="3,3" />

          {/* Y Axis Labels */}
          <text x={padLeft - 6} y={padTop + 4} className="axis-text" textAnchor="end">+5</text>
          <text x={padLeft - 6} y={centerY + 4} className="axis-text" textAnchor="end">0</text>
          <text x={padLeft - 6} y={height - padBottom + 4} className="axis-text" textAnchor="end">-5</text>

          {/* Fill Areas */}
          <path d={posAreaPath} fill="url(#whiteGrad)" />
          <path d={negAreaPath} fill="url(#blackGrad)" />

          {/* Curve Line */}
          <path d={linePath} fill="none" stroke="#f8fafc" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />

          {/* Active / Selected Position Vertical Guideline */}
          {points[selectedIndex] && (
            <line
              x1={points[selectedIndex].x}
              y1={padTop}
              x2={points[selectedIndex].x}
              y2={height - padBottom}
              stroke="#38bdf8"
              strokeWidth="1.5"
              strokeDasharray="2,2"
            />
          )}

          {/* Data Points */}
          {points.map((pt, i) => {
            const isSelected = i === selectedIndex;
            const isMate = pt.pos.mate !== null && pt.pos.mate !== undefined;
            return (
              <g key={i} onClick={() => onSelectMove(i)} style={{ cursor: 'pointer' }}>
                <circle
                  cx={pt.x}
                  cy={pt.y}
                  r={isSelected ? 5.5 : 3.5}
                  fill={isMate ? '#f59e0b' : isSelected ? '#38bdf8' : pt.score >= 0 ? '#10b981' : '#f43f5e'}
                  stroke="#0b0f19"
                  strokeWidth={isSelected ? 2 : 1}
                  onMouseEnter={() => setHoveredIndex(i)}
                  onMouseLeave={() => setHoveredIndex(null)}
                />
                {isMate && (
                  <text x={pt.x} y={pt.y - 8} className="mate-marker-text" textAnchor="middle">
                    #
                  </text>
                )}
              </g>
            );
          })}
        </svg>
      </div>
    </div>
  );
}
