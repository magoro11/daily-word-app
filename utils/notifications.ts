/**
 * Notification helpers — schedules an immediate local notification.
 * Called from the engine event listeners in root layout.
 */
import * as Notifications from 'expo-notifications'
import { Platform } from 'react-native'

export async function requestNotificationPermission(): Promise<boolean> {
  if (Platform.OS === 'android') {
    await Notifications.setNotificationChannelAsync('daily-word-updates', {
      name:       'Content updates',
      importance: Notifications.AndroidImportance.DEFAULT,
      vibrationPattern: [0, 200],
      lightColor: '#6366f1',
    })
    await Notifications.setNotificationChannelAsync('daily-word-status', {
      name: 'Daily Word status',
      importance: Notifications.AndroidImportance.LOW,
      sound: undefined,
    })
  }
  const { status: existing } = await Notifications.getPermissionsAsync()
  if (existing === 'granted') return true
  const { status } = await Notifications.requestPermissionsAsync()
  return status === 'granted'
}

export async function sendLocalNotification(
  title: string,
  body: string,
): Promise<void> {
  try {
    await Notifications.scheduleNotificationAsync({
      content: { title, body, sound: false, ...(Platform.OS === 'android' ? { channelId: 'daily-word-updates' } : {}) },
      trigger: null,  // fire immediately
    })
  } catch { /* ignore — permissions not granted */ }
}
