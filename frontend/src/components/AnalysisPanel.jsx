import React from 'react';

export default function AnalysisPanel({ result, loading, error }) {
  if (error) {
    return (
      <div className="analysis-panel error-state">
        <div className="error-badge">Analysis Error</div>
        <p className="error-message">{error}</p>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="analysis-panel loading-state">
        <div className="skeleton-loader">
          <div className="spinner-large"></div>
          <p>Stockfish is computing position...</p>
        </div>
      </div>
    );
  }

  if (!result) {
    return (
      <div className="analysis-panel empty-state">
        <p className="empty-hint">Enter or choose a FEN and click "Analyze Position" to get engine evaluation.</p>
      </div>
    );
  }

  const { evaluation, mate, bestMove, depth } = result;

  let evalDisplay = '0.00';
  let evalClass = 'equal';
  let evalSummary = 'Equal Position';

  if (mate !== null && mate !== undefined) {
    evalDisplay = `#${mate > 0 ? '+' + mate : mate}`;
    evalClass = mate > 0 ? 'white-winning' : 'black-winning';
    evalSummary = mate > 0 ? `White mates in ${mate}` : `Black mates in ${Math.abs(mate)}`;
  } else if (evaluation !== null && evaluation !== undefined) {
    const formatted = evaluation > 0 ? `+${evaluation.toFixed(2)}` : evaluation.toFixed(2);
    evalDisplay = formatted;

    if (evaluation > 1.5) {
      evalClass = 'white-winning';
      evalSummary = 'White has a strong advantage';
    } else if (evaluation > 0.3) {
      evalClass = 'white-advantage';
      evalSummary = 'White has a slight advantage';
    } else if (evaluation < -1.5) {
      evalClass = 'black-winning';
      evalSummary = 'Black has a strong advantage';
    } else if (evaluation < -0.3) {
      evalClass = 'black-advantage';
      evalSummary = 'Black has a slight advantage';
    } else {
      evalClass = 'equal';
      evalSummary = 'Balanced / Equal position';
    }
  }

  // Calculate evaluation bar percentage for White (0% = Black winning, 100% = White winning)
  let whitePercent = 50;
  if (mate !== null && mate !== undefined) {
    whitePercent = mate > 0 ? 100 : 0;
  } else if (evaluation !== null && evaluation !== undefined) {
    // Sigmoid mapping for smooth bar
    whitePercent = Math.min(100, Math.max(0, 50 + (2 / (1 + Math.exp(-0.4 * evaluation)) - 1) * 50));
  }

  return (
    <div className="analysis-panel">
      <div className="analysis-header">
        <h3 className="panel-title">Engine Analysis</h3>
        <span className="depth-badge">Depth: {depth}</span>
      </div>

      <div className="eval-bar-wrapper">
        <div className="eval-bar" style={{ width: `${whitePercent}%` }}></div>
      </div>

      <div className="eval-metrics-grid">
        <div className={`metric-card eval-card ${evalClass}`}>
          <span className="metric-label">Evaluation</span>
          <span className="metric-value eval-value">{evalDisplay}</span>
          <span className="metric-subtext">{evalSummary}</span>
        </div>

        <div className="metric-card bestmove-card">
          <span className="metric-label">Best Move</span>
          <span className="metric-value bestmove-value">{bestMove || '—'}</span>
          <span className="metric-subtext">Stockfish Recommendation</span>
        </div>
      </div>
    </div>
  );
}
