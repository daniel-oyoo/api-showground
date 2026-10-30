import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
  vus: 5,          // 5 Virtual Users
  duration: '10s', // Run for 10 seconds
};

export default function () {
  const url = 'http://host.docker.internal:8081/api/v1/chat';

  // 1. Generate fully unique data purely using JavaScript logic
  const randomString = Math.random().toString(36).substring(2, 8);
  const uniqueMessage = `Test message from VU-${__VU} loop-${__ITER} [${randomString}]`;
  
  const payload = JSON.stringify({
    userId: __VU,                             // Unique per Virtual User
    sequence: __ITER,                         // Increments every loop for the same VU
    message: uniqueMessage,                   // Random unique string
    timestamp: Date.now()                     // Exact millisecond timestamp
  });

  const params = {
    headers: { 'Content-Type': 'application/json' },
  };

  // 2. Send the request
  const res = http.post(url, payload, params);

  // 3. Validate response
  check(res, {
    'is status 200': (r) => r.status === 200,
  });

  sleep(1);
}