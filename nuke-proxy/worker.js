export default {
  async fetch(request, env) {
    // only allow POST requests
    if (request.method !== "POST") {
      return new Response("Method not allowed", { status: 405 });
    }

    // simple secret token to prevent random people using your proxy
    const authHeader = request.headers.get("X-Nuke-Token");
    if (authHeader !== env.NUKE_TOKEN) {
      return new Response("Unauthorized", { status: 401 });
    }

    // rate limit: 50 requests per IP per day
    const ip = request.headers.get("CF-Connecting-IP") || "unknown";
    const rateLimitKey = `rate:${ip}`;
    const count = parseInt((await env.RATE_LIMITER.get(rateLimitKey)) || "0");
    if (count >= 50) {
      return new Response("Rate limited — calm down.", { status: 429 });
    }
    await env.RATE_LIMITER.put(rateLimitKey, String(count + 1), {
      expirationTtl: 86400, // reset after 24 hours
    });

    try {
      const body = await request.json();

      const response = await fetch("https://api.anthropic.com/v1/messages", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "x-api-key": env.ANTHROPIC_API_KEY,
          "anthropic-version": "2023-06-01",
        },
        body: JSON.stringify(body),
      });

      const data = await response.json();

      return new Response(JSON.stringify(data), {
        status: response.status,
        headers: { "Content-Type": "application/json" },
      });
    } catch (err) {
      return new Response(JSON.stringify({ error: err.message }), {
        status: 500,
        headers: { "Content-Type": "application/json" },
      });
    }
  },
};
