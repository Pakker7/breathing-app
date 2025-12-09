import { BreathingConfig } from '../App';

type CompletionScreenProps = {
  config: BreathingConfig;
  completedSets: number;
  duration: number;
  onRestart: (config: BreathingConfig) => void;
  onBackToHome: () => void;
  onShowHistory: () => void;
};

export default function CompletionScreen({
  config,
  completedSets,
  duration,
  onRestart,
  onBackToHome,
  onShowHistory,
}: CompletionScreenProps) {
  const formatDuration = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}분 ${secs}초`;
  };

  const getTodaySessionCount = () => {
    const history = JSON.parse(localStorage.getItem('breathingHistory') || '[]');
    const today = new Date().toDateString();
    return history.filter((record: any) => {
      const recordDate = new Date(record.date).toDateString();
      return recordDate === today;
    }).length;
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-5">
      {/* Title */}
      <h1 className="mb-10 text-center">수고하셨습니다! 🌟</h1>

      {/* Result Card */}
      <div className="w-full max-w-[320px] bg-white/15 rounded-[20px] p-8 shadow-[0_8px_32px_rgba(0,0,0,0.1)] mb-10">
        <p className="text-[20px] text-[#4ade80] text-center mb-5">
          오늘 {getTodaySessionCount()}번째 호흡 완료!
        </p>
        <div className="space-y-2 text-white/90">
          <p>이번 세션: {completedSets}세트</p>
          <p>소요 시간: {formatDuration(duration)}</p>
          <p>패턴: {config.inhale}-{config.hold}-{config.exhale}</p>
        </div>
      </div>

      {/* Buttons */}
      <div className="w-full max-w-[280px] space-y-3">
        <button
          onClick={() => onRestart(config)}
          className="w-full h-[52px] bg-gradient-to-r from-[#4ade80] to-[#22d3ee] rounded-[26px] shadow-[0_4px_15px_rgba(0,0,0,0.2)] hover:-translate-y-0.5 transition-transform"
        >
          🔄 다시 하기
        </button>
        <button
          onClick={onBackToHome}
          className="w-full h-12 bg-white/15 border border-white/30 rounded-[24px] hover:bg-white/25 transition-colors"
        >
          🏠 홈으로
        </button>
        <button
          onClick={onShowHistory}
          className="w-full h-12 bg-transparent border border-white/20 rounded-[24px] hover:bg-white/10 transition-colors text-white/80"
        >
          📊 전체 기록 보기
        </button>
      </div>
    </div>
  );
}
