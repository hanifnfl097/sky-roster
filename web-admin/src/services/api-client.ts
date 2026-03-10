/**
 * SkyRoster API client configuration and base fetch wrapper.
 * All API calls go through this service for consistent auth and error handling.
 */

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1";

/**
 * Custom error class for API responses with structured error data.
 */
export class ApiError extends Error {
  constructor(
    public readonly statusCode: number,
    public readonly errorCode: string,
    message: string
  ) {
    super(message);
    this.name = "ApiError";
  }
}

interface RequestOptions extends RequestInit {
  skipAuth?: boolean;
}

/**
 * Base fetch wrapper with JWT authentication and error handling.
 * Automatically attaches Authorization header from localStorage.
 *
 * @param endpoint - API endpoint path (e.g., "/crew")
 * @param options - Fetch options with optional skipAuth flag
 * @returns Parsed JSON response
 * @throws ApiError on non-2xx responses
 */
export async function apiClient<T>(
  endpoint: string,
  options: RequestOptions = {}
): Promise<T> {
  const { skipAuth = false, headers: customHeaders, ...fetchOptions } = options;

  const headers: HeadersInit = {
    "Content-Type": "application/json",
    ...customHeaders,
  };

  if (!skipAuth) {
    const token = typeof window !== "undefined"
      ? localStorage.getItem("access_token")
      : null;
    if (token) {
      (headers as Record<string, string>)["Authorization"] = `Bearer ${token}`;
    }
  }

  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...fetchOptions,
    headers,
  });

  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({
      message: response.statusText,
      errorCode: "UNKNOWN_ERROR",
    }));

    throw new ApiError(
      response.status,
      errorBody.errorCode || "UNKNOWN_ERROR",
      errorBody.message || "An unexpected error occurred"
    );
  }

  // Handle 204 No Content
  if (response.status === 204) {
    return undefined as T;
  }

  return response.json();
}
