import { useEffect, useRef } from 'react';
import { Volume2 } from 'lucide-react';

type VolumeControlProps = {
  settings: {
    volume: number;
    voiceGuide: boolean;
    soundEffects: boolean;
  };
  onSettingsChange: (settings: any) => void;
  onClose: () => void;
};

export default function VolumeControl({ settings, onSettingsChange, onClose }: VolumeControlProps) {
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (ref.current && !ref.current.contains(event.target as Node)) {
        onClose();
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [onClose]);

  return (
    <div
      ref={ref}
      className="absolute top-14 right-0 w-40 bg-white/95 text-gray-800 rounded-xl p-4 shadow-[0_4px_20px_rgba(0,0,0,0.2)] z-50"
      style={{ animation: 'fadeIn 0.2s' }}
    >
      {/* Volume Icon */}
      <div className="flex justify-center mb-3">
        <Volume2 className="w-6 h-6" />
      </div>

      {/* Volume Slider */}
      <div className="mb-4">
        <input
          type="range"
          min="0"
          max="100"
          value={settings.volume}
          onChange={(e) => onSettingsChange({ ...settings, volume: parseInt(e.target.value) })}
          className="w-full h-1 bg-gray-300 rounded-full appearance-none cursor-pointer
            [&::-webkit-slider-thumb]:appearance-none
            [&::-webkit-slider-thumb]:w-4
            [&::-webkit-slider-thumb]:h-4
            [&::-webkit-slider-thumb]:rounded-full
            [&::-webkit-slider-thumb]:bg-[#667eea]
            [&::-webkit-slider-thumb]:cursor-pointer
            [&::-moz-range-thumb]:w-4
            [&::-moz-range-thumb]:h-4
            [&::-moz-range-thumb]:rounded-full
            [&::-moz-range-thumb]:bg-[#667eea]
            [&::-moz-range-thumb]:border-0
            [&::-moz-range-thumb]:cursor-pointer"
        />
        <div className="text-center text-xs mt-1 text-gray-600">{settings.volume}%</div>
      </div>

      {/* Voice Guide Toggle */}
      <div className="flex items-center justify-between text-sm">
        <span>음성</span>
        <button
          onClick={() => onSettingsChange({ ...settings, voiceGuide: !settings.voiceGuide })}
          className={`relative w-10 h-5 rounded-full transition-colors ${
            settings.voiceGuide ? 'bg-[#4ade80]' : 'bg-gray-300'
          }`}
        >
          <div
            className={`absolute top-0.5 w-4 h-4 bg-white rounded-full transition-transform ${
              settings.voiceGuide ? 'translate-x-5' : 'translate-x-0.5'
            }`}
          />
        </button>
      </div>
    </div>
  );
}
