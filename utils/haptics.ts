/**
 * Thin wrapper around expo-haptics so components stay clean.
 * All calls are fire-and-forget (swallow errors on unsupported devices).
 */
import * as Haptics from 'expo-haptics'

export function lightTap() {
  Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light).catch(() => {})
}

export function mediumTap() {
  Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Medium).catch(() => {})
}

export function success() {
  Haptics.notificationAsync(Haptics.NotificationFeedbackType.Success).catch(() => {})
}
