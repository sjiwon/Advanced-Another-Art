const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false,
  publicPath: '/',
  indexPath: '../../src/main/resources/templates/index.html',
  outputDir: '../../src/main/resources/static',
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:7777',
        changeOrigin: true
      }
    }
  }
})
