import { useState } from 'react';
import ConfigurationScreen from './components/ConfigurationScreen';
import BreathingScreen from './components/BreathingScreen';
import CompletionScreen from './components/CompletionScreen';
import HistoryScreen from './components/HistoryScreen';

export type BreathingConfig = {
  inhale: number;
  hold: number;
  exhale: number;
  sets: number;
};

export type SessionRecord = {
  date: string;
  pattern: string;
  sets: number;
  duration: number;
  presetName?: string;
};

type Screen = 'config' | 'breathing' | 'completion' | 'history';

export default function App() {
  const [currentScreen, setCurrentScreen] = useState<Screen>('config');
  const [config, setConfig] = useState<BreathingConfig>({
    inhale: 4,
    hold: 7,
    exhale: 8,
    sets: 4,
  });
  const [sessionData, setSessionData] = useState({
    completedSets: 0,
    duration: 0,
  });

  const handleStartBreathing = (newConfig: BreathingConfig) => {
    setConfig(newConfig);
    setCurrentScreen('breathing');
  };

  const handleComplete = (completedSets: number, duration: number) => {
    setSessionData({ completedSets, duration });
    
    // Save to history
    const record: SessionRecord = {
      date: new Date().toISOString(),
      pattern: `${config.inhale}-${config.hold}-${config.exhale}`,
      sets: completedSets,
      duration: duration,
    };
    
    const history = JSON.parse(localStorage.getItem('breathingHistory') || '[]');
    history.unshift(record);
    
    // Keep only last 100 records
    if (history.length > 100) {
      history.pop();
    }
    
    localStorage.setItem('breathingHistory', JSON.stringify(history));
    
    setCurrentScreen('completion');
  };

  const handleBackToConfig = () => {
    setCurrentScreen('config');
  };

  const handleShowHistory = () => {
    setCurrentScreen('history');
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-[#667eea] to-[#764ba2] text-white">
      {currentScreen === 'config' && (
        <ConfigurationScreen
          onStart={handleStartBreathing}
          onShowHistory={handleShowHistory}
        />
      )}
      {currentScreen === 'breathing' && (
        <BreathingScreen
          config={config}
          onComplete={handleComplete}
          onBack={handleBackToConfig}
        />
      )}
      {currentScreen === 'completion' && (
        <CompletionScreen
          config={config}
          completedSets={sessionData.completedSets}
          duration={sessionData.duration}
          onRestart={handleStartBreathing}
          onBackToHome={handleBackToConfig}
          onShowHistory={handleShowHistory}
        />
      )}
      {currentScreen === 'history' && (
        <HistoryScreen onBack={handleBackToConfig} />
      )}
    </div>
  );
}
