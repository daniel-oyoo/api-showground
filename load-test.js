// 1. IMPORT k6 modules you need
import http from 'k6/http';
import { sleep, check } from 'k6';

// 2. CONFIGURE test behavior (Virtual Users, duration, thresholds)
export const options = {
  vus: 10,               // 10 Virtual Users running concurrently
  duration: '30s',       // Run the test for 30 seconds
  thresholds: {
    // Fail the test if 95% of requests take longer than 200ms
    'http_req_duration': ['p(95)<200'], 
    // Fail the test if more than 1% of requests fail
    'http_req_failed': ['rate<0.01'],
  },
};

// 3. THE DEFAULT FUNCTION (Executed repeatedly by every VU)
export default function () {
  const url = 'http://host.docker.internal:8081/api/v1/chat';

  // Build payload or headers
  const payload = JSON.stringify({
    message: `Hello from VU ${__VU} iteration ${__ITER}`,
    mode: 'native'
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'X-Forwarded-For': `192.168.1.${__VU}` // Simulating unique IPs
    },
  };

  // Execute the HTTP request (supports get, post, put, del, etc.)
  const res = http.post(url, payload, params);

  // 4. VALIDATE responses using checks
  check(res, {
    'status is 200': (r) => r.status === 200,
    'response body contains reply': (r) => r.body.includes('reply'),
  });

  // Pause for 1 second between loop iterations to simulate real user behavior
  sleep(1);
}