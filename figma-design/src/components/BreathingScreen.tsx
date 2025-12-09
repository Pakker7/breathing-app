import { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Volume2, Settings, Pause, Square } from 'lucide-react';
import { BreathingConfig } from '../App';
import PauseModal from './PauseModal';
import VolumeControl from './VolumeControl';

type BreathState = 'idle' | 'inhale' | 'hold' | 'exhale';

type BreathingScreenProps = {
  config: BreathingConfig;
  onComplete: (completedSets: number, duration: number) => void;
  onBack: () => void;
};

export default function BreathingScreen({ config, onComplete, onBack }: BreathingScreenProps) {
  const [state, setState] = useState<BreathState>('idle');
  const [currentSet, setCurrentSet] = useState(1);
  const [counter, setCounter] = useState(0);
  const [isPaused, setIsPaused] = useState(false);
  const [showPauseModal, setShowPauseModal] = useState(false);
  const [showVolumeControl, setShowVolumeControl] = useState(false);
  const [startTime] = useState(Date.now());
  
  const audioContextRef = useRef<AudioContext | null>(null);
  const timerRef = useRef<NodeJS.Timeout | null>(null);

  // Audio settings from localStorage
  const [audioSettings, setAudioSettings] = useState(() => {
    const saved = localStorage.getItem('audioSettings');
    return saved ? JSON.parse(saved) : {
      volume: 60,
      voiceGuide: true,
      soundEffects: true,
    };
  });

  useEffect(() => {
    // Initialize Web Audio API
    audioContextRef.current = new (window.AudioContext || (window as any).webkitAudioContext)();
    
    // Start breathing automatically
    startBreathing();

    return () => {
      if (timerRef.current) {
        clearTimeout(timerRef.current);
      }
      if (audioContextRef.current) {
        audioContextRef.current.close();
      }
    };
  }, []);

  const playSound = (frequency: number, duration: number, type: 'tick' | 'transition' = 'tick') => {
    if (!audioSettings.soundEffects || !audioContextRef.current) return;

    const ctx = audioContextRef.current;
    const oscillator = ctx.createOscillator();
    const gainNode = ctx.createGain();

    oscillator.connect(gainNode);
    gainNode.connect(ctx.destination);

    oscillator.frequency.value = frequency;
    oscillator.type = 'sine';

    const volume = (audioSettings.volume / 100) * (type === 'transition' ? 0.3 : 0.15);
    gainNode.gain.setValueAtTime(volume, ctx.currentTime);
    gainNode.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + duration / 1000);

    oscillator.start(ctx.currentTime);
    oscillator.stop(ctx.currentTime + duration / 1000);
  };

  const playTransitionSound = (newState: BreathState) => {
    if (newState === 'inhale') {
      // Ascending tone
      playSound(600, 200, 'transition');
      setTimeout(() => playSound(800, 200, 'transition'), 100);
    } else if (newState === 'hold') {
      // Mid tone
      playSound(700, 150, 'transition');
    } else if (newState === 'exhale') {
      // Descending tone
      playSound(800, 250, 'transition');
      setTimeout(() => playSound(600, 250, 'transition'), 100);
    }
  };

  const speak = (text: string) => {
    if (!audioSettings.voiceGuide) return;
    
    const utterance = new SpeechSynthesisUtterance(text);
    utterance.lang = 'ko-KR';
    utterance.rate = 0.9;
    utterance.pitch = 1.0;
    utterance.volume = audioSettings.volume / 100;
    
    window.speechSynthesis.cancel();
    window.speechSynthesis.speak(utterance);
  };

  const startBreathing = () => {
    setState('inhale');
    setCounter(config.inhale);
    playTransitionSound('inhale');
    speak('들이쉬세요');
  };

  const runBreathingCycle = (currentState: BreathState, currentCounter: number) => {
    if (isPaused) return;

    if (currentCounter > 0) {
      playSound(1000, 50, 'tick');
      setCounter(currentCounter - 1);
      timerRef.current = setTimeout(() => {
        runBreathingCycle(currentState, currentCounter - 1);
      }, 1000);
    } else {
      // Transition to next state
      if (currentState === 'inhale') {
        setState('hold');
        setCounter(config.hold);
        playTransitionSound('hold');
        speak('멈추세요');
        timerRef.current = setTimeout(() => {
          runBreathingCycle('hold', config.hold);
        }, 1000);
      } else if (currentState === 'hold') {
        setState('exhale');
        setCounter(config.exhale);
        playTransitionSound('exhale');
        speak('내쉬세요');
        timerRef.current = setTimeout(() => {
          runBreathingCycle('exhale', config.exhale);
        }, 1000);
      } else if (currentState === 'exhale') {
        // Set complete
        if (currentSet < config.sets) {
          // Play set complete sound
          playSound(800, 150, 'transition');
          setTimeout(() => playSound(1000, 200, 'transition'), 150);
          
          setCurrentSet(currentSet + 1);
          setState('inhale');
          setCounter(config.inhale);
          playTransitionSound('inhale');
          speak('들이쉬세요');
          timerRef.current = setTimeout(() => {
            runBreathingCycle('inhale', config.inhale);
          }, 1000);
        } else {
          // All sets complete
          const duration = Math.floor((Date.now() - startTime) / 1000);
          speak('수고하셨습니다');
          onComplete(config.sets, duration);
        }
      }
    }
  };

  useEffect(() => {
    if (state !== 'idle' && !isPaused) {
      if (timerRef.current) {
        clearTimeout(timerRef.current);
      }
      timerRef.current = setTimeout(() => {
        runBreathingCycle(state, counter);
      }, 1000);
    }

    return () => {
      if (timerRef.current) {
        clearTimeout(timerRef.current);
      }
    };
  }, [state, counter, isPaused]);

  const handlePause = () => {
    setIsPaused(true);
    setShowPauseModal(true);
    window.speechSynthesis.cancel();
  };

  const handleResume = () => {
    setIsPaused(false);
    setShowPauseModal(false);
  };

  const handleRestart = () => {
    setIsPaused(false);
    setShowPauseModal(false);
    setCurrentSet(1);
    startBreathing();
  };

  const handleStop = () => {
    const duration = Math.floor((Date.now() - startTime) / 1000);
    onComplete(currentSet - 1, duration);
  };

  const getStateText = () => {
    if (isPaused) return '정지되었습니다';
    if (state === 'idle') return '시작 버튼을 눌러주세요';
    if (state === 'inhale') return '들이쉬세요';
    if (state === 'hold') return '멈추세요';
    if (state === 'exhale') return '내쉬세요';
    return '';
  };

  const getCircleScale = () => {
    if (state === 'inhale' || state === 'hold') return 1.5;
    return 1;
  };

  const getCircleColor = () => {
    if (state === 'inhale') return '#4ade80';
    if (state === 'hold') return '#fbbf24';
    if (state === 'exhale') return '#f87171';
    return 'rgba(255, 255, 255, 0.3)';
  };

  return (
    <div className="min-h-screen flex flex-col">
      {/* Status Bar */}
      <div className="h-16 flex items-center justify-center px-6 relative">
        <span className="text-xl">{currentSet}/{config.sets} 세트</span>
        <div className="absolute right-6 flex gap-4">
          <button
            onClick={() => setShowVolumeControl(!showVolumeControl)}
            className="w-12 h-12 flex items-center justify-center rounded-full hover:bg-white/10 transition-colors relative"
            aria-label="음량 조절"
          >
            <Volume2 className="w-6 h-6" />
            {showVolumeControl && (
              <VolumeControl
                settings={audioSettings}
                onSettingsChange={(newSettings) => {
                  setAudioSettings(newSettings);
                  localStorage.setItem('audioSettings', JSON.stringify(newSettings));
                }}
                onClose={() => setShowVolumeControl(false)}
              />
            )}
          </button>
        </div>
      </div>

      {/* Breathing Circle */}
      <div className="flex-1 flex flex-col items-center justify-center">
        <motion.div
          className="rounded-full border-4"
          style={{
            width: '200px',
            height: '200px',
            borderColor: getCircleColor(),
            boxShadow: `0 0 30px ${getCircleColor()}80`,
          }}
          animate={{
            scale: getCircleScale(),
          }}
          transition={{
            duration: 1,
            ease: 'easeInOut',
          }}
        />

        {/* Status Text */}
        <div className="mt-10 min-h-[60px] flex items-center justify-center">
          <AnimatePresence mode="wait">
            <motion.p
              key={getStateText()}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
              transition={{ duration: 0.2 }}
              className="text-center"
            >
              {getStateText()}
            </motion.p>
          </AnimatePresence>
        </div>

        {/* Counter */}
        <div className="min-h-[70px] flex items-center justify-center">
          <AnimatePresence mode="wait">
            <motion.span
              key={counter}
              initial={{ opacity: 0, scale: 0.8 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 1.2 }}
              transition={{ duration: 0.15 }}
              className="text-[44px]"
            >
              {counter}
            </motion.span>
          </AnimatePresence>
        </div>
      </div>

      {/* Control Buttons */}
      <div className="pb-8 px-5 flex flex-col items-center gap-3">
        <button
          onClick={handlePause}
          className="w-full max-w-[280px] h-12 bg-white/15 border border-white/30 rounded-[24px] hover:bg-white/25 transition-colors flex items-center justify-center gap-2"
        >
          <Pause className="w-5 h-5" />
          일시정지
        </button>
        <button
          onClick={handleStop}
          className="w-full max-w-[280px] h-12 bg-[#ff6b6b]/20 border border-[#ff6b6b]/50 rounded-[24px] hover:bg-[#ff6b6b]/30 transition-colors flex items-center justify-center gap-2 text-[#ff6b6b]"
        >
          <Square className="w-5 h-5" />
          종료하기
        </button>
      </div>

      {/* Pause Modal */}
      {showPauseModal && (
        <PauseModal
          onResume={handleResume}
          onRestart={handleRestart}
          onStop={handleStop}
        />
      )}
    </div>
  );
}