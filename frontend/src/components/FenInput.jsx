import React from 'react';

export const PRESET_POSITIONS = [
  {
    name: 'Starting Position',
    fen: 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1',
    description: 'Initial standard chess setup',
  },
  {
    name: "Scholar's Mate Threat",
    fen: 'r1bqkb1r/pppp1ppp/2n2n2/4p2Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 4 4',
    description: 'White can play Qxf7# mate in 1',
  },
  {
    name: 'Tactical Position',
    fen: 'r1b1k2r/pppp1ppp/8/4N3/1b1qn3/8/PPPP1PPP/RNBQKB1R w KQkq - 0 1',
    description: 'Sharp middle-game tactical position',
  },
  {
    name: 'Queen Endgame',
    fen: '8/8/4k3/8/8/4K3/4Q3/8 w - - 0 1',
    description: 'King and Queen endgame technique',
  },
];

export default function FenInput({
  fen,
  onFenChange,
  depth,
  onDepthChange,
  onAnalyze,
  loading,
}) {
  return (
    <div className="fen-input-container">
      <div className="preset-buttons">
        <label className="section-label">Presets:</label>
        <div className="presets-list">
          {PRESET_POSITIONS.map((preset) => (
            <button
              key={preset.name}
              type="button"
              className={`preset-btn ${fen === preset.fen ? 'active' : ''}`}
              onClick={() => onFenChange(preset.fen)}
              title={preset.description}
            >
              {preset.name}
            </button>
          ))}
        </div>
      </div>

      <div className="form-group">
        <label htmlFor="fen-input" className="section-label">
          FEN String:
        </label>
        <div className="input-row">
          <input
            id="fen-input"
            type="text"
            className="fen-text-input"
            value={fen}
            onChange={(e) => onFenChange(e.target.value)}
            placeholder="Paste or type a FEN string..."
            spellCheck="false"
          />
        </div>
      </div>

      <div className="controls-row">
        <div className="depth-control">
          <label htmlFor="depth-input" className="depth-label">
            Depth:
          </label>
          <input
            id="depth-input"
            type="number"
            min="1"
            max="30"
            className="depth-input"
            value={depth}
            onChange={(e) => onDepthChange(parseInt(e.target.value, 10) || 1)}
          />
        </div>

        <button
          type="button"
          className="analyze-button"
          onClick={onAnalyze}
          disabled={loading || !fen.trim()}
        >
          {loading ? (
            <span className="spinner-label">
              <span className="spinner"></span> Analyzing...
            </span>
          ) : (
            'Analyze Position'
          )}
        </button>
      </div>
    </div>
  );
}
