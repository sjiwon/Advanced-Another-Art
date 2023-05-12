const { defineConfig } = require('@vue/cli-service')
const path = require('path')

module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false,
  publicPath: '/',
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:7777',
        changeOrigin: true
      }
    }
  }
})
