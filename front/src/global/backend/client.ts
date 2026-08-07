import createClient from "openapi-fetch";

import type { paths } from "@/global/backend/apiV1/schema";

const NEXT_PUBLIC_API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

const client = createClient<paths>({
  baseUrl: NEXT_PUBLIC_API_BASE_URL,
  credentials: "include",
});

export default client;
