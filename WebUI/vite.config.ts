import { fileURLToPath } from "node:url";
import tailwindcss from "@tailwindcss/vite";
import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";

// The built app is published inside the Jekyll site, under <site baseurl>/ide/
// (see .github/workflows/docs.yml). Set WEBUI_BASE to publish it elsewhere.
const buildBase = process.env.WEBUI_BASE ?? "/PLP/ide/";

// https://vite.dev/config/
export default defineConfig(({ command }) => ({
  base: command === "build" ? buildBase : "/",
  plugins: [react(), tailwindcss()],
  build: {
    // Duas paginas: a IDE (index.html) e a BNF de OO2 (bnf.html)
    rolldownOptions: {
      input: {
        main: fileURLToPath(new URL("./index.html", import.meta.url)),
        bnf: fileURLToPath(new URL("./bnf.html", import.meta.url)),
      },
    },
  },
  server: {
    host: true,
    port: 4004,
    strictPort: true,
    hmr: {
      clientPort: 4004,
    },
  },
}));
