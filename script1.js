import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
  vus: 5,          // 5 virtual users
  duration: '10s', // for 10 seconds
};
//Get-Content script.js | docker run --rm -i grafana/k6 run -
export default function () {
  http.get('http://host.docker.internal:8081/api/v1/chat'); // Use host.docker.internal if your API runs on your host machine
  sleep(1);
}