import type { MotivationalQuote } from '../types'

const raw: Omit<MotivationalQuote, 'favorite' | 'lastDisplayedAt'>[] = [
  // ── SUCCESS ───────────────────────────────────────────────────────────────
  { id: 'q-suc-001', text: 'Success is not final, failure is not fatal: it is the courage to continue that counts.', author: 'Winston Churchill', category: 'success' },
  { id: 'q-suc-002', text: 'The secret of success is to do the common thing uncommonly well.', author: 'John D. Rockefeller Jr.', category: 'success' },
  { id: 'q-suc-003', text: 'I find that the harder I work, the more luck I seem to have.', author: 'Thomas Jefferson', category: 'success' },
  { id: 'q-suc-004', text: 'Success usually comes to those who are too busy to be looking for it.', author: 'Henry David Thoreau', category: 'success' },
  { id: 'q-suc-005', text: "Don't aim for success if you want it; just do what you love and believe in, and it will come naturally.", author: 'David Frost', category: 'success' },
  { id: 'q-suc-006', text: 'There are no secrets to success. It is the result of preparation, hard work, and learning from failure.', author: 'Colin Powell', category: 'success' },
  { id: 'q-suc-007', text: 'Success is walking from failure to failure with no loss of enthusiasm.', author: 'Winston Churchill', category: 'success' },
  { id: 'q-suc-008', text: "Opportunities don't happen. You create them.", author: 'Chris Grosser', category: 'success' },
  { id: 'q-suc-009', text: 'Try not to become a man of success. Rather become a man of value.', author: 'Albert Einstein', category: 'success' },
  { id: 'q-suc-010', text: 'The road to success and the road to failure are almost exactly the same.', author: 'Colin R. Davis', category: 'success' },
  // ── DISCIPLINE ────────────────────────────────────────────────────────────
  { id: 'q-dis-001', text: 'We are what we repeatedly do. Excellence, then, is not an act but a habit.', author: 'Aristotle', category: 'discipline' },
  { id: 'q-dis-002', text: 'Discipline is the bridge between goals and accomplishment.', author: 'Jim Rohn', category: 'discipline' },
  { id: 'q-dis-003', text: 'With self-discipline most anything is possible.', author: 'Theodore Roosevelt', category: 'discipline' },
  { id: 'q-dis-004', text: 'Discipline is choosing between what you want now and what you want most.', author: 'Abraham Lincoln', category: 'discipline' },
  { id: 'q-dis-005', text: 'A man who conquers himself is greater than one who conquers a thousand men in battle.', author: 'Buddha', category: 'discipline' },
  { id: 'q-dis-006', text: 'The pain of discipline is nothing like the pain of disappointment.', author: 'Justin Langer', category: 'discipline' },
  { id: 'q-dis-007', text: 'Do the hard jobs first. The easy jobs will take care of themselves.', author: 'Dale Carnegie', category: 'discipline' },
  { id: 'q-dis-008', text: 'Motivation gets you started. Habit keeps you going.', author: 'Jim Ryun', category: 'discipline' },
  // ── HARD WORK ─────────────────────────────────────────────────────────────
  { id: 'q-hw-001', text: 'The only place success comes before work is in the dictionary.', author: 'Vince Lombardi', category: 'hard-work' },
  { id: 'q-hw-002', text: 'Nothing worth having comes easy.', author: 'Theodore Roosevelt', category: 'hard-work' },
  { id: 'q-hw-003', text: 'Genius is one percent inspiration and ninety-nine percent perspiration.', author: 'Thomas Edison', category: 'hard-work' },
  { id: 'q-hw-004', text: "Hard work beats talent when talent doesn't work hard.", author: 'Tim Notke', category: 'hard-work' },
  { id: 'q-hw-005', text: 'Without hard work, nothing grows but weeds.', author: 'Gordon B. Hinckley', category: 'hard-work' },
  { id: 'q-hw-006', text: 'Things may come to those who wait, but only the things left by those who hustle.', author: 'Abraham Lincoln', category: 'hard-work' },
  { id: 'q-hw-007', text: 'There is no substitute for hard work.', author: 'Thomas Edison', category: 'hard-work' },
  { id: 'q-hw-008', text: "A dream doesn't become reality through magic; it takes sweat, determination and hard work.", author: 'Colin Powell', category: 'hard-work' },
  { id: 'q-hw-009', text: "If people knew how hard I worked to get my mastery, it wouldn't seem so wonderful at all.", author: 'Michelangelo', category: 'hard-work' },
  // ── CAREER ────────────────────────────────────────────────────────────────
  { id: 'q-car-001', text: 'Choose a job you love, and you will never have to work a day in your life.', author: 'Confucius', category: 'career' },
  { id: 'q-car-002', text: 'The only way to do great work is to love what you do.', author: 'Steve Jobs', category: 'career' },
  { id: 'q-car-003', text: 'Quality is not an act, it is a habit.', author: 'Aristotle', category: 'career' },
  { id: 'q-car-004', text: 'Whatever you are, be a good one.', author: 'Abraham Lincoln', category: 'career' },
  { id: 'q-car-005', text: "Be so good they can't ignore you.", author: 'Steve Martin', category: 'career' },
  { id: 'q-car-006', text: 'Your attitude, not your aptitude, will determine your altitude.', author: 'Zig Ziglar', category: 'career' },
  { id: 'q-car-007', text: 'The secret of getting ahead is getting started.', author: 'Mark Twain', category: 'career' },
  { id: 'q-car-008', text: 'Start where you are. Use what you have. Do what you can.', author: 'Arthur Ashe', category: 'career' },
  // ── EDUCATION ─────────────────────────────────────────────────────────────
  { id: 'q-edu-001', text: 'Education is the most powerful weapon which you can use to change the world.', author: 'Nelson Mandela', category: 'education' },
  { id: 'q-edu-002', text: 'Live as if you were to die tomorrow. Learn as if you were to live forever.', author: 'Mahatma Gandhi', category: 'education' },
  { id: 'q-edu-003', text: 'An investment in knowledge pays the best interest.', author: 'Benjamin Franklin', category: 'education' },
  { id: 'q-edu-004', text: 'The beautiful thing about learning is that no one can take it away from you.', author: 'B.B. King', category: 'education' },
  { id: 'q-edu-005', text: 'Education is not the filling of a pail, but the lighting of a fire.', author: 'W.B. Yeats', category: 'education' },
  { id: 'q-edu-006', text: 'Knowledge is power.', author: 'Francis Bacon', category: 'education' },
  { id: 'q-edu-007', text: 'Develop a passion for learning. If you do, you will never cease to grow.', author: "Anthony J. D'Angelo", category: 'education' },
  { id: 'q-edu-008', text: 'Anyone who stops learning is old, whether at twenty or eighty.', author: 'Henry Ford', category: 'education' },
  // ── CONFIDENCE ────────────────────────────────────────────────────────────
  { id: 'q-con-001', text: "Believe you can and you're halfway there.", author: 'Theodore Roosevelt', category: 'confidence' },
  { id: 'q-con-002', text: 'It is not the mountain we conquer but ourselves.', author: 'Edmund Hillary', category: 'confidence' },
  { id: 'q-con-003', text: 'No one can make you feel inferior without your consent.', author: 'Eleanor Roosevelt', category: 'confidence' },
  { id: 'q-con-004', text: 'You have power over your mind – not outside events. Realize this, and you will find strength.', author: 'Marcus Aurelius', category: 'confidence' },
  { id: 'q-con-005', text: 'Whether you think you can or you think you cannot, you are right.', author: 'Henry Ford', category: 'confidence' },
  { id: 'q-con-006', text: 'You are braver than you believe, stronger than you seem, and smarter than you think.', author: 'A.A. Milne', category: 'confidence' },
  { id: 'q-con-007', text: 'To be yourself in a world that is constantly trying to make you something else is the greatest accomplishment.', author: 'Ralph Waldo Emerson', category: 'confidence' },
  { id: 'q-con-008', text: 'Act as if what you do makes a difference. It does.', author: 'William James', category: 'confidence' },
  // ── LEADERSHIP ────────────────────────────────────────────────────────────
  { id: 'q-lea-001', text: 'A leader is one who knows the way, goes the way, and shows the way.', author: 'John C. Maxwell', category: 'leadership' },
  { id: 'q-lea-002', text: 'Leadership is the capacity to translate vision into reality.', author: 'Warren Bennis', category: 'leadership' },
  { id: 'q-lea-003', text: 'Management is doing things right; leadership is doing the right things.', author: 'Peter Drucker', category: 'leadership' },
  { id: 'q-lea-004', text: 'Before you are a leader, success is all about growing yourself. When you become a leader, success is all about growing others.', author: 'Jack Welch', category: 'leadership' },
  { id: 'q-lea-005', text: 'The key to successful leadership today is influence, not authority.', author: 'Ken Blanchard', category: 'leadership' },
  { id: 'q-lea-006', text: 'Leaders are made, they are not born. They are made by hard effort.', author: 'Vince Lombardi', category: 'leadership' },
  // ── RESILIENCE ────────────────────────────────────────────────────────────
  { id: 'q-res-001', text: "It's not whether you get knocked down, it's whether you get up.", author: 'Vince Lombardi', category: 'resilience' },
  { id: 'q-res-002', text: 'You may encounter many defeats, but you must not be defeated.', author: 'Maya Angelou', category: 'resilience' },
  { id: 'q-res-003', text: 'Rock bottom became the solid foundation on which I rebuilt my life.', author: 'J.K. Rowling', category: 'resilience' },
  { id: 'q-res-004', text: 'The world breaks everyone, and afterward, some are strong at the broken places.', author: 'Ernest Hemingway', category: 'resilience' },
  { id: 'q-res-005', text: 'Fall seven times, stand up eight.', author: 'Japanese Proverb', category: 'resilience' },
  { id: 'q-res-006', text: 'Our greatest glory is not in never falling, but in rising every time we fall.', author: 'Confucius', category: 'resilience' },
  { id: 'q-res-007', text: 'Tough times never last, but tough people do.', author: 'Robert H. Schuller', category: 'resilience' },
  { id: 'q-res-008', text: 'When everything seems to be going against you, remember that the airplane takes off against the wind, not with it.', author: 'Henry Ford', category: 'resilience' },
  // ── PERSONAL GROWTH ───────────────────────────────────────────────────────
  { id: 'q-pg-001', text: 'Be the change you wish to see in the world.', author: 'Mahatma Gandhi', category: 'personal-growth' },
  { id: 'q-pg-002', text: 'In any given moment we have two options: to step forward into growth or to step back into safety.', author: 'Abraham Maslow', category: 'personal-growth' },
  { id: 'q-pg-003', text: 'What you get by achieving your goals is not as important as what you become by achieving your goals.', author: 'Henry David Thoreau', category: 'personal-growth' },
  { id: 'q-pg-004', text: 'The only person you are destined to become is the person you decide to be.', author: 'Ralph Waldo Emerson', category: 'personal-growth' },
  { id: 'q-pg-005', text: 'The measure of intelligence is the ability to change.', author: 'Albert Einstein', category: 'personal-growth' },
  { id: 'q-pg-006', text: 'Yesterday I was clever, so I wanted to change the world. Today I am wise, so I am changing myself.', author: 'Rumi', category: 'personal-growth' },
  { id: 'q-pg-007', text: 'It is not in the stars to hold our destiny but in ourselves.', author: 'William Shakespeare', category: 'personal-growth' },
  { id: 'q-pg-008', text: 'Every strike brings me closer to the next home run.', author: 'Babe Ruth', category: 'personal-growth' },
]

export const motivationalQuotes: MotivationalQuote[] = raw.map(q => ({
  ...q,
  favorite: false,
  lastDisplayedAt: null,
}))

export default motivationalQuotes
