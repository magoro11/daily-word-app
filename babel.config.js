module.exports = function (api) {
  api.cache(true)
  return {
    presets: ['babel-preset-expo'],
    plugins: [
      // Path alias resolution  (@/* and @core/*)
      [
        'module-resolver',
        {
          root: ['.'],
          alias: {
            '@': '.',
            '@core': './core',
          },
        },
      ],
      // react-native-reanimated must be listed last
      'react-native-reanimated/plugin',
    ],
  }
}
