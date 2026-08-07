import nextVitals from "eslint-config-next/core-web-vitals";
import nextTs from "eslint-config-next/typescript";
import { defineConfig, globalIgnores } from "eslint/config";

const eslintConfig = defineConfig([
  ...nextVitals,
  ...nextTs,
  // Override default ignores of eslint-config-next.
  globalIgnores([
    // Default ignores of eslint-config-next:
    ".next/**",
    "out/**",
    "build/**",
    "next-env.d.ts",
    "src/components/**", // 추후 샤드CN에 의해서 자동으로 만들어지는 파일들이 저장될 경로
    "src/hooks/**", // 추후 샤드CN에 의해서 자동으로 만들어지는 파일들이 저장될 경로
    "src/lib/backend/*/schema.d.ts", // openapi-typescript에 의해서 자동으로 만들어지는 파일들이 저장되는 경로
  ]),
  {
    rules: {
      "@next/next/no-img-element": "off",
    },
  },
]);

export default eslintConfig;
