import { useState, useEffect } from 'react';
import { X } from 'lucide-react';

type SettingsModalProps = {
  onClose: () => void;
};

type AudioSettings = {
  volume: number;
  voiceGuide: boolean;
  soundEffects: boolean;
  backgroundSound: string;
};

export default function SettingsModal({ onClose }: SettingsModalProps) {
  const [settings, setSettings] = useState<AudioSettings>(() => {
    const saved = localStorage.getItem('audioSettings');
    return saved ? JSON.parse(saved) : {
      volume: 60,
      voiceGuide: true,
      soundEffects: true,
      backgroundSound: 'none',
    };
  });

  const handleSave = () => {
    localStorage.setItem('audioSettings', JSON.stringify(settings));
    onClose();
  };

  const updateSetting = <K extends keyof AudioSettings>(key: K, value: AudioSettings[K]) => {
    setSettings(prev => ({ ...prev, [key]: value }));
  };

  return (
    <div className="fixed inset-0 bg-black/80 backdrop-blur-sm flex items-center justify-center z-50 p-5">
      <div className="bg-gradient-to-br from-[#667eea] to-[#764ba2] w-full max-w-[500px] max-h-[80vh] overflow-y-auto rounded-[20px] p-8 shadow-[0_20px_60px_rgba(0,0,0,0.3)]">
        {/* Header */}
        <div className="flex items-center justify-center relative mb-8">
          <h2 className="text-center">설정</h2>
          <button
            onClick={onClose}
            className="absolute right-0 w-8 h-8 flex items-center justify-center text-[28px] hover:text-[#ff6b6b] hover:scale-110 transition-all"
            aria-label="닫기"
          >
            <X className="w-7 h-7" />
          </button>
        </div>

        {/* Sound Settings */}
        <div className="mb-8">
          <h3 className="mb-4 flex items-center gap-2">
            🔊 소리 설정
          </h3>
          
          {/* Volume Slider */}
          <div className="mb-4">
            <div className="flex items-center justify-between mb-2">
              <span className="text-sm">효과음</span>
              <span className="text-xs">{settings.volume}%</span>
            </div>
            <input
              type="range"
              min="0"
              max="100"
              value={settings.volume}
              onChange={(e) => updateSetting('volume', parseInt(e.target.value))}
              className="w-full h-1 bg-white/20 rounded-full appearance-none cursor-pointer
                [&::-webkit-slider-thumb]:appearance-none
                [&::-webkit-slider-thumb]:w-5
                [&::-webkit-slider-thumb]:h-5
                [&::-webkit-slider-thumb]:rounded-full
                [&::-webkit-slider-thumb]:bg-white
                [&::-webkit-slider-thumb]:cursor-pointer
                [&::-moz-range-thumb]:w-5
                [&::-moz-range-thumb]:h-5
                [&::-moz-range-thumb]:rounded-full
                [&::-moz-range-thumb]:bg-white
                [&::-moz-range-thumb]:border-0
                [&::-moz-range-thumb]:cursor-pointer"
            />
          </div>

          {/* Voice Guide Toggle */}
          <div className="flex items-center justify-between mb-4">
            <span className="text-sm">음성가이드</span>
            <Toggle
              checked={settings.voiceGuide}
              onChange={(checked) => updateSetting('voiceGuide', checked)}
            />
          </div>

          {/* Sound Effects Toggle */}
          <div className="flex items-center justify-between mb-4">
            <span className="text-sm">효과음</span>
            <Toggle
              checked={settings.soundEffects}
              onChange={(checked) => updateSetting('soundEffects', checked)}
            />
          </div>

          {/* Background Sound */}
          <div className="mb-4">
            <span className="text-sm block mb-2">배경음</span>
            <select
              value={settings.backgroundSound}
              onChange={(e) => updateSetting('backgroundSound', e.target.value)}
              className="w-full h-10 bg-white/15 rounded-lg px-3 text-white text-sm border-none outline-none cursor-pointer"
            >
              <option value="none" className="bg-[#667eea]">없음</option>
              <option value="waves" className="bg-[#667eea]">파도소리</option>
              <option value="rain" className="bg-[#667eea]">빗소리</option>
              <option value="whitenoise" className="bg-[#667eea]">백색소음</option>
            </select>
          </div>
        </div>

        {/* App Info */}
        <div className="mb-8 p-4 bg-white/10 rounded-xl">
          <h3 className="mb-2 flex items-center gap-2">
            ℹ️ 앱 정보
          </h3>
          <p className="text-sm text-white/80 leading-relaxed">
            숨쉬기 v1.0<br />
            명상과 이완을 위한 호흡 가이드 앱입니다.
          </p>
        </div>

        {/* Close Button */}
        <button
          onClick={handleSave}
          className="w-full h-12 bg-white/20 rounded-[24px] hover:bg-white/30 transition-colors"
        >
          닫기
        </button>
      </div>
    </div>
  );
}

type ToggleProps = {
  checked: boolean;
  onChange: (checked: boolean) => void;
};

function Toggle({ checked, onChange }: ToggleProps) {
  return (
    <button
      onClick={() => onChange(!checked)}
      className={`relative w-12 h-6 rounded-full transition-colors ${
        checked ? 'bg-[#4ade80]' : 'bg-white/20'
      }`}
    >
      <div
        className={`absolute top-0.5 w-5 h-5 bg-white rounded-full transition-transform ${
          checked ? 'translate-x-6' : 'translate-x-0.5'
        }`}
      />
    </button>
  );
}
