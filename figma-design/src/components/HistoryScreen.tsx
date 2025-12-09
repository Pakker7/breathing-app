import { ArrowLeft } from 'lucide-react';
import { SessionRecord } from '../App';

type HistoryScreenProps = {
  onBack: () => void;
};

export default function HistoryScreen({ onBack }: HistoryScreenProps) {
  const history: SessionRecord[] = JSON.parse(localStorage.getItem('breathingHistory') || '[]');

  const getWeeklyStats = () => {
    const weekAgo = new Date();
    weekAgo.setDate(weekAgo.getDate() - 7);

    const weeklyRecords = history.filter(record => {
      return new Date(record.date) >= weekAgo;
    });

    const totalSessions = weeklyRecords.length;
    const totalDuration = weeklyRecords.reduce((sum, record) => sum + record.duration, 0);
    
    // Find most used pattern
    const patternCounts: { [key: string]: number } = {};
    weeklyRecords.forEach(record => {
      patternCounts[record.pattern] = (patternCounts[record.pattern] || 0) + 1;
    });
    
    const mostUsedPattern = Object.entries(patternCounts).sort((a, b) => b[1] - a[1])[0]?.[0] || 'N/A';

    return {
      totalSessions,
      totalDuration,
      mostUsedPattern,
    };
  };

  const formatDuration = (seconds: number) => {
    const hours = Math.floor(seconds / 3600);
    const mins = Math.floor((seconds % 3600) / 60);
    if (hours > 0) {
      return `${hours}시간 ${mins}분`;
    }
    return `${mins}분`;
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    if (date.toDateString() === today.toDateString()) {
      return '오늘';
    } else if (date.toDateString() === yesterday.toDateString()) {
      return '어제';
    } else {
      return `${date.getMonth() + 1}월 ${date.getDate()}일`;
    }
  };

  const formatTime = (dateString: string) => {
    const date = new Date(dateString);
    const hours = date.getHours();
    const mins = date.getMinutes().toString().padStart(2, '0');
    const period = hours < 12 ? '오전' : '오후';
    const displayHours = hours % 12 || 12;
    return `${period} ${displayHours}:${mins}`;
  };

  const groupByDate = () => {
    const grouped: { [key: string]: SessionRecord[] } = {};
    
    history.forEach(record => {
      const dateKey = new Date(record.date).toDateString();
      if (!grouped[dateKey]) {
        grouped[dateKey] = [];
      }
      grouped[dateKey].push(record);
    });

    return Object.entries(grouped).map(([dateKey, records]) => ({
      date: records[0].date,
      records,
    }));
  };

  const stats = getWeeklyStats();
  const groupedHistory = groupByDate();

  return (
    <div className="min-h-screen">
      {/* Header */}
      <div className="h-16 flex items-center justify-center relative px-5">
        <button
          onClick={onBack}
          className="absolute left-4 w-12 h-12 flex items-center justify-center rounded-full hover:bg-white/10 transition-colors"
          aria-label="뒤로 가기"
        >
          <ArrowLeft className="w-6 h-6" />
        </button>
        <h2 className="text-center">기록</h2>
      </div>

      <div className="px-5 pb-8">
        {/* Weekly Stats */}
        <div className="mb-6">
          <h3 className="mb-4">📊 이번 주 통계</h3>
          <div className="bg-white/15 rounded-2xl p-6 space-y-2">
            <p>총 호흡 세션: {stats.totalSessions}회</p>
            <p>총 소요 시간: {formatDuration(stats.totalDuration)}</p>
            <p>가장 많이 사용: {stats.mostUsedPattern} 호흡법</p>
          </div>
        </div>

        {/* Recent History */}
        <div>
          <h3 className="mb-4">📅 최근 기록</h3>
          
          {groupedHistory.length === 0 ? (
            <div className="bg-white/10 rounded-xl p-8 text-center text-white/60">
              아직 기록이 없습니다
            </div>
          ) : (
            <div className="space-y-2">
              {groupedHistory.map((group, index) => (
                <div key={index} className="bg-white/10 rounded-xl p-4">
                  <p className="mb-3">
                    {formatDate(group.date)}
                  </p>
                  <div className="space-y-2 text-sm text-white/90">
                    {group.records.map((record, recordIndex) => (
                      <div key={recordIndex} className="flex items-center gap-2">
                        <span>•</span>
                        <span>{record.pattern}, {record.sets}세트</span>
                        <span className="ml-auto text-xs text-white/70">
                          {formatTime(record.date)}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
