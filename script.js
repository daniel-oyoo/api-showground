import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
  vus: 5,          // 5 Virtual Users
  duration: '10s', 
};

export default function () {
  const url = 'http://host.docker.internal:8081/api/v1/chat';

  // 1. Generate a unique fake IP for each Virtual User (e.g., 192.168.1.1, 192.168.1.2, etc.)
  const simulatedIp = `192.168.1.${__VU}`;

  // 2. Generate unique payload content
  const randomString = Math.random().toString(36).substring(2, 8);
  const payload = JSON.stringify({
    message: `Test message from IP ${simulatedIp} [${randomString}]`,
    mode: "native",
    sessionId: "default"
  });

  // 3. Pass the IP via X-Forwarded-For header so your Spring filter picks it up
  const params = {
    headers: {
      'Content-Type': 'application/json',
      'X-Forwarded-For': simulatedIp, 
    },
  };

  const res = http.post(url, payload, params);

  check(res, {
    'is status 200': (r) => r.status === 200,
    'is rate limited (429)': (r) => r.status === 429,
  });

  sleep(1);
}