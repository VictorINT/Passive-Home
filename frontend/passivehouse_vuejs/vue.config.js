const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 3000, // Portul serverului Vue
    hot: true, // Activează Hot Module Replacement (HMR)
    watchFiles: ['src/**/*'], // Asigură-te că fișierele sunt urmărite pentru modificări
    client: {
      overlay: true, // Afișează erorile în browser
    },
  },
});
