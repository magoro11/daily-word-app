import { useState } from 'react'
import { Pressable, SafeAreaView, StatusBar, StyleSheet, Text, View } from 'react-native'
import { BookOpen, Clock3, Heart, Home, Settings } from 'lucide-react-native'
import { useAppStore } from '@/store/useAppStore'
import { Display } from './Display'
import { SettingsPanel } from './SettingsPanel'

const tabs = [
  { key: 'home', label: 'Today', icon: Home },
  { key: 'favorites', label: 'Favorites', icon: Heart },
  { key: 'history', label: 'History', icon: Clock3 },
  { key: 'settings', label: 'Settings', icon: Settings },
] as const

export function DailyWordApp() {
  const [setupStep, setSetupStep] = useState(0)
  const hydrated = useAppStore(s => s.isHydrated)
  const onboarding = useAppStore(s => s.showOnboarding)
  const activeTab = useAppStore(s => s.activeTab)
  const setActiveTab = useAppStore(s => s.setActiveTab)
  const isDark = useAppStore(s => s.isDark)
  const complete = useAppStore(s => s.completeOnboarding)
  if (!hydrated) return <View style={styles.loading}><BookOpen color="#C49B45" size={30} /><Text style={styles.loadingText}>Daily Word</Text></View>
  if (onboarding) return <Setup step={setupStep} next={() => setupStep < 2 ? setSetupStep(setupStep + 1) : complete()} />
  const colors = isDark ? dark : light
  return <SafeAreaView style={[styles.safe, { backgroundColor: colors.background }]}><StatusBar barStyle={isDark ? 'light-content' : 'dark-content'} />
    <View style={[styles.topbar, { borderColor: colors.border }]}><View><Text style={[styles.brand, { color: colors.text }]}>Daily Word</Text><Text style={[styles.date, { color: colors.muted }]}>A steady word for your day</Text></View><View style={styles.live}><View style={styles.liveDot} /><Text style={styles.liveText}>RUNNING</Text></View></View>
    <View style={styles.content}>{activeTab === 'settings' ? <SettingsPanel /> : <Display view={activeTab} />}</View>
    <View style={[styles.nav, { borderColor: colors.border, backgroundColor: colors.surface }]}>{tabs.map(({ key, label, icon: Icon }) => <Pressable key={key} accessibilityRole="tab" accessibilityState={{ selected: activeTab === key }} onPress={() => setActiveTab(key)} style={styles.navItem}><Icon size={21} color={activeTab === key ? '#C49B45' : colors.muted} fill={key === 'favorites' && activeTab === key ? '#C49B45' : 'transparent'} /><Text style={[styles.navLabel, { color: activeTab === key ? '#B7892F' : colors.muted }]}>{label}</Text></Pressable>)}</View>
  </SafeAreaView>
}

function Setup({ step, next }: { step: number; next: () => void }) { const headings = ['Welcome to Daily Word', 'Make it yours', 'Ready when you are']; const details = ["Let every hour remind you of God's Word, and every five minutes give you a reason to keep moving forward.", 'Your KJV Bible verses, motivational quotes, and a daily theme are ready. You can refine categories and timings at any time.', 'Daily Word runs entirely from local content. Enable notifications when you want a gentle prompt outside the app.']; return <SafeAreaView style={styles.setup}><BookOpen size={38} color="#C49B45" /><Text style={styles.setupKicker}>DAILY WORD</Text><Text style={styles.setupTitle}>{headings[step]}</Text><Text style={styles.setupCopy}>{details[step]}</Text><View style={styles.steps}>{[0,1,2].map(item => <View key={item} style={[styles.step, item === step && styles.stepActive]} />)}</View><Pressable accessibilityRole="button" onPress={next} style={styles.setupButton}><Text style={styles.setupButtonText}>{step === 2 ? 'Begin your day' : 'Continue'}</Text></Pressable></SafeAreaView> }
const light = { background: '#F8F7F2', surface: '#FFFFFF', text: '#18231F', muted: '#67756D', border: '#E5E3DB' }; const dark = { background: '#151C19', surface: '#202925', text: '#F3F5EF', muted: '#A5B0A9', border: '#344039' }
const styles = StyleSheet.create({ safe: { flex: 1 }, loading: { flex: 1, alignItems: 'center', justifyContent: 'center', backgroundColor: '#18231F', gap: 12 }, loadingText: { color: '#F5F1E8', fontSize: 20, fontWeight: '700' }, topbar: { height: 68, paddingHorizontal: 20, flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', borderBottomWidth: 1 }, brand: { fontSize: 22, fontWeight: '700' }, date: { fontSize: 12, marginTop: 3 }, live: { flexDirection: 'row', alignItems: 'center', gap: 6 }, liveDot: { width: 7, height: 7, borderRadius: 4, backgroundColor: '#5AA176' }, liveText: { color: '#5A8B70', fontSize: 10, fontWeight: '800', letterSpacing: 1 }, content: { flex: 1 }, nav: { height: 70, flexDirection: 'row', borderTopWidth: 1, justifyContent: 'space-around', paddingHorizontal: 6 }, navItem: { flex: 1, alignItems: 'center', justifyContent: 'center', gap: 4 }, navLabel: { fontSize: 11, fontWeight: '600' }, setup: { flex: 1, backgroundColor: '#18231F', alignItems: 'center', justifyContent: 'center', paddingHorizontal: 34 }, setupKicker: { color: '#C49B45', fontSize: 11, fontWeight: '800', letterSpacing: 2, marginTop: 26 }, setupTitle: { color: '#F8F6EF', fontSize: 30, fontWeight: '700', textAlign: 'center', marginTop: 12 }, setupCopy: { color: '#B8C3BB', textAlign: 'center', fontSize: 16, lineHeight: 24, marginTop: 16 }, steps: { flexDirection: 'row', gap: 7, marginTop: 40 }, step: { width: 22, height: 4, backgroundColor: '#415049', borderRadius: 3 }, stepActive: { backgroundColor: '#C49B45' }, setupButton: { backgroundColor: '#D3AC56', paddingHorizontal: 26, paddingVertical: 14, borderRadius: 8, marginTop: 30, minWidth: 150, alignItems: 'center' }, setupButtonText: { color: '#18231F', fontSize: 15, fontWeight: '800' } })
