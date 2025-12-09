import { Pause, Play, RotateCcw, Square } from 'lucide-react';

type PauseModalProps = {
  onResume: () => void;
  onRestart: () => void;
  onStop: () => void;
};

export default function PauseModal({ onResume, onRestart, onStop }: PauseModalProps) {
  return (
    <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50">
      <div className="bg-gradient-to-br from-[#667eea] to-[#764ba2] w-[320px] rounded-[20px] p-10">
        {/* Title */}
        <h2 className="text-center mb-6">일시정지 중</h2>

        {/* Icon */}
        <div className="flex justify-center mb-8">
          <Pause className="w-16 h-16 opacity-60" />
        </div>

        {/* Buttons */}
        <div className="space-y-3">
          <button
            onClick={onResume}
            className="w-full h-12 bg-gradient-to-r from-[#4ade80] to-[#22d3ee] rounded-xl hover:-translate-y-0.5 transition-transform flex items-center justify-center gap-2"
          >
            <Play className="w-5 h-5" />
            계속하기
          </button>
          <button
            onClick={onRestart}
            className="w-full h-12 bg-white/15 border border-white/30 rounded-xl hover:bg-white/25 transition-colors flex items-center justify-center gap-2"
          >
            <RotateCcw className="w-5 h-5" />
            처음부터
          </button>
          <button
            onClick={onStop}
            className="w-full h-12 bg-[#ff6b6b]/20 border border-[#ff6b6b]/50 rounded-xl hover:bg-[#ff6b6b]/30 transition-colors flex items-center justify-center gap-2 text-[#ff6b6b]"
          >
            <Square className="w-5 h-5" />
            종료하기
          </button>
        </div>
      </div>
    </div>
  );
}
