import React, { useState } from 'react';
import Chessboard from './components/Chessboard';
import FenInput, { PRESET_POSITIONS } from './components/FenInput';
import AnalysisPanel from './components/AnalysisPanel';
import PgnAnalysis from './components/PgnAnalysis';
import { parseFenToBoard } from './utils/fenUtils';
import { evaluatePosition } from './services/api';
import './App.css';

export default function App() {
  const [activeTab, setActiveTab] = useState('fen'); // 'fen' or 'pgn'

  // FEN Analysis State
  const [fen, setFen] = useState(PRESET_POSITIONS[0].fen);
  const [depth, setDepth] = useState(15);
  const [fenResult, setFenResult] = useState(null);
  const [fenLoading, setFenLoading] = useState(false);
  const [fenError, setFenError] = useState(null);

  // PGN Analysis Active Position State
  const [pgnActivePosition, setPgnActivePosition] = useState(null);
  const [pgnActiveIndex, setPgnActiveIndex] = useState(0);

  // Determine active board FEN and best move to display
  const currentBoardFen = activeTab === 'fen' ? fen : (pgnActivePosition?.fen || PRESET_POSITIONS[0].fen);
  const currentBestMove = activeTab === 'fen' ? fenResult?.bestMove : pgnActivePosition?.bestMove;

  const { board, activeColor, castling, enPassant, isValid } = parseFenToBoard(currentBoardFen);

  const handleAnalyzeFen = async () => {
    if (!fen.trim()) return;
    setFenLoading(true);
    setFenError(null);

    try {
      const data = await evaluatePosition(fen.trim(), depth);
      setFenResult(data);
    } catch (err) {
      setFenError(err.message || 'Failed to evaluate position');
      setFenResult(null);
    } finally {
      setFenLoading(false);
    }
  };

  const handleFenChange = (newFen) => {
    setFen(newFen);
    setFenError(null);
  };

  const handlePgnPositionSelect = (pos, idx) => {
    setPgnActivePosition(pos);
    setPgnActiveIndex(idx);
  };

  return (
    <div className="app-container">
      <header className="app-header">
        <div className="header-content">
          <div className="header-logo">
            <span className="logo-icon">♟</span>
            <div>
              <h1 className="app-title">Chess Position Evaluator</h1>
              <p className="app-subtitle">Stockfish UCI Engine Analysis</p>
            </div>
          </div>

          <div className="tab-navigation">
            <button
              type="button"
              className={`tab-btn ${activeTab === 'fen' ? 'active' : ''}`}
              onClick={() => setActiveTab('fen')}
            >
              Position Analysis (FEN)
            </button>
            <button
              type="button"
              className={`tab-btn ${activeTab === 'pgn' ? 'active' : ''}`}
              onClick={() => setActiveTab('pgn')}
            >
              Game Analysis (PGN)
            </button>
          </div>
        </div>
      </header>

      <main className="main-content">
        <div className="layout-grid">
          {/* Left Column: Board & State Details */}
          <section className="board-section">
            <Chessboard board={board} bestMove={currentBestMove} />

            <div className="board-meta">
              <div className="meta-item">
                <span className="meta-label">Turn:</span>
                <span className={`turn-indicator ${activeColor === 'w' ? 'white' : 'black'}`}>
                  {activeColor === 'w' ? 'White to move' : 'Black to move'}
                </span>
              </div>
              <div className="meta-item">
                <span className="meta-label">Castling:</span>
                <span className="meta-val">{castling || '-'}</span>
              </div>
              <div className="meta-item">
                <span className="meta-label">En Passant:</span>
                <span className="meta-val">{enPassant || '-'}</span>
              </div>
            </div>

            {activeTab === 'pgn' && pgnActivePosition && (
              <div className="pgn-active-summary">
                <div className="summary-move">
                  Move {pgnActivePosition.moveNumber}. {pgnActivePosition.move} ({pgnActivePosition.color})
                </div>
                <div className="summary-eval">
                  Eval:{' '}
                  <strong>
                    {pgnActivePosition.mate !== null && pgnActivePosition.mate !== undefined
                      ? `#${pgnActivePosition.mate > 0 ? '+' + pgnActivePosition.mate : pgnActivePosition.mate}`
                      : pgnActivePosition.evaluation > 0
                      ? `+${pgnActivePosition.evaluation.toFixed(2)}`
                      : pgnActivePosition.evaluation?.toFixed(2) || '0.00'}
                  </strong>
                  {pgnActivePosition.bestMove && (
                    <span className="summary-bestmove"> • Best: {pgnActivePosition.bestMove}</span>
                  )}
                </div>
              </div>
            )}

            {!isValid && currentBoardFen.trim() && (
              <div className="invalid-fen-warning">
                ⚠️ Invalid FEN structure. Please enter a valid 8-rank standard FEN string.
              </div>
            )}
          </section>

          {/* Right Column: Dynamic Analysis Tab Content */}
          <section className="controls-section">
            {activeTab === 'fen' ? (
              <>
                <div className="card control-card">
                  <FenInput
                    fen={fen}
                    onFenChange={handleFenChange}
                    depth={depth}
                    onDepthChange={setDepth}
                    onAnalyze={handleAnalyzeFen}
                    loading={fenLoading}
                  />
                </div>

                <div className="card analysis-card">
                  <AnalysisPanel
                    result={fenResult}
                    loading={fenLoading}
                    error={fenError}
                  />
                </div>
              </>
            ) : (
              <PgnAnalysis
                onPositionSelect={handlePgnPositionSelect}
                selectedPositionIndex={pgnActiveIndex}
              />
            )}
          </section>
        </div>
      </main>

      <footer className="app-footer">
        <p>Chess Position Evaluator • Powered by Java Spring Boot & Stockfish UCI</p>
      </footer>
    </div>
  );
}
