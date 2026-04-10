import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  build: {
    sourcemap: true
  },
  server: {
    host: true,
    port: 3000,
    watch: { usePolling: true },
    proxy: {
      '/ow': {
        target: 'https://api.openweathermap.org/data/2.5',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/ow/, ''),
      },
      // Proxy backend API calls to avoid CORS during development
      '/users': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        // leave path as-is
        rewrite: (path) => path,
      },
      '/quadrants': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path,
      },
    }
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
});
