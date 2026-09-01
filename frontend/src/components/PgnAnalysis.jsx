import React, { useState } from 'react';
import EvaluationGraph from './EvaluationGraph';
import PgnTable from './PgnTable';
import { analyzePgn } from '../services/api';

export const PRESET_PGNS = [
  {
    name: "Scholar's Mate",
    description: '4-move classic tactical checkmate',
    pgn: '1. e4 e5 2. Qh5 Nc6 3. Bc4 Nf6 4. Qxf7# 1-0',
  },
  {
    name: 'Opera Game (Opening)',
    description: 'Paul Morphy attacking miniature',
    pgn: '1. e4 e5 2. Nf3 d6 3. d4 Bg4 4. dxe5 Bxf3 5. Qxf3 dxe5 6. Bc4 Nf6 7. Qb3 Qe7 8. Nc3 c6 9. Bg5 b5 10. Nxb5',
  },
  {
    name: 'Italian Game',
    description: 'Standard classical open game',
    pgn: '1. e4 e5 2. Nf3 Nc6 3. Bc4 Bc5 4. c3 Nf6 5. d4 exd4 6. cxd4 Bb4+ 7. Bd2 Bxd2+ 8. Nbxd2 d5',
  },
];

export default function PgnAnalysis({ onPositionSelect, selectedPositionIndex }) {
  const [pgnText, setPgnText] = useState(PRESET_PGNS[0].pgn);
  const [depth, setDepth] = useState(12);
  const [positions, setPositions] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [currentIndex, setCurrentIndex] = useState(0);

  const handleAnalyze = async () => {
    if (!pgnText.trim()) return;
    setLoading(true);
    setError(null);

    try {
      const data = await analyzePgn(pgnText.trim(), depth);
      setPositions(data.positions || []);
      if (data.positions && data.positions.length > 0) {
        const lastIdx = data.positions.length - 1;
        setCurrentIndex(lastIdx);
        onPositionSelect(data.positions[lastIdx], lastIdx);
      }
    } catch (err) {
      setError(err.message || 'Failed to analyze PGN game');
      setPositions(null);
    } finally {
      setLoading(false);
    }
  };

  const handleSelectIndex = (idx) => {
    if (!positions || idx < 0 || idx >= positions.length) return;
    setCurrentIndex(idx);
    onPositionSelect(positions[idx], idx);
  };

  return (
    <div className="pgn-analysis-section">
      <div className="card pgn-input-card">
        <div className="preset-buttons">
          <label className="section-label">Game Presets:</label>
          <div className="presets-list">
            {PRESET_PGNS.map((preset) => (
              <button
                key={preset.name}
                type="button"
                className={`preset-btn ${pgnText === preset.pgn ? 'active' : ''}`}
                onClick={() => setPgnText(preset.pgn)}
                title={preset.description}
              >
                {preset.name}
              </button>
            ))}
          </div>
        </div>

        <div className="form-group">
          <label htmlFor="pgn-textarea" className="section-label">
            PGN Game Text:
          </label>
          <textarea
            id="pgn-textarea"
            className="pgn-textarea"
            rows="4"
            value={pgnText}
            onChange={(e) => setPgnText(e.target.value)}
            placeholder="Paste standard PGN text (e.g. 1. e4 e5 2. Nf3...)"
            spellCheck="false"
          />
        </div>

        <div className="controls-row">
          <div className="depth-control">
            <label htmlFor="pgn-depth-input" className="depth-label">
              Depth:
            </label>
            <input
              id="pgn-depth-input"
              type="number"
              min="1"
              max="25"
              className="depth-input"
              value={depth}
              onChange={(e) => setDepth(parseInt(e.target.value, 10) || 1)}
            />
          </div>

          <button
            type="button"
            className="analyze-button"
            onClick={handleAnalyze}
            disabled={loading || !pgnText.trim()}
          >
            {loading ? (
              <span className="spinner-label">
                <span className="spinner"></span> Analyzing Game...
              </span>
            ) : (
              'Analyze Game'
            )}
          </button>
        </div>

        {loading && (
          <div className="long-analysis-hint">
            ⏳ Running Stockfish analysis for each move. This may take a few moments for longer games.
          </div>
        )}

        {error && (
          <div className="error-state" style={{ marginTop: '1rem' }}>
            <div className="error-badge">PGN Analysis Error</div>
            <p className="error-message">{error}</p>
          </div>
        )}
      </div>

      {positions && positions.length > 0 && (
        <div className="card pgn-results-card">
          <div className="pgn-results-header">
            <h3 className="panel-title">Game Analysis ({positions.length} moves evaluated)</h3>

            {/* Move Navigator */}
            <div className="move-navigator">
              <button
                type="button"
                className="nav-btn"
                onClick={() => handleSelectIndex(0)}
                disabled={currentIndex === 0}
                title="First move"
              >
                «
              </button>
              <button
                type="button"
                className="nav-btn"
                onClick={() => handleSelectIndex(currentIndex - 1)}
                disabled={currentIndex === 0}
                title="Previous move"
              >
                ‹
              </button>
              <span className="nav-step">
                Move {currentIndex + 1} of {positions.length}
              </span>
              <button
                type="button"
                className="nav-btn"
                onClick={() => handleSelectIndex(currentIndex + 1)}
                disabled={currentIndex === positions.length - 1}
                title="Next move"
              >
                ›
              </button>
              <button
                type="button"
                className="nav-btn"
                onClick={() => handleSelectIndex(positions.length - 1)}
                disabled={currentIndex === positions.length - 1}
                title="Last move"
              >
                »
              </button>
            </div>
          </div>

          <EvaluationGraph
            positions={positions}
            selectedIndex={currentIndex}
            onSelectMove={handleSelectIndex}
          />

          <PgnTable
            positions={positions}
            selectedIndex={currentIndex}
            onSelectPosition={handleSelectIndex}
          />
        </div>
      )}
    </div>
  );
}
