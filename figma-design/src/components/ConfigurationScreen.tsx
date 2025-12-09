import { useState, useEffect } from 'react';
import { Settings } from 'lucide-react';
import { BreathingConfig } from '../App';
import SettingsModal from './SettingsModal';

type Preset = {
  name: string;
  config: BreathingConfig;
  isDefault?: boolean;
};

const DEFAULT_PRESETS: Preset[] = [
  {
    name: '4-7-8 호흡법',
    config: { inhale: 4, hold: 7, exhale: 8, sets: 4 },
    isDefault: true,
  },
  {
    name: '5-5-5 균형 호흡',
    config: { inhale: 5, hold: 5, exhale: 5, sets: 5 },
    isDefault: true,
  },
  {
    name: '6-6-6 깊은 호흡',
    config: { inhale: 6, hold: 6, exhale: 6, sets: 3 },
    isDefault: true,
  },
];

type ConfigurationScreenProps = {
  onStart: (config: BreathingConfig) => void;
  onShowHistory: () => void;
};

export default function ConfigurationScreen({ onStart, onShowHistory }: ConfigurationScreenProps) {
  const [config, setConfig] = useState<BreathingConfig>({
    inhale: 4,
    hold: 7,
    exhale: 8,
    sets: 4,
  });
  const [presets, setPresets] = useState<Preset[]>(DEFAULT_PRESETS);
  const [selectedPreset, setSelectedPreset] = useState<string>('4-7-8 호흡법');
  const [showSettings, setShowSettings] = useState(false);
  const [showSaveModal, setShowSaveModal] = useState(false);
  const [presetName, setPresetName] = useState('');

  useEffect(() => {
    const saved = localStorage.getItem('userPresets');
    if (saved) {
      const userPresets = JSON.parse(saved);
      setPresets([...DEFAULT_PRESETS, ...userPresets]);
    }
  }, []);

  const handlePresetChange = (presetName: string) => {
    setSelectedPreset(presetName);
    const preset = presets.find(p => p.name === presetName);
    if (preset) {
      setConfig(preset.config);
    }
  };

  const handleConfigChange = (key: keyof BreathingConfig, delta: number) => {
    setConfig(prev => {
      const newValue = Math.max(1, Math.min(20, prev[key] + delta));
      return { ...prev, [key]: newValue };
    });
    setSelectedPreset('커스텀');
  };

  const handleQuickSelect = (pattern: string) => {
    const [inhale, hold, exhale] = pattern.split('-').map(Number);
    setConfig(prev => ({ ...prev, inhale, hold, exhale }));
    setSelectedPreset('커스텀');
  };

  const handleSavePreset = () => {
    if (!presetName.trim()) return;

    const newPreset: Preset = {
      name: presetName,
      config: { ...config },
    };

    const userPresets = presets.filter(p => !p.isDefault);
    userPresets.push(newPreset);
    
    localStorage.setItem('userPresets', JSON.stringify(userPresets));
    setPresets([...DEFAULT_PRESETS, ...userPresets]);
    setSelectedPreset(presetName);
    setPresetName('');
    setShowSaveModal(false);
  };

  const getDescription = () => {
    if (config.inhale === 4 && config.hold === 7 && config.exhale === 8) {
      return '4-7-8 호흡법: 4초 들이쉬고 7초 멈추고 8초 내쉽니다';
    }
    if (config.inhale === config.hold && config.hold === config.exhale) {
      return `균형 호흡법: ${config.inhale}초씩 일정하게 호흡합니다`;
    }
    return `들숨 ${config.inhale}초, 멈춤 ${config.hold}초, 날숨 ${config.exhale}초로 호흡합니다`;
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-5 md:p-8">
      {/* Settings Button */}
      <button
        onClick={() => setShowSettings(true)}
        className="absolute top-8 right-5 md:right-8 w-12 h-12 flex items-center justify-center rounded-full hover:bg-white/10 transition-colors"
        aria-label="설정"
      >
        <Settings className="w-6 h-6" />
      </button>

      {/* Title */}
      <h1 className="mb-10 text-center">호흡 설정 🫁</h1>

      {/* Card Container */}
      <div className="w-full max-w-[400px] bg-white/10 backdrop-blur-md rounded-[20px] p-8 shadow-[0_8px_32px_rgba(0,0,0,0.1)]">
        {/* Preset Dropdown */}
        <div className="mb-6">
          <select
            value={selectedPreset}
            onChange={(e) => handlePresetChange(e.target.value)}
            className="w-full h-12 bg-white/15 rounded-xl px-4 text-white border-none outline-none cursor-pointer hover:bg-white/20 transition-colors"
          >
            <option value="커스텀" className="bg-[#667eea] text-white">커스텀</option>
            {presets.map(preset => (
              <option key={preset.name} value={preset.name} className="bg-[#667eea] text-white">
                {preset.name}
              </option>
            ))}
          </select>
        </div>

        {/* Number Inputs */}
        <div className="space-y-3 mb-6">
          <NumberInput
            label="들숨:"
            value={config.inhale}
            unit="초"
            onDecrement={() => handleConfigChange('inhale', -1)}
            onIncrement={() => handleConfigChange('inhale', 1)}
          />
          <NumberInput
            label="멈춤:"
            value={config.hold}
            unit="초"
            onDecrement={() => handleConfigChange('hold', -1)}
            onIncrement={() => handleConfigChange('hold', 1)}
          />
          <NumberInput
            label="날숨:"
            value={config.exhale}
            unit="초"
            onDecrement={() => handleConfigChange('exhale', -1)}
            onIncrement={() => handleConfigChange('exhale', 1)}
          />
          <NumberInput
            label="세트:"
            value={config.sets}
            unit="회"
            onDecrement={() => handleConfigChange('sets', -1)}
            onIncrement={() => handleConfigChange('sets', 1)}
          />
        </div>

        {/* Quick Select */}
        <div className="mb-6">
          <p className="text-xs opacity-70 mb-2">빠른 선택</p>
          <div className="flex gap-2">
            {['4-7-8', '5-5-5', '6-6-6'].map(pattern => (
              <button
                key={pattern}
                onClick={() => handleQuickSelect(pattern)}
                className="flex-1 h-9 bg-white/15 rounded-[18px] hover:bg-white/25 transition-all hover:-translate-y-0.5"
              >
                {pattern}
              </button>
            ))}
          </div>
        </div>

        {/* Save Preset Button */}
        <button
          onClick={() => setShowSaveModal(true)}
          className="w-full h-12 bg-white/15 border border-white/30 rounded-xl hover:bg-white/25 hover:-translate-y-0.5 transition-all mb-4"
        >
          💾 프리셋 저장하기
        </button>

        {/* Start Button */}
        <button
          onClick={() => onStart(config)}
          className="w-full h-14 bg-gradient-to-r from-[#4ade80] to-[#22d3ee] rounded-[28px] shadow-[0_4px_15px_rgba(0,0,0,0.2)] hover:-translate-y-0.5 hover:shadow-[0_6px_20px_rgba(0,0,0,0.3)] transition-all mb-3"
        >
          🫁 호흡 시작하기
        </button>

        {/* History Button */}
        <button
          onClick={onShowHistory}
          className="w-full h-12 bg-white/10 border border-white/30 rounded-[24px] hover:bg-white/20 transition-colors"
        >
          📊 기록 보기
        </button>

        {/* Description */}
        <p className="text-xs opacity-70 text-center mt-6 leading-relaxed">
          {getDescription()}
        </p>
      </div>

      {/* Settings Modal */}
      {showSettings && (
        <SettingsModal onClose={() => setShowSettings(false)} />
      )}

      {/* Save Preset Modal */}
      {showSaveModal && (
        <div className="fixed inset-0 bg-black/80 backdrop-blur-sm flex items-center justify-center z-50 p-5">
          <div className="bg-gradient-to-br from-[#667eea] to-[#764ba2] w-full max-w-sm rounded-[20px] p-8 shadow-[0_20px_60px_rgba(0,0,0,0.3)]">
            <h2 className="text-center mb-6">프리셋 저장</h2>
            <input
              type="text"
              value={presetName}
              onChange={(e) => setPresetName(e.target.value)}
              placeholder="프리셋 이름을 입력하세요"
              className="w-full h-12 bg-white/20 rounded-xl px-4 mb-6 outline-none placeholder:text-white/50"
              autoFocus
            />
            <div className="flex gap-3">
              <button
                onClick={() => setShowSaveModal(false)}
                className="flex-1 h-12 bg-white/15 border border-white/30 rounded-xl hover:bg-white/25 transition-colors"
              >
                취소
              </button>
              <button
                onClick={handleSavePreset}
                disabled={!presetName.trim()}
                className="flex-1 h-12 bg-gradient-to-r from-[#4ade80] to-[#22d3ee] rounded-xl hover:-translate-y-0.5 transition-all disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:translate-y-0"
              >
                저장
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

type NumberInputProps = {
  label: string;
  value: number;
  unit: string;
  onDecrement: () => void;
  onIncrement: () => void;
};

function NumberInput({ label, value, unit, onDecrement, onIncrement }: NumberInputProps) {
  return (
    <div className="flex items-center gap-3 h-14">
      <span className="w-16">{label}</span>
      <div className="flex-1 h-12 bg-white/20 rounded-lg flex items-center justify-center">
        <span className="text-2xl">{value}</span>
        <span className="ml-1 text-sm opacity-80">{unit}</span>
      </div>
      <button
        onClick={onDecrement}
        className="w-10 h-10 bg-white/15 rounded-full flex items-center justify-center hover:bg-white/25 transition-colors"
        aria-label="감소"
      >
        -
      </button>
      <button
        onClick={onIncrement}
        className="w-10 h-10 bg-white/15 rounded-full flex items-center justify-center hover:bg-white/25 transition-colors"
        aria-label="증가"
      >
        +
      </button>
    </div>
  );
}
